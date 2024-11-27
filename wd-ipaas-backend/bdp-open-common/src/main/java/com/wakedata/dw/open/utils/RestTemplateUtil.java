package com.wakedata.dw.open.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.*;
import java.util.Map;

/**
 * @author wujunqiang
 * @since 2022/10/25 20:29
 */
@Slf4j
@Component
public class RestTemplateUtil {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${spring.mvc.backend.api.prefix}")
    private String apiGatewayPath;

    @Resource(name = DwOpenConstant.REST_TEMPLATE_CUSTOMER)
    private RestTemplate restTemplate;

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;

    /**
     * API请求URL，依次填充：ipaas域名、context-path、${spring.mvc.backend.api.prefix}、默认前缀、/、API方法、accessToken、timestamp
     */
    private static final String REQUEST_URL_TEMPLATE = "%s%s%s%s%s%s?accessToken=%s&timestamp=%s&sign=%s";

    /**
     * 根据API方法设置的url构建完整访问API的请求URL
     *
     * @param dataAssetApiMethod API方法Path
     * @param accessToken        accessToken
     * @param timestamp          时间戳字符串
     * @return 请求URL
     */
    public String buildRequestUrl(String dataAssetApiMethod, String accessToken, String timestamp) {
        // 如果domainName末尾是/并且contextPath开头是/，要将拼起来的//转换为/
        String transformContextPath;
        if (dwOpenCommonConfig.getDomainName().endsWith(StrUtil.SLASH) && contextPath.startsWith(StrUtil.SLASH)) {
            transformContextPath = contextPath.substring(1);
        } else {
            transformContextPath = contextPath;
        }
        return String.format(REQUEST_URL_TEMPLATE, dwOpenCommonConfig.getIpaasDomain(),
                transformContextPath, apiGatewayPath, StrUtil.SLASH + DwOpenConstant.OPEN_API_PREFIX, StrUtil.SLASH, dataAssetApiMethod, accessToken, timestamp, IdUtil.fastSimpleUUID());
    }

    /**
     * 根据API方法设置的url、请求参数构建完整访问API的请求URL（GET请求用这个方法将QUERY参数拼接在这里）
     *
     * @param dataAssetApiMethod API方法Path
     * @param accessToken        accessToken
     * @param timestamp          时间戳字符串
     * @param params             QUERY请求参数JSONObject
     * @return 请求URL
     */
    public String buildRequestUrl(String dataAssetApiMethod, String accessToken, String timestamp, JSONObject params) {
        String baseUrl = buildRequestUrl(dataAssetApiMethod, accessToken, timestamp);
        if (params == null || params.size() == 0) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        for (Map.Entry<String, Object> mapEntry : params.entrySet()) {
            sb.append("&").append(mapEntry.getKey()).append("=").append(mapEntry.getValue());
        }
        return sb.toString();
    }

