package com.wakedata.dw.open.parammapping.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.*;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.function.FuncExecutor;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.input.PathMapping;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.parammapping.ResponseKind;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.noear.snack.ONode;

import java.math.BigDecimal;
import java.util.*;


/**
 * @author ZhangXueJun
 * @title JsonUtils
 * @date 2021/5/14 15:38
 * @projectName dw-open
 * @description
 */
public class ParamMappingsUtils {

    public static final String EXPRESSION_DATA_TYPE = "dataType";

    public static final String EXPRESSION_IS_JSON = "expressionIsJson";

    public static final String EXPRESSION_VALUE = "value";

    public static final String CONVERT_TO_DATA_TYPE = "$.root.dataType";

    /**
     * 请求参数体的根节点名称
     */
    public static final String SCHEMA_ROOT = "root";

    public static class MappingFieldResults {
        private ResponseKind responseKind;
        private Map<String, Object> fieldValues;
        private int maxRows;

        public static final MappingFieldResults getDefault() {
            return MappingFieldResults.getInstance(ResponseKind.JSON_OBJECT, Maps.newLinkedHashMap(), 0);
        }

        public static MappingFieldResults getInstance(ResponseKind responseKind, Map<String, Object> fieldValues, int maxRows) {
            return new MappingFieldResults(responseKind, fieldValues, maxRows);
        }

        private MappingFieldResults(ResponseKind responseKind, Map<String, Object> fieldValues, int maxRows) {
            this.responseKind = responseKind;
            this.fieldValues = fieldValues;
            this.maxRows = maxRows;
        }

        public void setResponseKind(ResponseKind responseKind) {
            this.responseKind = responseKind;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public ResponseKind getResponseKind() {
            return responseKind;
        }

        public Map<String, Object> getFieldValues() {
            return fieldValues;
        }

        public int getMaxRows() {
            return maxRows;
        }
    }

    /**
     * 根据参数映射，获取单个JSON映射后结果
     * 考虑兼容JSONObject JSONArray
     *
     * @param originJson
     * @param paramMappings
     * @return
     */
    public static JSON mappingJson(JSON originJson, List<? extends AbstractParamMapping> paramMappings) {
        if (originJson == null) {
            return new JSONArray();
        }

        MappingFieldResults mappingFieldResults = alignFieldValues(originJson, paramMappings);
        return alignMultiJsonArray(mappingFieldResults);
    }

    public static MappingFieldResults alignFieldValues(JSON originJson, List<? extends AbstractParamMapping> paramMappings) {
        ResponseKind responseKind = ResponseKind.JSON_OBJECT;
        Map<String, Object> fieldValues = Maps.newLinkedHashMap();

        // 获取最大行数
        int maxRows = 0;
        for (AbstractParamMapping paramMapping : paramMappings) {
            // 判断AbstractParamMapping设置的是固定值还是引用值，如果是引用值使用jsonPath解析参数
            if (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(paramMapping.getType())) {
                fieldValues.put(paramMapping.getField(), paramMapping.getFixedValue());
                continue;
            }
            if (paramMapping.getHttpParamKind() == HttpParamKind.FILTER) {
                JSONObject filterObject = (JSONObject) JSONPath.eval(originJson, "$.FILTER");
                getValue(filterObject, paramMapping, responseKind, maxRows, fieldValues, HttpParamKind.FILTER);
            } else {
                getValue(originJson, paramMapping, responseKind, maxRows, fieldValues, paramMapping.getHttpParamKind());
            }
        }
        return MappingFieldResults.getInstance(responseKind, fieldValues, maxRows);
    }

    public static MappingFieldResults alignFieldValues(Map<String, JSON> multiInputMappings, List<? extends AbstractParamMapping> paramMappings) {
        ResponseKind responseKind = ResponseKind.JSON_OBJECT;
        Map<String, Object> fieldValues = Maps.newLinkedHashMap();

        // 获取最大行数
        int maxRows = 0;
        for (AbstractParamMapping paramMapping : paramMappings) {
            // 判断AbstractParamMapping设置的是固定值还是引用值，如果是引用值使用jsonPath解析参数
            if (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(paramMapping.getType())) {
                fieldValues.put(paramMapping.getField(), convertValueType(paramMapping, paramMapping.getFixedValue(),null));
                continue;
            }
            Boolean expressionIsJson = paramMapping.getExpressionIsJson();
            if (expressionIsJson == null || !expressionIsJson) {
                JSON originJson = ParamMappingsUtils.multiInputMappingsConvert(multiInputMappings);
                getValue(originJson, paramMapping, responseKind, maxRows, fieldValues, paramMapping.getHttpParamKind());
            } else {
                getExpressionJsonValue(multiInputMappings, paramMapping, fieldValues, paramMapping.getHttpParamKind());
            }
        }
        return MappingFieldResults.getInstance(responseKind, fieldValues, maxRows);
    }

