package com.wakedata.dw.open.service.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static com.wakedata.dw.open.enums.DataTypeEnum.*;

/**
 * @author WangChenSheng
 * @descriptor 解析json/json-schema字符串工具类
 * @title JsonUtils
 * @date 2022/8/22 11:35
 */
public class JsonUtils {

    /**
     * JsonSchema默认Key
     */
    public static final String SCHEMA_ROOT = "root";
    public static final String SCHEMA_TYPE = "type";
    public static final String SCHEMA_PROPERTIES = "properties";
    public static final String SCHEMA_REQUIRED = "required";
    public static final String SCHEMA_PARAMETER_OBJECT = "object";
    public static final String SCHEMA_PARAMETER_ARRAY = "array";
    public static final String SCHEMA_ITEMS = "items";
    public static final String FORMAT_KEY = "format";
    public static final String SCHEMA_NAME = "name";

    public static final String SCHEMA_ALLOW_EMPTY = "allowEmpty";
    public static final String SCHEMA_DESCRIPTION = "description";
    public static final String SCHEMA_VALUE = "value";
    public static final String SCHEMA_PARAM_VALUE_TYPE = "paramValueType";
    public static final String SCHEMA_EXPRESSION = "expression";
    public static final String SCHEMA_DEFAULT_VALUE = "defaultValue";

    public static final String DEFAULT_BANK = " ";

    public static final Map<String,Object> DEFAULT_COLUMN_VALUE = new HashMap<>();

    /*
      给相应数据类型赋值示例值
     */
    static {
        DEFAULT_COLUMN_VALUE.put(STRING.getType()," ");
        DEFAULT_COLUMN_VALUE.put(BOOLEAN.getType(), Boolean.TRUE);
        DEFAULT_COLUMN_VALUE.put(INTEGER.getType(),0);
        DEFAULT_COLUMN_VALUE.put(NUMBER.getType(), 0.0);
        DEFAULT_COLUMN_VALUE.put(LONG.getType(), 0);
        DEFAULT_COLUMN_VALUE.put(DOUBLE.getType(), 0.00);
    }

