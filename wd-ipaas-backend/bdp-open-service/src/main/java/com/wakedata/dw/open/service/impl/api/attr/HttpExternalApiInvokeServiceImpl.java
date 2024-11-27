package com.wakedata.dw.open.service.impl.api.attr;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.webservice.SoapClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.authentication.ConnectorApiProcessor;
import com.wakedata.dw.open.authentication.ConnectorApiProcessorFactory;
import com.wakedata.dw.open.authentication.dto.ConnectorProcessorParamDTO;
import com.wakedata.dw.open.client.HttpContextHolder;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wakedata.dw.open.service.utils.RequestParamUtils.CONTENT_TYPE;
import static com.wakedata.dw.open.service.utils.RequestParamUtils.CONTENT_TYPE_X_WWW_FORM;

/**
 * @author ZhangXueJun
 * @title ExternalApiInvokeServiceImpl
 * @date 2021/3/1 16:11
 * @projectName dw-open
 * @description
 */
@Service
@Slf4j
public class HttpExternalApiInvokeServiceImpl implements ExternalApiInvokeService<Object> {

    @Resource
    private OpenApiDataCache openApiDataCache;
    private static final String PATH_SPIT = "/";
    @Resource(name = DwOpenConstant.REST_TEMPLATE_CUSTOMER)
    private RestTemplate restTemplate;
    @Resource
    private ConnectorApiProcessorFactory connectorApiProcessorFactory;

