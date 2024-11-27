package com.wakedata.dw.open.swagger;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.SwaggerImportTypeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.ApiTagInfo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.external.http.HttpCode;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.swagger.utils.SwaggerDataUtils;
import com.wakedata.dw.open.utils.OkHttpClientUtils;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Swagger API详情
 *
 * @author chenshaopeng
 * @date 2021/10/28
 */
@Data
public class SwaggerApiDetail implements SwaggerConstants {

    /**
     * 默认超时时间
     */
    private final static int TIMEOUT = 3000;

    /**
     * swagger文档url
     */
    private String swaggerDocUrl;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 基础路径
     */
    private String basePath;

    /**
     * Api外部域名
     */
    private String apiDomainName;

    /**
     * swagger文档json数据
     */
    private String swaggerJson;

    /**
     * 导入方式
     */
    private SwaggerImportTypeEnum importType;

    /**
     * api详情列表
     */
    private List<ApiDetailVo> apiDetailList = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param swaggerDocUrl 远程数据地址
     * @param basePath      接口分组公共路径
     * @param swaggerJson   swagger文档json数据
     */
    public SwaggerApiDetail(String swaggerDocUrl, String basePath, String swaggerJson, SwaggerImportTypeEnum importType) {
        this.swaggerDocUrl = swaggerDocUrl;
        this.basePath = basePath;
        this.swaggerJson = swaggerJson;
        this.importType = importType;
    }


    /**
     * swagger文档url数据转换为apiDetailVo列表
     */
    @SuppressWarnings("unchecked")
    public void transition() throws Exception {
        // 获取文档数据
        String swaggerJson;
        if (SwaggerImportTypeEnum.URL == importType) {
            swaggerJson = Objects.requireNonNull(OkHttpClientUtils.get(swaggerDocUrl, new HashMap<>(16)).body()).string();
        } else {
            swaggerJson = this.swaggerJson;
        }

        LinkedTreeMap<String, Object> swaggerMap = AbstractReferResolve.jsonToMapConvert(URLUtil.decode(swaggerJson));
        this.checkSwaggerRequestResult(swaggerMap);

        // 选择一个定义的处理类
        AbstractReferResolve referResolve;
        if (swaggerMap.containsKey(VERSIONS_KEY)) {
            switch (SwaggerDataUtils.getString(swaggerMap, VERSIONS_KEY)) {
                case SwaggerV2ReferResolve.VERSIONS:
                    referResolve = new SwaggerV2ReferResolve();
                    break;
                case SwaggerV3ReferResolve.VERSIONS:
                    referResolve = new SwaggerV3ReferResolve();
                    break;
                default:
                    throw new OpenException(MsgCodeEnum.w_swagger_versions_not_support);
            }
        } else {
            switch (SwaggerDataUtils.getString(swaggerMap, OPEN_API_KEY)) {
                case OpenApiV3ReferResolve.VERSIONS_301:
                case OpenApiV3ReferResolve.VERSIONS_310:
                    referResolve = new OpenApiV3ReferResolve();
                    break;
                default:
                    throw new OpenException(MsgCodeEnum.w_open_api_versions_not_support);
            }
        }
        swaggerMap = referResolve.resolveObjectRefer(swaggerMap);

        transitionApiInfo(swaggerMap, (LinkedTreeMap<String, Object>) swaggerMap.get(SwaggerV2ReferResolve.PATHS_KEY), referResolve);
    }

    /**
     * 转换API信息列表
     *
     * @link com.wakedata.dw.open.service.vo.ApiDetailVo
     */
    @SuppressWarnings("unchecked")
    private void transitionApiInfo(LinkedTreeMap<String, Object> swaggerMap, LinkedTreeMap<String, Object> paths, AbstractReferResolve referResolve){
        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            LinkedTreeMap<String, Object> reqBody = (LinkedTreeMap<String, Object>) entry.getValue();
            for(DataAssetEnums.ReqMethod reqMethod : DataAssetEnums.ReqMethod.values()){
                if(!reqBody.containsKey(reqMethod.getValue().toLowerCase())){
                    continue;
                }
                reqBody= (LinkedTreeMap<String, Object>) reqBody.get(reqMethod.getValue().toLowerCase());

                ApiDetailVo apiDetailVo = new ApiDetailVo();
                apiDetailVo.setResults(new ArrayList<>());
                List<ApiConditionPo> apiParameters = getApiRequestBody(reqBody, getApiParameters(reqBody));
                apiDetailVo.setParameters(apiParameters);
                apiDetailVo.setDataAssetApi(getDataAssetApiPo(swaggerMap, reqBody, entry, reqMethod, referResolve));
                this.getApiDetailList().add(apiDetailVo);
            }
        }