    private static void getValue(JSON originJson, AbstractParamMapping paramMapping, ResponseKind responseKind, int maxRows, Map<String, Object> fieldValues, HttpParamKind httpParamKind) {
        Object object = null;
        //解析函数
        if (ParamMappingTypeEnum.FUNCTION_TYPE.getType().equals(paramMapping.getType())) {
            ONode ctxNode = ONode.load(new HashMap());
            PathMapping.setByPath(ctxNode, IFunc.SEPARATOR_ASTERISK, originJson, false);
            object = FuncExecutor.getInstance().exec(ctxNode, paramMapping.getExpression());

        } else {
            // 支持json 参数映射
            object = new JsonPathHelper(paramMapping.getExpression(), originJson).getResult();

            // 注释该代码 处理返回参数模版时 第一层字段的值为空时不显示的问题
//            if (object == null) {
//                return;
//            }
            if (object instanceof List) {
                responseKind = ResponseKind.JSON_ARRAY;
                int currSize = ((JSONArray) object).size();
                if (currSize > maxRows) {
                    maxRows = currSize;
                }
            }
        }

        // 将JsonPath匹配到的数据 转换为用户所设置的参数类型
        object = convertValueType(paramMapping, object, null);
        fieldValues.put(paramMapping.getField(), object);
    }

    /**
     * multiInputMappings参数转换
     * @param multiInputMappings
     * @return
     */
    public static JSON multiInputMappingsConvert(Map<String, JSON> multiInputMappings) {
        JSONObject mappings = new JSONObject();
        mappings.putAll(multiInputMappings);
        return mappings;
    }

    /**
     * 从表达式对象中获取参数映射值
     *
     * @param multiInputMappings 算子返回结果Map
     * @param paramMapping       参数映射
     * @param fieldValues        字段map
     */
    private static void getExpressionJsonValue(Map<String, JSON> multiInputMappings, AbstractParamMapping paramMapping, Map<String, Object> fieldValues, HttpParamKind httpParamKind) {
        Map<String,String> filedDataType = new HashMap<>();

        // 以后可能要对函数进行处理，参考上面的getValue方法
        Object object = new JsonPathHelper(paramMapping.getExpression(), multiInputMappings).getResult();
        if (object == null) {
            return;
        }

        // 将JsonPath匹配到的数据 转换为用户所设置的参数类型
        filedDataType.put(paramMapping.getField(),paramMapping.getDataType());
        //表达式内容对象
        JSONObject expressionJsonObject = JSONObject.parseObject(paramMapping.getExpression());
        convertType(expressionJsonObject,filedDataType,paramMapping.getField());
        object = convertValueType(paramMapping, object, filedDataType);

        if (httpParamKind == HttpParamKind.FILTER) {
            fieldValues.put("FILTER_" + paramMapping.getField(), object);
        } else {
            fieldValues.put(paramMapping.getField(), object);
        }
    }

