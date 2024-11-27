package com.wakedata.dw.open.service.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.util.JsonUtil;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.swagger.SwaggerConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.guava.Sets;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author ZhangXueJun
 * @title RequestParamUtils
 * @date 2021/3/29 16:27
 * @projectName bdp-open
 * @description
 */
@Slf4j
public class RequestParamUtils {

    public static final Set<String> DEFAULT_PARAMS = new HashSet();
    public static final Set<String> DEFAULT_PARAMS_NOT_PAGE_NO_SIZE = new HashSet<>();

    public static final String APP_KEY = "apiKey";
    public static final String APP_ID = "appId";
    public static final String SEQ_NO = "seqNo";
    public static final String TIME_STAMP = "timestamp";
    public static final String VERSION = "version";
    public static final String SIGN = "sign";
    public static final String PAGE_NO = "pageNo";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ORDER_BY = "orderBy";
    public static final String ENABLE_LOG = "__enable_log__";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_X_WWW_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String PRE_CONTENT_TYPE = "application/";

    /**
     * 添加固定的请求列
     */
    static {
        //初始化默认参数
        String[] paramArrays = new String[]{APP_KEY, APP_ID, SEQ_NO, TIME_STAMP, VERSION, SIGN, PAGE_NO, PAGE_SIZE, ACCESS_TOKEN};
        Collections.addAll(DEFAULT_PARAMS, paramArrays);
        //初始化去除了分页参数的默认参数
        String[] paramArraysSecond = new String[]{APP_KEY, APP_ID, SEQ_NO, TIME_STAMP, VERSION, SIGN, ACCESS_TOKEN};
        Collections.addAll(DEFAULT_PARAMS_NOT_PAGE_NO_SIZE, paramArraysSecond);
    }

    public static final String __SQL__ = "__sql__";

    /**
     * 移除默认参数
     *
     * @param params    请求参数JSONObject
     * @param paramKeys 需要额外移除的keys
     */
    public static void removeDefaultParams(JSONObject params, String... paramKeys) {
        for (String defaultParam : DEFAULT_PARAMS) {
            params.remove(defaultParam);
        }
        for (String paramKey : paramKeys) {
            params.remove(paramKey);
        }
    }