        setAppName(SwaggerDataUtils.getString(swaggerMap, INFO_TITLE_KEY));
        setAppDesc(SwaggerDataUtils.getString(swaggerMap, INFO_DESC_KEY));
    }

    /**
     * 获取Api基础信息
     *
     * @link com.wakedata.dw.open.model.api.DataAssetApiPo
     */
    private DataAssetApiPo getDataAssetApiPo(LinkedTreeMap<String, Object> swaggerMap, LinkedTreeMap<String, Object> reqBody
            , Map.Entry<String, Object> entry, DataAssetEnums.ReqMethod reqMethod, AbstractReferResolve referResolve){
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();

        HttpExternalApiAttr apiAttr = getHttpExternalApiAttr(swaggerMap, entry, reqBody, referResolve);
        dataAssetApiPo.setApiAttr(apiAttr);

        // 请求协议
        DataAssetEnums.ReqProtocol protocol = DataAssetEnums.ReqProtocol.HTTPS;
        dataAssetApiPo.setProtocol(protocol);
        // 返回格式
        dataAssetApiPo.setResponseContentType(DataAssetEnums.ResContentType.JSON);
        // 请求方式
        dataAssetApiPo.setReqMethod(reqMethod);
        // API名称
        dataAssetApiPo.setApiName(SwaggerDataUtils.getString(reqBody, SUMMARY_KEY));
        // API描述
        String description = SwaggerDataUtils.getString(reqBody, DESCRIPTION_KEY);
        dataAssetApiPo.setApiDescription(Objects.nonNull(description) ? description : dataAssetApiPo.getApiName());
        // API Path，对超过4级以上的路径进行转换，防止无法调用API
        String dataAssetApiMethod = basePath + "/" + apiAttr.getPath();
        String transformDataAssetApiMethod = transformDataAssetApiMethod(dataAssetApiMethod);
        dataAssetApiPo.setDataAssetApiMethod(transformDataAssetApiMethod);
        // API创建时间
        dataAssetApiPo.setCreateTime(new Date());
        // 更新时间
        dataAssetApiPo.setUpdateTime(dataAssetApiPo.getCreateTime());
        // API类型
        dataAssetApiPo.setApiType(DataAssetEnums.DataApiType.EXTERNAL_HTTP);
        // 是否公开-加密
        dataAssetApiPo.setSecret(DataAssetEnums.PublicEnums.PRIVATE);
        // 更新频率
        dataAssetApiPo.setUpdateFrequency(DataAssetEnums.UpdateFrequency.DAY);
        // 更新频率
        dataAssetApiPo.setOperationType(DataAssetEnums.DataApiOperationType.INSERT);
        // API标签
        dataAssetApiPo.setApiTagInfo(getTagInfo(swaggerMap, reqBody));
        return dataAssetApiPo;
    }

    /**
     * 获取Http外部Api属性
     *
     * @link com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr
     */
    private HttpExternalApiAttr getHttpExternalApiAttr(LinkedTreeMap<String, Object> swaggerMap
            , Map.Entry<String, Object> entry, LinkedTreeMap<String, Object> reqBody, AbstractReferResolve referResolve){
        HttpExternalApiAttr apiAttr = new HttpExternalApiAttr();
        // 后台服务Host
        if(Objects.nonNull(apiDomainName)){
            apiAttr.setHost(apiDomainName);
        }else{
            apiAttr.setHost(SwaggerDataUtils.getString(swaggerMap, SERVICE_URL_KEY)
                    .startsWith("https") ? "https" : "http" + "://" + SwaggerDataUtils.getString(swaggerMap, HOST_KEY));
        }
        // 后台服务Path
        apiAttr.setPath(entry.getKey().replaceFirst("/", ""));
        // 超时时间
        apiAttr.setTimeout(TIMEOUT);
        // 错误码订阅
        apiAttr.setHttpCodes(getErrorHttpCode(reqBody));
        // 正常返回结果示例
        apiAttr.setResultExample(getResultExample(reqBody, ResultType.READABLE_JSON, referResolve));
        apiAttr.setResponseResult(getResultExample(reqBody, ResultType.RESOLVABLE_JSON, referResolve));
        apiAttr.setResponseJsonSchema(getResultExample(reqBody,ResultType.JSON_SCHEMA, referResolve));
        // 异常返回结果示例
        apiAttr.setErrorExample(getErrorExample());
        return apiAttr;
    }

    /**
     * 获取api的请求体参数（OpenApi 3.0会把请求体参数放在 requestBody.content.application/json 里面，swagger2.0没有这个结构）
     *
     * @return API请求参数集合
     */
    private List<ApiConditionPo> getApiRequestBody(LinkedTreeMap<String, Object> reqBody, List<ApiConditionPo> conditionPoList) {
        if (!reqBody.containsKey(REQUEST_BODY_KEY)) {
            return conditionPoList;
        }
        LinkedTreeMap<String, Object> param = (LinkedTreeMap<String, Object>) SwaggerDataUtils.getObject(reqBody, REQUEST_BODY_CONTENT_APPLICATION_JSON_KEY);
        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setAssetColumns(HttpParamKind.BODY.name().toLowerCase());
        // 参数类型
        String paramType = SwaggerDataUtils.getString(param, SCHEMA_TYPE_KEY);
        if (StrUtil.isEmpty(paramType)) {
            paramType = STRING;
        }
        // 参数类型转换
        paramType = convertParamType(paramType, HttpParamKind.BODY);
        apiConditionPo.setAssetDatatype(paramType);
        // 参数说明
        apiConditionPo.setDescriptions(SwaggerDataUtils.getString(param, DESCRIPTION_KEY));
        // 参数说明
        apiConditionPo.setDescriptions("请求体参数");
        // 赋值参数位置
        apiConditionPo.setHttpParamKind(HttpParamKind.BODY);
        apiConditionPo.setRequired(Boolean.FALSE);
        // 参数示例-默认值
        if (apiConditionPo.getAssetDatatype().equals(JSON_VAL)) {
            apiConditionPo.setJsonSchema(buildJsonSchema((LinkedTreeMap<String, Object>) param.get(SCHEMA_KEY)));
            apiConditionPo.setSample(
                    transitionJsonFormat(
                            (LinkedTreeMap<String, Object>) param.get(SCHEMA_KEY), ResultType.READABLE_JSON));
        }
        // 固定值
        apiConditionPo.setTypeAttr(DataAssetEnums.FiledTypeAttrEnums.OPERATOR);
        // 固定值
        apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
        conditionPoList.add(apiConditionPo);

        return conditionPoList;
    }

    /**
     * 获取Api请求参数列表
     *
     * @link com.wakedata.dw.open.model.api.ApiConditionPo
     */
    @SuppressWarnings("unchecked")
    private List<ApiConditionPo> getApiParameters(LinkedTreeMap<String, Object> reqBody){
        List<ApiConditionPo> parameters = new ArrayList<>();
        if(!reqBody.containsKey(PARAMS_KEY)){
            return parameters;
        }
        List<Object> params = SwaggerDataUtils.getList(reqBody, PARAMS_KEY);
        if(params == null || params.size() == 0){
            return parameters;
        }
        for (Object object : params){
            ApiConditionPo apiConditionPo = new ApiConditionPo();

            LinkedTreeMap<String, Object> param = (LinkedTreeMap<String, Object>) object;
            // 参数名称
            apiConditionPo.setAssetColumns(SwaggerDataUtils.getString(param, NAME_KEY));
            // 参数类型
            String paramType = SwaggerDataUtils.getString(param, SCHEMA_TYPE_KEY);
            if(StrUtil.isEmpty(paramType)){
                paramType = STRING;
            }
            // 获取参数位置
            String paramKind = SwaggerDataUtils.getString(param, IN_KEY);
            HttpParamKind httpParamKind = HttpParamKind.convert(paramKind);
            if(httpParamKind == null){
                httpParamKind = HttpParamKind.QUERY;
            }
            // 参数类型转换
            paramType = convertParamType(paramType, httpParamKind);
            apiConditionPo.setAssetDatatype(paramType);
            // 参数说明
            apiConditionPo.setDescriptions(SwaggerDataUtils.getString(param, DESCRIPTION_KEY));
            // 赋值参数位置
            apiConditionPo.setHttpParamKind(httpParamKind);
            // 是否必填
            Boolean required = URL_PATH_PARAM.equals(paramKind)?Boolean.TRUE:SwaggerDataUtils.getBoolean(param, REQUIRED_KEY);
            apiConditionPo.setRequired(Objects.nonNull(required) ? required : false);
            // 参数示例-默认值
            if(apiConditionPo.getAssetDatatype().equals(JSON_VAL)){
                apiConditionPo.setJsonSchema(buildJsonSchema((LinkedTreeMap<String, Object>) param.get(SCHEMA_KEY)));
                apiConditionPo.setSample(
                    transitionJsonFormat(
                        (LinkedTreeMap<String, Object>) param.get(SCHEMA_KEY), ResultType.READABLE_JSON));
            }
            // 固定值
            apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
            // 固定值
            apiConditionPo.setTypeAttr(DataAssetEnums.FiledTypeAttrEnums.OPERATOR);

            parameters.add(apiConditionPo);
        }
        return parameters;
    }

    /**
     * 参数类型转换
     * @param paramType 参数类型
     * @param httpParamKind 参数位置
     */
    private String convertParamType(String paramType, HttpParamKind httpParamKind){
        switch (paramType){
            case STRING:
                paramType = STRING;
                break;
            case OBJECT:
                paramType = JSON_VAL;
                break;
            case LONG:
                paramType = LONG;
                break;
            case DOUBLE:
                paramType = DOUBLE;
                break;
            case ARRAY:
                if (HttpParamKind.QUERY.equals(httpParamKind)) {
                    //存在query位置传入基本数据类型的数组的情况，经过测试可以使用String类型传参
                    paramType = STRING;
                }else {
                    paramType = JSON_VAL;
                }
                break;
            default:
                break;
        }
        return paramType;
    }
    /**
     * 获取标签信息
     *
     * @link com.wakedata.dw.open.model.swagger.TagInfo
     */
    private ApiTagInfo getTagInfo(LinkedTreeMap<String, Object> swaggerMap, LinkedTreeMap<String, Object> reqBody){
        List<String> tags = SwaggerDataUtils.getList(reqBody, TAGS_KEY)
                .stream().map(String::valueOf).collect(Collectors.toList());
        for(Object object : SwaggerDataUtils.getList(swaggerMap, TAGS_KEY)){
            String name = SwaggerDataUtils.getString(object, NAME_KEY);
            if(name.equals(tags.get(0))){
                String description = SwaggerDataUtils.getString(object, DESCRIPTION_KEY);
                return ApiTagInfo.builder().name(name.replaceAll(" ", ""))
                        .description(name.replaceAll(" ", ""))
                        .controller(StringUtils.isBlank(description) ? "" : description.replaceAll(" ", "")).build();
            }
        }
        return new ApiTagInfo();
    }

    /**
     * 获取结果示例
     */
    @SuppressWarnings("unchecked")
    private String getResultExample(LinkedTreeMap<String, Object> reqBody, ResultType resultType, AbstractReferResolve referResolve) {
        LinkedTreeMap<String, Object> responseResult =
                (LinkedTreeMap<String, Object>) SwaggerDataUtils.getObject(reqBody, referResolve.getNormalResponsesKey());
        return transitionJsonFormat((LinkedTreeMap<String, Object>) responseResult.get(SCHEMA_KEY), resultType);
    }

    /**
     * 转换为Json-schema可读格式
     */
    @SuppressWarnings("unchecked")
    private String buildJsonSchema(LinkedTreeMap<String, Object> schema) {
        if (ObjectUtil.isEmpty(schema)) {
            return null;
        }
        // 补充JsonSchema头节点名称
        schema.put(NAME_KEY,FIRST_NAME);
        // 补充JsonSchema节点名称
        addParamNameOfSchema(schema,FIRST_NAME);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return String.format(SCHEMA_ROOT,gson.toJson(schema));
    }

    /**
     * jsonSchema递归新增字段【"name"："paramKey"】
     *
     * @param schema schema
     */
    @SuppressWarnings("unchecked")
    private void addParamNameOfSchema(LinkedTreeMap<String, Object> schema,String parentKey) {
        LinkedTreeMap<String, Object> map;

        // 处理Object
        if (ObjectUtil.equal(OBJECT,schema.get(TYPE_KEY))) {
            LinkedTreeMap<String, Object> paramsLinkedTreeMap = (LinkedTreeMap<String, Object>) schema.get(PROPERTIES_KEY);
            if (CollectionUtil.isEmpty(paramsLinkedTreeMap)){
                return;
            }

            for (Map.Entry<String, Object> stringObjectEntry : paramsLinkedTreeMap.entrySet()) {
                String key = stringObjectEntry.getKey();
                map = (LinkedTreeMap<String, Object>) stringObjectEntry.getValue();
                map.put(NAME_KEY,key);

                if (ObjectUtil.equal(OBJECT,map.get(TYPE_KEY)) || ObjectUtil.equal(ARRAY,map.get(TYPE_KEY))){
                    addParamNameOfSchema(map,key);
                }
            }
        }

        // 处理array(type,items)
        if (ObjectUtil.equal(ARRAY,schema.get(TYPE_KEY))) {
            map = schema;
            map.put(NAME_KEY, parentKey);  // type items name

            LinkedTreeMap<String, Object> objectLinkedTreeMap = (LinkedTreeMap<String, Object>) map.get(ITEMS_KEY);
            objectLinkedTreeMap.put(NAME_KEY,ITEMS_KEY);
            addParamNameOfSchema(objectLinkedTreeMap,parentKey);
        }
    }

    /**
     * 转换为json可读格式
     */
    @SuppressWarnings("unchecked")
    private String transitionJsonFormat(LinkedTreeMap<String, Object> schema, ResultType resultType){
        if(schema != null){
            LinkedTreeMap<String, Object> properties = schema.containsKey(PROPERTIES_KEY)
                    ? (LinkedTreeMap<String, Object>) schema.get(PROPERTIES_KEY) : new LinkedTreeMap<>();

            JSONObject json = new JSONObject(true);
            //可读json
            if (resultType == ResultType.READABLE_JSON) {
                json.putAll(transitionProperties(properties));

                // 序列化为可读格式
                return JSON.toJSONString(json, SerializerFeature.PrettyFormat
                    , SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
            }
            // 可解析JsonSchema
            if (resultType == ResultType.JSON_SCHEMA){
                return buildJsonSchema(schema);
            }
            //可解析json
            return JSON.toJSONString(transformProperties(properties));
        }
        return "";
    }

    /**
     * 转换属性值并增加默认值
     */
    @SuppressWarnings("unchecked")
    private LinkedTreeMap<String, Object> transitionProperties(LinkedTreeMap<String, Object> properties){
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        if(properties == null || properties.size() < 1){
            return result;
        }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            Map<String, Object> value = (Map<String, Object>) entry.getValue();

            if (Objects.isNull(value.get(TYPE_KEY))) {
                continue;
            }
            switch ((String) value.get(TYPE_KEY)) {
                case STRING:
                    String format = (String) value.get(FORMAT_KEY);
                    if (DATE_TIME.equals(format) || DATETIME.equals(format)) {
                        result.put(entry.getKey(), DateUtil.now());
                        break;
                    }
                    result.put(entry.getKey(), STRING);
                    break;
                case INTEGER:
                    result.put(entry.getKey(), 0);
                    break;
                case NUMBER:
                    result.put(entry.getKey(), 0.0);
                    break;
                case BOOLEAN:
                    result.put(entry.getKey(), true);
                    break;
                case ARRAY:
                    List<Object> list = new ArrayList<>();
                    // 递归逻辑
                    list.add(transitionProperties((LinkedTreeMap<String, Object>) SwaggerDataUtils
                            .getObject(entry.getValue(), ITEMS_PROPERTIES_KEY)));
                    result.put(entry.getKey(), list);
                    break;
                case OBJECT:
                    LinkedTreeMap<String, Object> object = transitionProperties((LinkedTreeMap<String, Object>)
                            SwaggerDataUtils.getObject(entry.getValue(), PROPERTIES_KEY));
                    if (object.size() > 0) {
                        result.put(entry.getKey(), object);
                        break;
                    }
                default:
                    result.put(entry.getKey(), "{}");
                    break;
            }
        }
        return result;
    }

    /**
     * 转换属性
     *
     * @param properties 属性
     * @return map
     */
    @SuppressWarnings("unchecked")
    private LinkedTreeMap<String, Object> transformProperties(LinkedTreeMap<String, Object> properties) {
        LinkedTreeMap<String, Object> result = new LinkedTreeMap<>();
        if (properties == null || properties.size() < 1) {
            return result;
        }
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            Map<String, Object> value = (Map<String, Object>) entry.getValue();

            //空类型默认为对象类型
            if (Objects.isNull(value.get(TYPE_KEY))) {
                value.put(TYPE_KEY, StringUtils.EMPTY);
            }
            Object description = ObjectUtils.defaultIfNull(value.get(DESCRIPTION_KEY), StringUtils.EMPTY);
            String type = (String) value.get(TYPE_KEY);
            switch (type) {
                case STRING:
                case INTEGER:
                case NUMBER:
                case BOOLEAN:
                    result.put(entry.getKey(), type + "," + description.toString());
                    break;
                case ARRAY:
                    List<Object> list = new ArrayList<>();
                    // 递归逻辑
                    list.add(transformProperties((LinkedTreeMap<String, Object>) SwaggerDataUtils
                        .getObject(entry.getValue(), ITEMS_PROPERTIES_KEY)));
                    result.put(entry.getKey(), list);
                    break;
                case OBJECT:
                    LinkedTreeMap<String, Object> object = transformProperties((LinkedTreeMap<String, Object>)
                        SwaggerDataUtils.getObject(entry.getValue(), PROPERTIES_KEY));
                    if (object.size() > 0) {
                        result.put(entry.getKey(), object);
                        break;
                    }
                default:
                    result.put(entry.getKey(), "{}");
                    break;
            }
        }
        return result;
    }

    /**
     * 获取异常示例
     */
    private String getErrorExample(){
        // swagger未提供异常示例，使用固定示例
        return "{\n" +
                "\t\"errorCode\":500,\n" +
                "\t\"errorMessage\":\"系统异常\",\n" +
                "\t\"success\":false\n" +
                "}";
    }

    /**
     * 获取错误码定义
     */
    @SuppressWarnings("unchecked")
    private List<HttpCode> getErrorHttpCode(LinkedTreeMap<String, Object> reqBody){
        LinkedTreeMap<String, Object> responseResult = (LinkedTreeMap<String, Object>)
                SwaggerDataUtils.getObject(reqBody, RESPONSES_KEY);

        List<HttpCode> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : responseResult.entrySet()){
            // 排除正常响应结果
            if(NORMAL_CODE_KEY.equalsIgnoreCase(entry.getKey())){
                continue;
            }
            HttpCode httpCode = new HttpCode();
            httpCode.setErrorCode(entry.getKey());
            httpCode.setErrorMsg(SwaggerDataUtils.getString(entry.getValue(), DESCRIPTION_KEY));
            result.add(httpCode);
        }
        return result;
    }

    /**
     * 检查Swagger数据地址的请求结果
     */
    private void checkSwaggerRequestResult(LinkedTreeMap<String, Object> swaggerMap) {
        if (MapUtil.isEmpty(swaggerMap) || (!swaggerMap.containsKey(VERSIONS_KEY) && !swaggerMap.containsKey(OPEN_API_KEY))) {
            throw new OpenException(MsgCodeEnum.w_swagger_data_query_fail);
        }
        if (swaggerMap.containsKey(ERROR_KEY)) {
            throw new OpenException(String.valueOf(swaggerMap.get(ERROR_KEY)));
        }
    }

    /**
     * 判断API Path路径层级是否超过4级，如果超过需要对后续的路径进行转换
     *
     * @param dataAssetApiMethod 原API Path
     * @return 转换后API Path
     */
    private String transformDataAssetApiMethod(String dataAssetApiMethod) {
        if (dataAssetApiMethod.split(StrUtil.SLASH).length <= DwOpenConstant.MAX_API_PATH_SLASH_SPLIT_COUNT) {
            return dataAssetApiMethod;
        }
        StringBuilder sb = new StringBuilder(dataAssetApiMethod);
        int stringBuilderLength = dataAssetApiMethod.split(StrUtil.SLASH).length;
        for (int i = stringBuilderLength; i > DwOpenConstant.MAX_API_PATH_SLASH_SPLIT_COUNT; i--) {
            sb.setCharAt(sb.lastIndexOf(StrUtil.SLASH), StrUtil.C_DOT);
        }
        return sb.toString();
    }

    private enum ResultType {
        /**
         * jsonSchema
         */
        JSON_SCHEMA,
        /**
         * 可读json
         */
        READABLE_JSON,
        /**
         * 可解析json
         */
        RESOLVABLE_JSON;
    }

}