    /**
     * get请求，返回响应实体（响应业务对象不支持范型）
     * 支持restful风格
     *
     * @param url          请求URL
     * @param headers      请求头参数Map
     * @param uriVariables PathVariable参数，没有不传
     * @return 响应体参数
     */
    public String get(String url, Map<String, String> headers, Object... uriVariables) {
        ResponseEntity<String> rsp = commonExchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders(headers)), uriVariables);
        return rsp.getBody();
    }

    /**
     * post请求，form表单提交（响应业务对象不支持范型）
     *
     * @param url          请求URL
     * @param headers      请求头参数Map
     * @param paramMap     form参数Map
     * @param uriVariables PathVariable参数，没有不传
     * @return 响应体参数
     */
    public String postByFrom(String url, Map<String, String> headers, Map<String, Object> paramMap, Object... uriVariables) {
        //指定请求头为表单类型
        HttpHeaders httpHeaders = createHeaders(headers);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<String> rsp = commonExchange(url, HttpMethod.POST, new HttpEntity<>(createBody(paramMap), httpHeaders), uriVariables);
        return rsp.getBody();
    }

    /**
     * post请求，form表单提交（响应业务对象支持范型）
     * 支持restful风格
     *
     * @param url          请求URL
     * @param headers      请求头参数Map
     * @param paramMap     form参数Map
     * @param responseType 响应类型
     * @param uriVariables PathVariable参数，没有不传
     * @param <T>          响应业务对象
     * @return 响应业务对象
     */
    public <T> T postByFrom(String url, Map<String, String> headers, Map<String, Object> paramMap, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        //指定请求头为表单类型
        HttpHeaders httpHeaders = createHeaders(headers);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<T> rsp = commonExchange(url, HttpMethod.POST, new HttpEntity<>(createBody(paramMap), httpHeaders), responseType, uriVariables);
        return buildResponse(rsp);
    }

    /**
     * post请求，json提交（响应业务对象不支持范型）
     * 支持restful风格
     *
     * @param url          请求URL
     * @param headers      请求头参数Map
     * @param request      请求体参数
     * @param uriVariables PathVariable参数，没有不传
     * @return 响应体参数
     */
    public String postByJson(String url, Map<String, String> headers, Object request, Object... uriVariables) {
        //指定请求头为json类型
        HttpHeaders httpHeaders = createHeaders(headers);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> rsp = commonExchange(url, HttpMethod.POST, new HttpEntity<>(request, httpHeaders), uriVariables);
        return rsp.getBody();
    }

    /**
     * 公共http请求方法（响应业务对象不支持范型）
     *
     * @param url           请求URL
     * @param method        请求方法类型
     * @param requestEntity HttpEntity
     * @param uriVariables  请求参数，没有不传
     * @return ResponseEntity
     */
    public ResponseEntity<String> commonExchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Object... uriVariables) {
        return restTemplate.exchange(url, method, requestEntity, new ParameterizedTypeReference<String>() {
        }, uriVariables);
    }

    /**
     * 公共http请求方法（响应业务对象支持范型）
     *
     * @param url           请求URL
     * @param method        请求方法类型
     * @param requestEntity HttpEntity
     * @param responseType  响应类型
     * @param uriVariables  请求参数，没有不传
     * @param <T>           响应类型对象
     * @return ResponseEntity
     */
    public <T> ResponseEntity<T> commonExchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    /**
     * 封装头部参数
     *
     * @param headers 请求头参数Map
     * @return HttpHeaders
     */
    private HttpHeaders createHeaders(Map<String, String> headers) {
        return new HttpHeaders() {{
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(this::set);
            }
        }};
    }

    /**
     * 封装请求体
     *
     * @param paramMap 请求参数map
     * @return MultiValueMap
     */
    private MultiValueMap<String, Object> createBody(Map<String, Object> paramMap) {
        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        if (paramMap != null && !paramMap.isEmpty()) {
            paramMap.forEach(valueMap::add);
        }
        return valueMap;
    }

    /**
     * 返回响应对象
     *
     * @param rsp ResponseEntity
     * @param <T> 响应类型对象
     * @return 响应类型对象
     */
    private <T> T buildResponse(ResponseEntity<T> rsp) {
        if (!rsp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(rsp.getStatusCode().getReasonPhrase());
        }
        return rsp.getBody();
    }

    /**
     * Get请求
     *
     * @param apiUrl            访问路径
     * @param requestData       请求参数
     * @param responseBeanClass 返回bean的类型
     * @param restTemplate      注入的RestTemplate
     * @param <T>
     * @param <R>
     * @return
     */
    @Deprecated
    public static <T, R extends Object> T requestGet(String apiUrl, R requestData, Class<T> responseBeanClass, RestTemplate restTemplate) {
        return restTemplate.getForObject(apiUrl, responseBeanClass, requestData);
    }

    /**
     * Post请求
     *
     * @param apiUrl
     * @param requestData
     * @param responseBeanClass
     * @param restTemplate
     * @param <T>
     * @param <R>
     * @return 返回Bean
     */
    @Deprecated
    public static <T, R> T requestBeanPost(String apiUrl, R requestData, Class<T> responseBeanClass, RestTemplate restTemplate) {
        return requestMethodBodyBean(apiUrl, requestData, responseBeanClass, restTemplate, HttpMethod.POST);
    }

    /**
     * Get请求  无参
     *
     * @param apiUrl
     * @param responseBeanClass
     * @param restTemplate
     * @param <T>
     * @return 返回bean
     */
    @Deprecated
    public static <T> T requestGet(String apiUrl, Class<T> responseBeanClass, RestTemplate restTemplate) {
        return restTemplate.getForObject(apiUrl, responseBeanClass);
    }

    /**
     * Post请求 无参
     *
     * @param apiUrl
     * @param responseBeanClass
     * @param restTemplate
     * @param <T>
     * @return 返回bean
     */
    @Deprecated
    public static <T> T requestBeanPost(String apiUrl, Class<T> responseBeanClass, RestTemplate restTemplate) {
        return requestMethodBodyBean(apiUrl, null, responseBeanClass, restTemplate, HttpMethod.POST);
    }

    /**
     * 抽取请求bean
     *
     * @param apiUrl
     * @param requestData
     * @param responseBeanClass
     * @param restTemplate
     * @param httpMethod
     * @param <T>
     * @param <R>
     * @return
     */
    @Deprecated
    protected static <T, R> T requestMethodBodyBean(String apiUrl, R requestData, Class<T> responseBeanClass, RestTemplate restTemplate, HttpMethod httpMethod) {
        Object responseData = requestMethodBody(apiUrl, requestData, restTemplate, httpMethod);
        if (responseData == null) {
            return null;
        }
        Map<String, Object> data = (Map<String, Object>) responseData;

        T responseInfoBean = null;
        try {
            responseInfoBean = BeanUtil.mapToBean(data, responseBeanClass, false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("类型转换异常，map投射Bean异常:{}", e);
            throw new ClassCastException("类型转换异常，map投射Bean异常");
        }
        return responseInfoBean;
    }

    /**
     * 抽取请求body
     *
     * @param apiUrl
     * @param requestData
     * @param restTemplate
     * @param httpMethod
     * @param <R>
     * @return
     */
    @Deprecated
    protected static <R> Object requestMethodBody(String apiUrl, R requestData, RestTemplate restTemplate, HttpMethod httpMethod) {
        RequestEntity<R> requestEntity = null;
        if (requestData == null) {
            requestEntity = new RequestEntity<>(httpMethod, URI.create(apiUrl));
        } else {
            requestEntity = new RequestEntity<>(requestData, httpMethod, URI.create(apiUrl));
        }

        ResponseEntity<Object> exchange = null;
        try {
            exchange = restTemplate.exchange(requestEntity, Object.class);
        } catch (HttpClientErrorException e) {
            e.getResponseBodyAsString();
            log.error("HttpClientErrorException:{}", e);
            throw new RestClientException(e.getResponseBodyAsString());
        } catch (RestClientException e) {
            e.printStackTrace();
            log.error("RestTemplate远程调用请求异常:{}", e);
            throw new RestClientException("RestTemplate远程调用请求异常");
        }

        if (exchange == null) {
            log.info("exchange == null");
            return null;
        } else if (exchange.getBody() == null) {
            log.info("exchange.getBody() == null");
            return null;
        }
        return exchange.getBody();
    }

}