    /**
     * 获取除了固定的几个查询糁数外的其他请求参数
     *
     * @param request
     * @param dataAssetApi
     * @param apiConditions
     * @return
     */
    public static JSONObject requestDetails(
            HttpServletRequest request,
            DataAssetApiPo dataAssetApi,
            List<ApiConditionPo> apiConditions,String postData) {

        JSONObject requestDetails = new JSONObject();
        requestDetails.put(HttpParamKind.QUERY.name(), new JSONObject());
        requestDetails.put(HttpParamKind.HEAD.name(), new JSONObject());
        requestDetails.put(HttpParamKind.BODY.name(), StringUtils.EMPTY);
        //增加过滤参数，为了给删除和修改的时候，区别query用，这样做不会影响之前的功能
        requestDetails.put(HttpParamKind.FILTER.name(), new JSONObject());

        if (CollectionUtils.isEmpty(apiConditions)) {
            return requestDetails;
        }

        Set<ApiConditionPo> headerParams = Sets.newHashSet();
        Set<ApiConditionPo> queryParams = Sets.newHashSet();
        Set<ApiConditionPo> filterParams = Sets.newHashSet();
        ApiConditionPo body = null;

        for (ApiConditionPo apiCondition : apiConditions) {
            if (apiCondition.getType() != null && apiCondition.getType() != DataAssetEnums.FiledTypeEnums.PARAMETERS) {
                continue;
            }

            //过滤掉默认参数（不过滤分页参数）
            if (DEFAULT_PARAMS_NOT_PAGE_NO_SIZE.contains(apiCondition.getAssetColumns())) {
                continue;
            }

            if (apiCondition.getHttpParamKind() == null) {
                apiCondition.setHttpParamKind(HttpParamKind.QUERY);
            }

            switch (apiCondition.getHttpParamKind()) {
                case QUERY:
                    queryParams.add(apiCondition);
                    break;
                case FILTER:
                    filterParams.add(apiCondition);
                    break;
                case BODY:
                    body = apiCondition;
                    break;
                case HEAD:
                    headerParams.add(apiCondition);
                    break;
                default:
                    break;
            }
        }
        if (CollectionUtils.isNotEmpty(queryParams)) {
//            Map<String, Object> params = getParams(request, queryParams,false);
            Map<String, Object> params = getParamsByQuery(request, queryParams);
            if (MapUtils.isNotEmpty(params)) {
                requestDetails.put(HttpParamKind.QUERY.name(), new JSONObject(params));
            }
        }

        if (CollectionUtils.isNotEmpty(filterParams)) {
//            Map<String, Object> params = getParams(request, filterParams,true);
            Map<String, Object> params = getParamsByQuery(request, filterParams);
            if (MapUtils.isNotEmpty(params)) {
                requestDetails.put(HttpParamKind.FILTER.name(), new JSONObject(params));
            }
        }


        if (Objects.nonNull(request.getParameter(ENABLE_LOG))) {
            requestDetails.getJSONObject(HttpParamKind.QUERY.name()).put(ENABLE_LOG, request.getParameter(ENABLE_LOG));
        }

        switch (dataAssetApi.getApiType()) {
            case CUSTOM_SQL:
                requestDetails.put(__SQL__, dataAssetApi.getApiSql());
                if (body != null) {
                    String bodyContent = getBodyByJsonAndXml(request, body, postData);
                    requestDetails.put(HttpParamKind.BODY.name(), bodyContent);
                }
                break;
            case NORMAL_TABLE:
                if (body != null) {
                    String bodyContent = getBodyByJsonAndXml(request,body,postData);
                    requestDetails.put(HttpParamKind.BODY.name(), bodyContent);
                }
                break;
            case WEB_SERVICE:
            case EXTERNAL_HTTP:
            case LITE_FLOW:
                if (CollectionUtils.isNotEmpty(headerParams)) {
                    HttpHeaders httpHeaders = getHeaders(request, headerParams);
                    JSONObject results = new JSONObject();
                    for (Map.Entry<String, List<String>> mapEntry : httpHeaders.entrySet()) {
                        results.put(mapEntry.getKey(), URLUtil.decode(mapEntry.getValue().iterator().next()));
                    }
                    requestDetails.put(HttpParamKind.HEAD.name(), results);
                }

                if (body != null) {
//                    String bodyContent = getBody(request, body,postData);
                    String bodyContent = getBodyByJsonAndXml(request,body,postData);
                    requestDetails.put(HttpParamKind.BODY.name(), bodyContent);
                }
                break;
            default:
        }
        return requestDetails;
    }