    @Override
    public Object queryHttpExternalApi(DataAssetApiPo dataAssetApi, JSONObject params) {

        HttpExternalApiAttr httpExternalApiAttr = getHttpExternalApiAttr(dataAssetApi);
        String urlTemplate = getUrlTemplate(
            httpExternalApiAttr.getHost(),
            httpExternalApiAttr.getPath(),
            dataAssetApi.getApiAttr(),
            params.getJSONObject(HttpParamKind.QUERY.name())
        );

        JSONObject jsonObject = new JSONObject();
        try {
            setHttpContextHolder(httpExternalApiAttr.getTimeout());
            HttpHeaders httpHeaders = RequestParamUtils.getHeaders(params.getJSONObject(HttpParamKind.HEAD.name()));

            ResponseEntity responseEntity = null;

            List<String> contentTypeHeaderList = httpHeaders.get(CONTENT_TYPE);
            if(!CollectionUtils.isEmpty(contentTypeHeaderList) && contentTypeHeaderList.contains(CONTENT_TYPE_X_WWW_FORM)){
                // 表单模式不支持数组形式传参
                MultiValueMap<String,Object> postParameters = new LinkedMultiValueMap<>();
                JSONObject bodyJsonObject = params.getJSONObject(HttpParamKind.BODY.name());
                if(null != bodyJsonObject){
                    for (Map.Entry<String, Object> mapEntry : bodyJsonObject.entrySet()) {
                        if (Objects.isNull(mapEntry.getValue())) {
                            continue;
                        }
                        postParameters.add(mapEntry.getKey(), mapEntry.getValue().toString());
                    }
                }

                responseEntity = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.valueOf(dataAssetApi.getReqMethod().name()),
                    new HttpEntity<>(
                        postParameters,
                        httpHeaders
                    ),
                    new ParameterizedTypeReference<String>() {
                    },

                    RequestParamUtils.getParams(params.getJSONObject(HttpParamKind.QUERY.name()))
                );
            } else {
                String jsonType = ParamMappingsUtils.isJsonObjectOrJsonArray(params.get(HttpParamKind.BODY.name()));
                responseEntity = restTemplate.exchange(
                    urlTemplate,
                    HttpMethod.valueOf(dataAssetApi.getReqMethod().name()),
                    new HttpEntity<>(DataTypeEnum.JSON_ARRAY.getType().equals(jsonType) ?
                            (params.getJSONArray(HttpParamKind.BODY.name()) != null ? params.getJSONArray(HttpParamKind.BODY.name()) : "")
                            : (params.getJSONObject(HttpParamKind.BODY.name()) != null ? params.getJSONObject(HttpParamKind.BODY.name()) : ""), httpHeaders),
                    new ParameterizedTypeReference<String>() {
                    },

                    RequestParamUtils.getParams(params.getJSONObject(HttpParamKind.QUERY.name()))
                );
            }
            jsonObject.put(HttpParamKind.BODY.name(),responseEntity.getBody());
            jsonObject.put(HttpParamKind.HEAD.name(),responseEntity.getHeaders());

        } catch (Exception e) {
            log.error("error url: {}", urlTemplate);
            if (e.getMessage().indexOf("Read timed out; nested exception is java.net.SocketTimeoutException: Read timed out") > 0) {
                throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_http_operator_timeout_error);
            }
            log.error(e.getMessage(), e);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_http_operator_error.getCode()
                    ,OpenApiMsgCodeEnum.w_api_graph_execute_http_operator_error.getDesc() + ",e = " + e.getMessage()
                    , e);
        }finally {
            HttpContextHolder.remove();
        }
        return jsonObject;
    }

    @Override
    public Object queryHttpConnectorApi(String secretKey, ConnectorApiPo connectorApi, JSONObject params, ConnectorApiAuthTypeEnum connectorApiAuthTypeEnum, Long environmentId) {
        JSONObject resultJsonObject = new JSONObject();
        ConnectorApiProcessor processor = connectorApiProcessorFactory.getConnectorApiProcessor(connectorApiAuthTypeEnum);
        ConnectorProcessorParamDTO paramDto = buildConnectorProcessorParamDTO(secretKey, connectorApi, params, connectorApiAuthTypeEnum, environmentId);
        ResponseEntity<?> responseEntity = processor.execute(paramDto);
        if (responseEntity == null) {
            log.error("request connector api fail, environment address: {} , url: {}", paramDto.getEnvironmentAddress().getEnvironmentAddress(), connectorApi.getApiMethod());
            throw new OpenException(OpenApiMsgCodeEnum.w_request_connector_api_fail);
        }
        resultJsonObject.put(HttpParamKind.BODY.name(), responseEntity.getBody());
        resultJsonObject.put(HttpParamKind.HEAD.name(), responseEntity.getHeaders());
        return resultJsonObject;
    }

    public HttpExternalApiAttr getHttpExternalApiAttr(DataAssetApiPo dataAssetApi) {
        return openApiDataCache.getHttpExternalApiAttr(dataAssetApi.getDataAssetApiId());
    }

    @Override
    public Object queryHttpWebServiceApi(DataAssetApiPo dataAssetApi, JSONObject params) {
        HttpExternalApiAttr httpExternalApiAttr = getHttpExternalApiAttr(dataAssetApi);
        //获取url
        String urlTemplate = getUrlTemplate(httpExternalApiAttr.getHost(), httpExternalApiAttr.getPath(), null, null);
        //设置soap连接
        SoapClient soapClient = SoapClient.create(urlTemplate)
                .setMethod(httpExternalApiAttr.getWsMethod(), httpExternalApiAttr.getWsNameSpaceUri())
                .setCharset(StandardCharsets.UTF_8);
        //构造请求参数
        JSONObject body = params.getJSONObject(HttpParamKind.BODY.name());
        if(CollectionUtil.isNotEmpty(body)){
            setWsMethodElement(soapClient.getMethodEle(),body);
        }
        log.info("生成的webservice 数据格式：\n{}",soapClient.getMsgStr(true));
        return soapClient.send(false);
    }

    @Override
    public Object queryHttpWebServiceWsdl(DataAssetApiPo dataAssetApi, JSONObject params) {
        HttpExternalApiAttr httpExternalApiAttr = getHttpExternalApiAttr(dataAssetApi);
        try {
            //获取url
            String urlTemplate = getUrlTemplate(httpExternalApiAttr.getHost(), httpExternalApiAttr.getPath() + DwOpenConstant.WEB_SERVICE_WSDL_SUFFIX, null, null);
//        RestTemplate restTemplate = restTemplate(httpExternalApiAttr.getTimeout());
            setHttpContextHolder(httpExternalApiAttr.getTimeout());
            return restTemplate.getForObject(urlTemplate, String.class);
        }finally {
            HttpContextHolder.remove();
        }
    }

    /**
     * 构建ConnectorProcessorParamDTO
     *
     * @param secretKey                连接器密钥唯一标识
     * @param connectorApi             第三方api信息表
     * @param params                   请求参数JSON
     * @param connectorApiAuthTypeEnum 连接器API鉴权类型枚举类
     * @return ConnectorProcessorParamDTO
     */
    private ConnectorProcessorParamDTO buildConnectorProcessorParamDTO(String secretKey, ConnectorApiPo connectorApi
            , JSONObject params, ConnectorApiAuthTypeEnum connectorApiAuthTypeEnum, Long environmentId) {
        ConnectorProcessorParamDTO dto = new ConnectorProcessorParamDTO();
        if (StringUtils.isNotBlank(secretKey)) {
            ConnectorSecretKeyPo connectorSecretKeyPo = openApiDataCache.getConnectorSecretKeyPo(secretKey);
            if (connectorSecretKeyPo == null) {
                OpenApiMsgCodeEnum notfoundCodeEnum = OpenApiMsgCodeEnum.w_notfound_connector_secret_key;
                throw new OpenException(notfoundCodeEnum.getCode(), String.format(notfoundCodeEnum.getDesc(), connectorApi.getApiName()));
            }
            dto.setConnectorSecretKey(connectorSecretKeyPo);
        }
        dto.setConnectorApi(connectorApi);
        dto.setParams(params);
        dto.setApiAuthTypeEnum(connectorApiAuthTypeEnum);
        Long queryEnvironmentId = dto.getConnectorSecretKey() == null ? environmentId : dto.getConnectorSecretKey().getEnvironmentId();
        ConnectorEnvironmentAddressPo environmentAddressPo = openApiDataCache.getConnectorEnvironmentAddressPo(queryEnvironmentId);
        if (environmentAddressPo == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_notfound_connector_environment_address);
        }
        dto.setEnvironmentAddress(environmentAddressPo);
        return dto;
    }

    /**
     * 设置ws方法参数
     * @param methodEle
     * @param body
     * @return
     */
    private void setWsMethodElement(SOAPBodyElement methodEle, JSONObject body){

        body.forEach((key,value)->{
            try {
                SOAPElement soapElement = methodEle.addChildElement(key);

                if(isJson(value)){
                    addSoapElement(soapElement,(JSONObject)value);
                    return;
                }
                if(isJsonArray(value)){
                    addSoapElement(soapElement,(JSONArray)value);
                    return;
                }
                soapElement.setValue(String.valueOf(value));
            } catch (SOAPException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 添加soap参数
     * @param soapElement
     * @param json
     * @return
     */
    private void addSoapElement(SOAPElement soapElement, JSONObject json){
        json.forEach((key,value)-> addSoapElement(soapElement,key,value));
    }

    private void addSoapElement(SOAPElement soapElement,String key,Object value){
        try {
            SOAPElement element = soapElement.addChildElement(key);
            if(isJson(value) || isJsonArray(value)){
                if(isJson(value)){
                    addSoapElement(element,(JSONObject)value);
                }else{
                    addSoapElement(element,(JSONArray) value);
                }
                return;
            }
            element.setValue(String.valueOf(value));
        } catch (SOAPException e) {
            e.printStackTrace();
        }
    }

    private void addSoapElement(SOAPElement soapElement, JSONArray json){
        Object first = json.get(0);
        if(isJson(first)){
            json.forEach(x-> addSoapElement(soapElement,(JSONObject)x));
        }else{
            soapElement.setValue(String.valueOf(json));
        }

    }

    private boolean isJson(Object value){
        return value instanceof JSONObject;
    }

    private boolean isJsonArray(Object value){
        return value instanceof JSONArray;
    }

    private String getUrlTemplate(String host, String path, AbstractApiAttr apiAttr, JSONObject params) {
        StringBuilder urlTemplate = new StringBuilder();
        urlTemplate.append(host);
        if (!host.endsWith(PATH_SPIT) && !path.startsWith(PATH_SPIT)) {
            urlTemplate.append(PATH_SPIT);
        }
        urlTemplate.append(path);
        if(params == null){
            return urlTemplate.toString();
        }
        int i = 0;
        for (Map.Entry<String, Object> mapEntry : params.entrySet()) {
            if (i == 0) {
                urlTemplate.append("?");
            } else {
                urlTemplate.append("&");
            }
            urlTemplate.append(mapEntry.getKey()).append("=").append("{").append(mapEntry.getKey()).append("}");
            i++;
        }
        return urlTemplate.toString();
    }

    public void setHttpContextHolder(Integer timeout){
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
        HttpContextHolder.set(requestConfig);
    }

//    public RestTemplate restTemplate(Integer timeout) {
//        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient(timeout)));
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        return restTemplate;
//    }

//    private HttpClient httpClient(Integer timeout) {
//        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
//                .build();
//
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
//        connectionManager.setMaxTotal(dwOpenCommonConfig.getMaxTotal());
//        connectionManager.setDefaultMaxPerRoute(dwOpenCommonConfig.getMaxPerRoute());
//        RequestConfig config = RequestConfig.custom()
//                .setSocketTimeout(timeout)
//                .setConnectionRequestTimeout(timeout)
//                .setConnectTimeout(timeout)
//                .build();
//
//        return HttpClientBuilder.create()
//                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))
//                .setConnectionReuseStrategy(new DefaultClientConnectionReuseStrategy())
//                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
//                .setConnectionManagerShared(true)
//                .setDefaultRequestConfig(config)
//                .setConnectionManager(connectionManager)
//                .build();
//    }
}