    /**
     * JsonSchema转Json
     *
     * @param jsonSchema JsonSchema(String)
     * @return Json(String)
     */
    public static String JsonSchemaToJson(String jsonSchema){
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();

        JSONObject root = JSONUtil.parseObj(jsonSchema).getJSONObject(SCHEMA_ROOT);
        if (Objects.isNull(root)){
            return null;
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        if(SCHEMA_PARAMETER_OBJECT.equals(root.get(SCHEMA_TYPE))) {
            JsonSchemaToJsonObject(root,map,null,ResultType.JSON_SCHEMA_BASIC_COLUMN);
        }else {
            JsonSchemaToJsonArray(root, map, new ArrayList<>(), null, ResultType.JSON_SCHEMA_BASIC_COLUMN);
            return Strings.concat("[", gson.toJson(map) + "]");
        }



        return gson.toJson(map);
    }

    /**
     * JsonSchema解析补充字段(以LinkTreeMap的形式返回)
     *
     * @param jsonSchema jsonSchema(String)
     * @return 以key=colum，value=List<Map<补充字段名，补充字段的值>>的形式返回
     */
    public static LinkedTreeMap<String,Object> JsonSchemaToExtraColum(String jsonSchema){
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();

        JSONObject root = JSONUtil.parseObj(jsonSchema).getJSONObject(SCHEMA_ROOT);
        JsonSchemaToJsonObject(root,map,null,ResultType.JSON_SCHEMA_EXTRA_COLUMN);

        return map;
    }

    /**
     * 获取jsonSchema中的必填字段
     *
     * @param jsonSchema jsonSchema
     * @return 必填字段List
     */
    public static List<String> JsonSchemaToRequiredColum(String jsonSchema){
        List<String> schemaRequiredList = new ArrayList<>();

        JSONObject parentSchema = JSONUtil.parseObj(jsonSchema).getJSONObject(SCHEMA_ROOT);
        JsonSchemaToRequiredColum(parentSchema,null, schemaRequiredList);

        return schemaRequiredList;
    }


    /**
     * 获取jsonSchema指定type
     * (数组key存在两种type，例如www:array[Object],在指定type=array或者type=object获取key时，都存在www)
     *
     * @param jsonSchema jsonSchema
     * @param type 指定类型
     * @return 指定类型的List<key>
     */
    public static List<String> jsonSchemaType(String jsonSchema,String type){
        List<String> typeKeyList = new ArrayList<>();

        JSONObject parentSchema = JSONUtil.parseObj(jsonSchema).getJSONObject(SCHEMA_ROOT);
        jsonSchemaType(parentSchema,null, typeKeyList,type);

        return typeKeyList;
    }

    /**
     * 获取json所有的key-value
     *
     * @param json json
     * @return Map<key,value>
     */
    public static Map<String,Object> JsonToMap(String json){
        Map<String,Object> parameterJsomMap = new HashMap<>();

        JSONObject jsonObject = JSONUtil.parseObj(json);
        JsonToMap(jsonObject,null, parameterJsomMap);

        return parameterJsomMap;
    }


    /**
     * XML转换JSON
     *
     * @param xmlStr xmlStr
     * @param arrayKeyList arrayKeyList
     * @return 以key=colum，value=List<Map<补充字段名，补充字段的值>>的形式返回
     */
    public static String transformXML(String xmlStr, List<String> arrayKeyList){
        JSONObject xml = XML.toJSONObject(xmlStr);
        if (Objects.isNull(xml)){
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        transformXMLObject(xml, arrayKeyList, null, jsonObject);

        return JSONUtil.toJsonStr(jsonObject);
    }


    /**
     * 将json转换为Map<key,value>
     *
     * @param object jsonObject
     * @param parameterJsomMap 存放json的Map
     */
    private static void JsonToMap(Object object, String parentParameterKey, Map<String, Object> parameterJsomMap) {

        // 处理类型为object的情况
        if(object instanceof JSONObject) {
            JSONObject jsonObject = JSONUtil.parseObj(object);
            for (Map.Entry<String, Object> entry: jsonObject.entrySet()) {

                // 拼接json中的父级节点
                String parameterKey = Objects.isNull(parentParameterKey) ? entry.getKey() : parentParameterKey.concat(".".concat(entry.getKey()));
                Object parameterValue = entry.getValue();

                if (parameterValue instanceof  JSONObject){
                    parameterJsomMap.put(parameterKey,parameterValue);
                }

                if(parameterValue instanceof String || parameterValue instanceof Integer || parameterValue instanceof Boolean) {
                    parameterJsomMap.put(parameterKey,parameterValue);
                }

                if (parameterValue instanceof JSONObject || parameterValue instanceof JSONArray){
                    JsonToMap(parameterValue,parameterKey,parameterJsomMap);
                }
            }
        }

        // 处理类型为JSONArray的情况
        if(object instanceof JSONArray) {
            JSONArray jsonArray = JSONUtil.parseArray(object);
            parameterJsomMap.put(parentParameterKey,jsonArray);
            jsonArray.forEach(obj -> JsonToMap(obj,parentParameterKey,parameterJsomMap));
        }
    }

    /**
     * 获取List<String> requiredKey
     *
     * @param jsonObject jsonObject(jsonSchema中的root节点)
     * @param parentParameterKey 上层Key
     * @param schemaRequiredList List<String> requiredKey
     */
    private static void JsonSchemaToRequiredColum(JSONObject jsonObject, String parentParameterKey, List<String> schemaRequiredList){

        if (Objects.isNull(jsonObject)){
            return;
        }

        String parameterTypeSchema = jsonObject.getStr(SCHEMA_TYPE);
        JSONArray requiredSchema = jsonObject.getJSONArray(SCHEMA_REQUIRED);

        // 获取List<必填字段>
        if (Objects.nonNull(requiredSchema)){
            requiredSchema.toList(String.class).stream()
                    .map(parameter -> Objects.equals(parameterTypeSchema, SCHEMA_PARAMETER_ARRAY)
                            ? jsonObject.getJSONObject(SCHEMA_ITEMS).getJSONObject(SCHEMA_PROPERTIES).getJSONObject(parameter)
                            : jsonObject.getJSONObject(SCHEMA_PROPERTIES).getJSONObject(parameter))
                    .filter(Objects::nonNull)
                    .map(object -> object.getStr(DwOpenConstant.SCHEMA_PARAMETER_KEY_NAME)).map(str -> Objects.isNull(parentParameterKey) ? str : parentParameterKey.concat(".").concat(str))
                    .forEach(schemaRequiredList::add);
        }

        // 处理非Array类型
        if (Objects.equals(parameterTypeSchema, SCHEMA_PARAMETER_OBJECT)){
            JSONObject sonJson = jsonObject.getJSONObject(SCHEMA_PROPERTIES);
            if (JSONUtil.isNull(sonJson)){
                return;
            }
            for (Map.Entry<String, Object> entry : sonJson.entrySet()) {

                // 拼接jsonSchema的父级节点
                JSONObject value = JSONUtil.parseObj(entry.getValue());
                String key = value.getStr(DwOpenConstant.SCHEMA_PARAMETER_KEY_NAME);
                key = StringUtils.isEmpty(key) ? DEFAULT_BANK : key;
                String parameterKey = Objects.isNull(parentParameterKey) ? key : parentParameterKey.concat(".").concat(key);

                JsonSchemaToRequiredColum(sonJson.getJSONObject(entry.getKey()),parameterKey,schemaRequiredList);
            }
        }

        // 处理Array类型
        if (Objects.equals(parameterTypeSchema, SCHEMA_PARAMETER_ARRAY)){
            JSONObject array = jsonObject.getJSONObject(SCHEMA_ITEMS);
            JsonSchemaToRequiredColum(array,parentParameterKey,schemaRequiredList);
        }
    }

    /**
     * 获取jsonSchema中类型为array的字段
     *
     * @param jsonObject jsonObject
     * @param parentParameterKey parentParameterKey
     * @param type 指定的类型
     * @param typeList 指定类型key集合
     */
    public static void jsonSchemaType(JSONObject jsonObject, String parentParameterKey, List<String> typeList, String type){

        if (Objects.isNull(jsonObject)){
            return;
        }

        // 获取字段类型
        String parameterTypeSchema = jsonObject.getStr(SCHEMA_TYPE);

        // 处理非Array类型
        if (Objects.equals(parameterTypeSchema, SCHEMA_PARAMETER_OBJECT)){
            JSONObject sonJson = jsonObject.getJSONObject(SCHEMA_PROPERTIES);
            if (JSONUtil.isNull(sonJson)){
                return;
            }
            for (Map.Entry<String, Object> entry : sonJson.entrySet()) {

                // 拼接jsonSchema的父级节点
                JSONObject value = JSONUtil.parseObj(entry.getValue());
                String key = value.getStr(DwOpenConstant.SCHEMA_PARAMETER_KEY_NAME);
                key = StringUtils.isEmpty(key) ? DEFAULT_BANK : key;
                String parameterKey = Objects.isNull(parentParameterKey) ? key : parentParameterKey.concat(".").concat(key);

                jsonSchemaType(sonJson.getJSONObject(entry.getKey()),parameterKey, typeList,type);
            }
        }

        // 处理Array类型
        if (Objects.equals(parameterTypeSchema, SCHEMA_PARAMETER_ARRAY)){

            JSONObject array = jsonObject.getJSONObject(SCHEMA_ITEMS);
            jsonSchemaType(array,parentParameterKey, typeList,type);
        }

        // 获取jsonSchema中指定type对应字段(数组key存在两种类型)
        if (StringUtils.isNotEmpty(parentParameterKey) && ObjectUtil.equal(parameterTypeSchema,type)){
            typeList.add(parentParameterKey);
        }
    }

    /**
     * 根据JsonSchema构建Object类型的结构
     *
     * @param jsonObject jsonObject
     * @param parentMap parentMap
     */
    private static void JsonSchemaToJsonObject(JSONObject jsonObject,
                                               LinkedTreeMap<String, Object> parentMap,
                                               String parentKey, ResultType resultType){

        JSONObject properties = jsonObject.getJSONObject(SCHEMA_PROPERTIES);
        if (JSONUtil.isNull(properties)){
            return;
        }

        if (ObjectUtil.equal(SCHEMA_PARAMETER_OBJECT,jsonObject.get(SCHEMA_TYPE))){
            buildKeyAndValue(properties, parentMap, parentKey, resultType);
        }
    }

    /**
     * 根据JsonSchema构建Array类型的结构
     *
     * @param jsonObject jsonObject
     * @param parentMap parentMap
     * @param parentArray parentArray
     * @param parentKey parentKey
     * @param resultType resultType
     */
    private static void JsonSchemaToJsonArray(JSONObject jsonObject,
                                              LinkedTreeMap<String, Object> parentMap, List<Object> parentArray,
                                              String parentKey, ResultType resultType){

        JSONObject items = jsonObject.getJSONObject(SCHEMA_ITEMS);
        JSONObject properties = items.getJSONObject(SCHEMA_PROPERTIES);

        if (JSONUtil.isNull(properties)){
            return;
        }

        if (ObjectUtil.equal(SCHEMA_PARAMETER_OBJECT,items.get(SCHEMA_TYPE))){
            buildKeyAndValue(properties,parentMap, parentKey, resultType);
            parentArray.add(parentMap);
        }
    }

    /**
     * 构建数据结构(转换核心)
     *
     * @param properties properties
     * @param parentMap parentMap
     * @param parentKey parentKey
     * @param resultType 转换类型
     */
    private static void buildKeyAndValue(JSONObject properties, LinkedTreeMap<String, Object> parentMap, String parentKey, ResultType resultType) {
        LinkedTreeMap<String, Object> sonMap;
        List<Object> sonArray;
        for (Map.Entry<String, Object> stringObjectEntry : properties.entrySet()) {
            sonMap = new LinkedTreeMap<>();
            sonArray = new ArrayList<>();

            // 拼接jsonSchema的父级节点
            JSONObject value = JSONUtil.parseObj(stringObjectEntry.getValue());
            String key = value.getStr(DwOpenConstant.SCHEMA_PARAMETER_KEY_NAME);
            key = StringUtils.isEmpty(key) ? DEFAULT_BANK : key;
            String parameterKey = Objects.isNull(parentKey) ? key : parentKey.concat(".").concat(key);

            if(StringUtils.isEmpty(key) || ObjectUtil.isNull(value)) {
                return;
            }

            // 构建Map结构
            if (ObjectUtil.equal(SCHEMA_PARAMETER_OBJECT,value.get(SCHEMA_TYPE))){
                parentMap.put(key,sonMap);
                JsonSchemaToJsonObject(value,sonMap,parameterKey,resultType);
            }

            if (ObjectUtil.equal(SCHEMA_PARAMETER_ARRAY,value.get(SCHEMA_TYPE))){
                parentMap.put(key,sonArray);
                JsonSchemaToJsonArray(value,sonMap,sonArray,parameterKey,resultType);
            }

            // 构建字段的值
            buildColumnValue(value, resultType, parentMap, sonMap, parameterKey, key);
        }
    }

    /**
     *
     * 根据ResultType构建相对应的LinkTreeMap<key,object>结构
     *
     * @param value 字段对应的值
     * @param resultType 解析类型(基本字段/补充字段)
     * @param map map
     * @param sonMap 子map
     * @param parentKey 父字段(拼接所有父字段)
     * @param key 字段
     */
    private static void buildColumnValue(JSONObject value, ResultType resultType,
                                         LinkedTreeMap<String, Object> map, LinkedTreeMap<String, Object> sonMap,
                                         String parentKey, String key) {
        // 基本字段：根据类型获取默认值
        if (ObjectUtil.equal(resultType, ResultType.JSON_SCHEMA_BASIC_COLUMN)) {
            defaultColumnValue(map, key, value);
        }

        // 补充字段：构建key=字段名 value=(补充字段1=xxx,补充字段2=xxx。。。)的Map
        if (ObjectUtil.equal(resultType, ResultType.JSON_SCHEMA_EXTRA_COLUMN)) {
            map.putAll(sonMap);
            extraColumnValue(map, parentKey, value);
        }
    }

    /**
     * 构建JsonSchema基本字段的默认值
     *
     * @param map map
     * @param key key
     * @param value value
     */
    private static void defaultColumnValue(Map<String, Object> map, String key, JSONObject value) {
        Object type = value.get(SCHEMA_TYPE);
//        Object simpleValue = value.get(SCHEMA_VALUE);
        DataTypeEnum dataTypeEnum = DataTypeEnum.getEnumByType((String)type);
        if (dataTypeEnum != null) {
            switch (dataTypeEnum) {
                case STRING:
                    String format = (String) value.get(FORMAT_KEY);
                    if (DATE_TIME.getType().equals(format) || DATETIME.getType().equals(format)) {
                        map.put(key, DateUtil.now());
                        break;
                    }
    //                simpleValue = ObjectUtil.isNull(simpleValue) ? DEFAULT_COLUM_VALUE.get(COLUM_STRING) : simpleValue;
                    map.put(key, DEFAULT_COLUMN_VALUE.get(STRING.getType()));
                    break;
                case BOOLEAN:
    //                simpleValue = ObjectUtil.isNull(simpleValue) ? DEFAULT_COLUM_VALUE.get(COLUM_BOOLEAN) : simpleValue;
                    map.put(key, DEFAULT_COLUMN_VALUE.get(BOOLEAN.getType()));
                    break;
                case INTEGER:
    //                simpleValue = ObjectUtil.isNull(simpleValue) ? DEFAULT_COLUM_VALUE.get(COLUM_INTEGER) : simpleValue;
                    map.put(key, DEFAULT_COLUMN_VALUE.get(INTEGER.getType()));
                    break;
                case NUMBER:
    //                simpleValue = ObjectUtil.isNull(simpleValue) ? DEFAULT_COLUM_VALUE.get(COLUM_NUMBER) : simpleValue;
                    map.put(key, DEFAULT_COLUMN_VALUE.get(NUMBER.getType()));
                    break;
                case LONG:
                    map.put(key, DEFAULT_COLUMN_VALUE.get(LONG.getType()));
                    break;
                case DOUBLE:
                    map.put(key, DEFAULT_COLUMN_VALUE.get(DOUBLE.getType()));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 构建JsonSchema补充字段的值
     *
     * @param map 存储补充字段的key-value
     * @param key key
     * @param value value
     */
    private static void extraColumnValue(Map<String, Object> map, String key, JSONObject value) {

        Map<String,Object> extraColumnMap = new HashMap<>();
        extraColumnMap.put(DwOpenConstant.EXPRESSION,checkJsonNull(value.get(DwOpenConstant.EXPRESSION)));
        extraColumnMap.put(DwOpenConstant.PARAM_VALUE_TYPE,checkJsonNull(value.get(DwOpenConstant.PARAM_VALUE_TYPE)));
        extraColumnMap.put(DwOpenConstant.DEFAULT_VALUE,checkJsonNull(value.get(DwOpenConstant.DEFAULT_VALUE)));

        map.put(key,extraColumnMap);
    }

    /**
     * 处理XML中的Object
     */
    private static void transformXMLObject(Object object, List<String> arrayKeyList, String parentKey, JSONObject parentObj) {

        JSONObject sonObj;
        JSONArray sonArray;
        if (object instanceof JSONObject){
            JSONObject jsonObject = JSONUtil.parseObj(object);
            for (Map.Entry<String, Object> stringObjectEntry : jsonObject.entrySet()) {
                sonObj = new JSONObject();
                sonArray = new JSONArray();

                String key = stringObjectEntry.getKey();
                String parameterKey = ObjectUtil.isNull(parentKey) ? key : parentKey.concat(".").concat(key);
                Object value = stringObjectEntry.getValue();
                if (CollectionUtil.contains(arrayKeyList, parameterKey)) {
                    value = (value instanceof JSONArray) ? value : new JSONArray(Collections.singletonList(value));
                }

                if (value instanceof JSONObject){
                    transformXMLObject(value,arrayKeyList,parameterKey, sonObj);
                    parentObj.set(key,value);
                }
                if (value instanceof JSONArray){
                    transformXMLArray(value,arrayKeyList,parameterKey, sonObj,sonArray);
                    parentObj.set(key,value);
                }
                if (value instanceof String || value instanceof Boolean || value instanceof Number){
                    parentObj.set(key,value);
                }
            }
        }
    }

    /**
     * 处理XML中的ARRAY
     */
    private static void transformXMLArray(Object value, List<String> arrayKeyList, String parameterKey, JSONObject parentObj, JSONArray parentArray) {
        if (ObjectUtil.isNull(value)){
            return;
        }

        JSONArray array = (JSONArray) value;
        for (Object arrayValue : array) {
            transformXMLObject(arrayValue, arrayKeyList, parameterKey, parentObj);
            parentArray.add((arrayValue instanceof String) ? arrayValue : parentObj);
        }
    }

    /**
     * 判断是否为value = "null"
     *
     * @param obj obj
     * @return obj
     */
    private static Object checkJsonNull(Object obj){
        return JSONUtil.isNull(obj) ? null : obj;
    }

    private enum ResultType {
        /**
         * 解析JsonSchema基本类型
         */
        JSON_SCHEMA_BASIC_COLUMN,
        /**
         * 解析JsonSchema补充类型
         */
        JSON_SCHEMA_EXTRA_COLUMN
    }

}