    /**
     * 处理对外api请求接口中的query参数
     *
     * @param request 请求
     * @param params query参数(该api存储在表中且为非系统字段信息)
     * @return (query或filter)参数(key-value)
     */
    public static Map<String, Object> getParamsByQuery(HttpServletRequest request, Set<ApiConditionPo> params) {
        // 获取用户传参
        Map<String, Object> queryParams = new HashMap<>();
        for (ApiConditionPo apiConditionPo : params) {
            // 校验QUERY中的必填参数是否必填
            if (ObjectUtil.equal(apiConditionPo.getRequired(),Boolean.TRUE) && StringUtils.isBlank(request.getParameter(apiConditionPo.getAssetColumns()))){
                throw new OpenException(OpenApiMsgCodeEnum.w_app_api_params_required.getCode(),String.format(OpenApiMsgCodeEnum.w_app_api_params_required.getDesc(),HttpParamKind.QUERY.name(),apiConditionPo.getAssetColumns()));
            }

            // 过滤QUERY中的非必填参数为value为空的字段
            if (StringUtils.isBlank(request.getParameter(apiConditionPo.getAssetColumns()))){
                continue;
            }

            // 将QUERY参数放入Map中
            if (DwOpenConstant.EVENT_RECEIVE_QUERY_PARAM_NAME.equals(apiConditionPo.getAssetColumns())) {
                try {
                    // 只有eventReceiveParams参数要解码
                    queryParams.put(apiConditionPo.getAssetColumns(), URLDecoder.decode(request.getParameter(apiConditionPo.getAssetColumns()), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                queryParams.put(apiConditionPo.getAssetColumns(), request.getParameter(apiConditionPo.getAssetColumns()));
            }
        }
        if (MapUtil.isEmpty(queryParams)){
            return new HashMap<>();
        }
        return queryParams;
    }


    /**
     * 处理对外api请求接口中的body参数
     *
     * @param request 请求
     * @param postData body参数
     * @return json或XML(字符串)
     */
    public static String getBodyByJsonAndXml(HttpServletRequest request, ApiConditionPo body, String postData) {

        if (ObjectUtil.notEqual(body.getHttpParamKind(),HttpParamKind.BODY)){
            return null;
        }

        // post请求(JSON格式或XML格式)
        String contentType = request.getHeader(CONTENT_TYPE);
        if (StringUtils.isEmpty(contentType) || StringUtils.isEmpty(postData)){
            return null;
        }

        // xml转换成json
        if (Objects.equals(contentType,CONTENT_TYPE_XML)){
            String requestType = PRE_CONTENT_TYPE.concat(body.getAssetDatatype());
            String resultPostData = Objects.equals(requestType,DataAssetEnums.ResContentType.XML.getValue()) ? postData : toConvertJson(DocumentManagementUtils.deleteXmlLabel(postData),body.getJsonSchema());
            // 转换后的数据如果是JSON格式
            if (JsonUtil.isJson(resultPostData)) {
                JSONObject jsonPostData = JSONObject.parseObject(resultPostData);
                Set<String> keySet = jsonPostData.keySet();
                // JSON对象只有一个key并且这个key是“root”，说明这个请求体多了一层root包装，需要去掉
                if (keySet.size() == 1 && jsonPostData.containsKey(ParamMappingsUtils.SCHEMA_ROOT)){
                    // value值是JSONArray格式才需要去掉root包装
                    String jsonType = ParamMappingsUtils.isJsonObjectOrJsonArray(jsonPostData.get(ParamMappingsUtils.SCHEMA_ROOT));
                    if (DataTypeEnum.JSON_ARRAY.getType().equals(jsonType)) {
                        return jsonPostData.get(ParamMappingsUtils.SCHEMA_ROOT).toString();
                    }
                }
            }
            return resultPostData;
        }
        //默认使用Json格式 (已经暂时放开了这个必传的检测)
        toCheckRequired(body.getJsonSchema(),postData);
        //包含了Array串的统一替换成json格式做参数转换
        if (StringUtils.containsIgnoreCase(body.getAssetDatatype(), SwaggerConstants.ARRAY)){
            body.setAssetDatatype(DataAssetEnums.ResContentType.JSON.getValue());
        }
        String requestType = PRE_CONTENT_TYPE.concat(DataAssetEnums.AssetDataTypeEnum.JSON.getValue());
        return Objects.equals(requestType,DataAssetEnums.ResContentType.JSON.getValue()) ? postData : DocumentManagementUtils.addXmlLabel(toConvertXml(postData));
    }

    /**
     * 校验BODY参数是否为必填
     *
     * @param jsonSchema api定义参数格式
     * @param postData api请求所传参数
     */
    public static void toCheckRequired(String jsonSchema, String postData) {

        // TODO 后端暂时放开对jsonSchema中必填字段对校验
//        // 只校验json类型的传参
//        if (!JSONUtil.isJson(postData) || !JSONUtil.isJson(jsonSchema) ){
//            return;
//        }
//
//        List<String> schemaRequiredList = JsonUtils.JsonSchemaToRequiredColum(jsonSchema);
//        Map<String, Object> parameterJsomMap = JsonUtils.JsonToMap(postData);
//
//        // 校验必填参数
//        schemaRequiredList.stream().filter(colum -> Objects.isNull(parameterJsomMap.get(colum))).forEach(colum -> {
//            throw new OpenException(OpenApiMsgCodeEnum.w_app_api_params_required.getCode(),String.format(OpenApiMsgCodeEnum.w_app_api_params_required.getDesc(),HttpParamKind.BODY,colum));
//        });
    }

    /**
     * 将JSON格式转换为XML格式
     *
     * @param postData JSON格式(字符串)
     * @return XML格式(字符串)
     */
    private static String toConvertXml(String postData) {
        if (!JSONUtil.isJson(postData)){
            return null;
        }
        return JSONUtil.toXmlStr(JSONUtil.parse(postData));
    }

    /**
     * 将XML格式转换为JSON格式
     *
     * @param postData XML格式(字符串)
     * @param jsonSchema API定义的数据结构
     * @return JSON格式(字符串)
     */
    private static String toConvertJson(String postData, String jsonSchema) {

        List<String> arrayKeyList = JsonUtils.jsonSchemaType(jsonSchema, DataTypeEnum.ARRAY.getType());
        return JsonUtils.transformXML(postData, arrayKeyList);
    }

    /**
     * 获取请求头参数信息
     * @param request
     * @param headers
     * @return
     */
    public static HttpHeaders getHeaders(HttpServletRequest request, Set<ApiConditionPo> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (ApiConditionPo header : headers) {
            String value = request.getHeader(header.getAssetColumns());
            if (value == null) {
                continue;
            }
            httpHeaders.put(header.getAssetColumns(), Collections.singletonList(value));
        }
        return httpHeaders;
    }

    public static HttpHeaders getHeaders(JSONObject jsonObject) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (Objects.isNull(jsonObject)) {
            return httpHeaders;
        }

        for (Map.Entry<String, Object> mapEntry : jsonObject.entrySet()) {
            Object headValue = mapEntry.getValue();

            if (Objects.isNull(headValue)) {
                continue;
            }

            // 这里原本是取数组类型第一个值，但是实际测试登陆惟客云获取cookie会有问题，所以还是改成了取集合
            if ((headValue instanceof List) && CollectionUtils.isNotEmpty((Collection<?>) headValue)) {
                httpHeaders.put(mapEntry.getKey(), (List)headValue);
                continue;
            }

            httpHeaders.put(mapEntry.getKey(), Collections.singletonList(String.valueOf(headValue)));
        }
        return httpHeaders;
    }

    public static Map<String, Object> getParams(JSONObject jsonObject) {
        Map<String, Object> results = Maps.newHashMap();
        if (Objects.isNull(jsonObject)) {
            return results;
        }

        for (Map.Entry<String, Object> mapEntry : jsonObject.entrySet()) {
            if (Objects.isNull(mapEntry.getValue())) {
                continue;
            }

            results.put(mapEntry.getKey(), mapEntry.getValue());
        }
        return results;
    }

    /**
     * 获取body参数
     * @param request
     * @param postData
     * @param jsonSchema
     * @return
     */
    public static JSONObject getBodyParams(HttpServletRequest request, String postData, String jsonSchema) {
        String contentType = request.getHeader(CONTENT_TYPE);
        if(!DataAssetEnums.ResContentType.JSON.getValue().equals(contentType)){
            return null;
        }
        ApiRespParamDTO respParamDTO = JsonSchemaConvertUtil.convert(jsonSchema);
        JSONObject requestBody = JSONObject.parseObject(postData);
        if(ObjectUtil.isNull(requestBody) || CollUtil.isEmpty(respParamDTO.getChildApiRespParams())){
            return null;
        }
        JSONObject bodyParam = new JSONObject();
        for (ApiRespParamDTO x : respParamDTO.getChildApiRespParams()) {
            bodyParam.put(x.getAssetColumns(), JSONPath.eval(requestBody, IFunc.PRE_SPLIT + x.getAssetColumns()));
        }
        return bodyParam;
    }
}