    /**
     * 对象转换
     *
     * @param mappingFieldResults
     * @return
     */
    public static JSON alignMultiJsonArray(MappingFieldResults mappingFieldResults) {

        switch (mappingFieldResults.responseKind) {
            case JSON_OBJECT:
                return new JSONObject(mappingFieldResults.fieldValues);
            default:
                Map<String, List<Object>> groupColumnDatas = Maps.newLinkedHashMap();

                for (Map.Entry<String, Object> mapEntry : mappingFieldResults.fieldValues.entrySet()) {
                    String fieldName = mapEntry.getKey();
                    Object values = mapEntry.getValue();

                    // 补齐行数
                    List<Object> alignValues = groupColumnDatas.get(fieldName);
                    if (CollectionUtils.isEmpty(alignValues)) {
                        alignValues = Lists.newArrayList();
                        groupColumnDatas.put(fieldName, alignValues);
                    }

                    if (values instanceof List) {
                        List<Object> arr = (List<Object>) values;
                        if (arr.size() < mappingFieldResults.maxRows) {
                            for (int i = 0; i < mappingFieldResults.maxRows - arr.size(); i++) {
                                arr.add(null);
                            }
                        }
                        alignValues = arr;
                    } else {
                        for (int i = 0; i < mappingFieldResults.maxRows; i++) {
                            alignValues.add(values);
                        }
                    }
                    groupColumnDatas.put(fieldName, alignValues);
                }

                JSONArray resultArr = new JSONArray();
                for (int i = 0; i < mappingFieldResults.maxRows; i++) {
                    JSONObject rowData = new JSONObject();
                    for (Map.Entry<String, List<Object>> mapEntry : groupColumnDatas.entrySet()) {
                        rowData.put(mapEntry.getKey(), mapEntry.getValue().get(i));
                    }
                    resultArr.add(rowData);
                }
                return resultArr;
        }
    }

    /**
     * 格式化输出
     *
     * @param json
     * @return
     */
    public static String prettyFormatJson(JSON json) {
        return JSON.toJSONString(
                json,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat
        );
    }

    /**
     * 转换参数类型
     *
     * @param object 转换前的object(value)
     * @param map Map<filed,dataType>
     * @return 转换后的object
     */
    private static Object convertValueType(AbstractParamMapping paramMapping, Object object, Map<String, String> map) {
        // 如果没有匹配到参数或者匹配到空串，则返回null
        if (ObjectUtil.isNull(object) || StringUtils.isBlank(object.toString())){
            return null;
        }

        // 获取到参数的类型
        String objectDataType = object.getClass().getSimpleName();
        Object value = null;
        try {
            // 设置的参数类型
            object = convertObject(object, paramMapping);
            String dataType = buildDataType(paramMapping.getDataType());
            DataTypeEnum type = DataTypeEnum.getEnumByType(dataType);
            if (ObjectUtil.isNull(type)){
                return object;
            }
            // 转换类型
            switch (type){
                case OBJECT:
                    value = CollectionUtil.isEmpty(map) ? object : traverseJsonObjectType(paramMapping, (JSONObject) object, map);
                    break;
                case ARRAY:
                    value = (object instanceof JSONArray || object instanceof ArrayList)
                            ? object
                            : JSON.parseArray((String) object);
                    break;
                case STRING :
                    value = String.valueOf(object);
                    break;
                case INTEGER:
                    value = Integer.valueOf(String.valueOf(object));
                    break;
                case BOOLEAN:
                    value = ObjectUtil.equal(Boolean.TRUE.toString(),object) || ObjectUtil.equal(Boolean.FALSE.toString(),object)
                            ? Boolean.valueOf(String.valueOf(object))
                            : (Boolean) object;
                    break;
                case NUMBER:
                    value = new BigDecimal(String.valueOf(object));
                    break;
                default:
                    value = object;
                    break;
            }
        } catch (ClassCastException | NumberFormatException e){
            throw new OpenException(
                    String.format(OpenApiMsgCodeEnum.w_param_mapper_type_error.getDesc(),
                            paramMapping.getField(),objectDataType,paramMapping.getDataType(),object));
        } catch (JSONException e){
            throw new OpenException(
                    String.format(OpenApiMsgCodeEnum.w_param_mapper_format_error.getDesc(), paramMapping.getField()));
        }
        return value;
    }

    /**
     * 判断object中是否包含“root”这个key， 并且value值是一个JSONArray类型数据
     * 需要转换的类型是Array，并且表达式是直接获取开始节点的请求体根节点的数据“$.start.Body”
     *
     * @param object 需要判断的object对象
     * @param paramMapping 表达式的信息
     */
    private static Object convertObject(Object object, AbstractParamMapping paramMapping) {
        // 如果object是JSONObject格式，转换后做处理
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            String expression = paramMapping.getExpression();
            // 如果表达式是json格式，从表达式中获取要转换成的类型
            if (StringUtils.isNotEmpty(expression) && paramMapping.getExpressionIsJson() &&  JsonUtil.isJson(expression)) {
                Object expressionObject = JSONPath.eval(JSONObject.parseObject(paramMapping.getExpression()), CONVERT_TO_DATA_TYPE);
                expression = ObjectUtil.isNotEmpty(expressionObject) ? expressionObject.toString() : StringUtils.EMPTY;
            }
            // 如果参数object中是否包含“root”这个key， 并且value值是一个JSONArray类型数据。需要转换的类型是Array，并且表达式是直接获取开始节点的请求体根节点的数据“$.start.Body”
            if (jsonObject.containsKey(ParamMappingsUtils.SCHEMA_ROOT)
                    && DataTypeEnum.JSON_ARRAY.getType().equals(isJsonObjectOrJsonArray(jsonObject.get(ParamMappingsUtils.SCHEMA_ROOT)))
                    // 如果之后逻辑需要转换成array
                    && DataTypeEnum.ARRAY.getType().equals(expression)) {
                paramMapping.setDataType(expression);
                return jsonObject.get(ParamMappingsUtils.SCHEMA_ROOT);
            }
        }
        // 不符合消除“root”这一层包装的，直接返回
        return object;
    }
    /**
     * 处理array<object>,array<string>,array<integer>,array<number>
     *
     * @param dataType 数据类型
     * @return 处理后的数据类型
     */
    public static String buildDataType(String dataType) {
        if (StringUtils.containsIgnoreCase(dataType,DataTypeEnum.ARRAY.getType())){
            return DataTypeEnum.ARRAY.getType();
        }
        return dataType;
    }

    /**
     * 当对象为object时
     * 转换ExpressionJsonValue为Map<filed,dataType>
     *
     * @param jsonObject object对象
     * @param map Map<filed,dataType>
     * @param parentKey 父节点
     */
    private static void convertType(JSONObject jsonObject, Map<String, String> map, String parentKey) {

        if (JSONUtil.isNull(jsonObject)){
            return;
        }
        for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
            String key = StringUtils.isEmpty(parentKey) ? stringObjectEntry.getKey() : parentKey.concat(".".concat(stringObjectEntry.getKey()));
            JSONObject value = (JSONObject)stringObjectEntry.getValue();
            map.put(key,value.getString(EXPRESSION_DATA_TYPE));
//            if (ObjectUtil.equal(value.getString(EXPRESSION_IS_JSON),Boolean.TRUE.toString())){
//                convertType(value.getJSONObject(EXPRESSION_VALUE),map,key);
//            }
            if (DataTypeEnum.OBJECT.getType().equals(value.getString(EXPRESSION_DATA_TYPE))) {
                convertType(value.getJSONObject(EXPRESSION_VALUE), map, key);
            }
        }
    }

    /**
     * 转换object中所有的字段类型
     *
     * @param paramMapping 该object对应的paramMapping
     * @param jsonObject object对象
     * @param map Map<filed,dataType>
     * @return 转换完类型后的object
     */
    private static JSONObject traverseJsonObjectType(AbstractParamMapping paramMapping, JSONObject jsonObject, Map<String, String> map) {

        // 组装parentFiled(例如：node1.node11.node111)
        String filed = paramMapping.getField();
        String parentFiled = paramMapping.getParentFiled();
        parentFiled = StringUtils.isEmpty(parentFiled) ? filed : parentFiled.concat(".").concat(filed);

        // 转换object中所有的字段类型
        AbstractParamMapping sonParamMapping = new AbstractParamMapping();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            // 获取子节点的dataType
            sonParamMapping.setParentFiled(parentFiled);
            sonParamMapping.setField(entry.getKey());
            sonParamMapping.setDataType(map.get(parentFiled.concat(".").concat(entry.getKey())));
            // 将子节点转换成相对应的dataType
            Object valueType = convertValueType(sonParamMapping, entry.getValue(), map);
            entry.setValue(valueType);
        }
        return jsonObject;
    }

    /**
     * 判断入参是JsonObject还是JsonArray
     *
     * @param value Object类型入参
     * @return String DataAssetEnums.AssetDataTypeEnum中的value
     */
    public static String isJsonObjectOrJsonArray(Object value) {
        String str = Objects.toString(value, StringUtils.EMPTY);
        if (ObjectUtil.isEmpty(value) || StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        if (JSONUtil.isJsonObj(str)) {
            return DataTypeEnum.JSON_OBJECT.getType();
        }
        if (JSONUtil.isJsonArray(str)) {
            return DataTypeEnum.JSON_ARRAY.getType();
        }
        return StringUtils.EMPTY;
    }

}