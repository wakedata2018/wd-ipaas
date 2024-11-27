package com.wakedata.dw.open.utils;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import org.apache.commons.beanutils.ConvertUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;

/**
 * 字段类型转换工具类
 * @author 佟蕊
 */
public class ColumnTypeConvertUtil {

    /**
     * JSON数据格式与Java数据格式映射 Map
     */
    private static Map<String, Class> columnTypeAndClassMap = new ConcurrentHashMap<String, Class>(){{
        put("string",String.class);
        put("integer",Integer.class);
        put("boolean",Boolean.class);
        put("number",Number.class);
        put("object",Map.class);
        put("array",List.class);
    }};

    /**
     * 引用类型转换并赋值
     * @param parentJson 要赋值的json对象
     * @param assetColumns 字段名
     * @param assetColumnType 字段类型
     * @param fullOperatorResults 所有算子返回值
     * @param expression 表达式
     */
    public static void putValue(JSONObject parentJson, String assetColumns, String assetColumnType, JSONObject fullOperatorResults, String expression){
        Class tClass = columnTypeAndClassMap.get(assetColumnType);
        if (null == tClass){
            Object value = JSONPath.eval(fullOperatorResults, expression);
            parentJson.put(assetColumns,value);
            return;
        }
        parentJson.put(assetColumns,typeConversion(fullOperatorResults, expression,tClass));
    }

    /**
     * 固定类型转换并赋值
     * @param parentJson 要赋值的json对象
     * @param assetColumns 字段名
     * @param assetColumnType 字段类型
     * @param defaultValue 默认值
     */
    public static void putValue(JSONObject parentJson,String assetColumns,String assetColumnType,String defaultValue){
        Class tClass = columnTypeAndClassMap.get(assetColumnType);
        if (null == tClass){
            parentJson.put(assetColumns,defaultValue);
            return;
        }
        parentJson.put(assetColumns,typeConversion(defaultValue, tClass));
    }

    /**
     * 引用类型转换
     * @param fullOperatorResults 所有算子返回值
     * @param expression 表达式
     * @param tClass 要转换成为的类型
     * @return
     */
    private static Object typeConversion(JSONObject fullOperatorResults, String expression,Class<?> tClass){
        return ConvertUtils.convert(JSONPath.eval(fullOperatorResults, expression),tClass);
    }

    /**
     * 固定类型转换
     * @param defaultValue 默认值
     * @param tClass 要转换成为的类型
     * @return
     */
    private static Object typeConversion(String defaultValue,Class<?> tClass){
        return ConvertUtils.convert(defaultValue,tClass);
    }


    /**
     * hutool源码list类型转换
     */
    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
        return copyToList(collection, targetType, CopyOptions.create());
    }

    /**
     * hutool源码list类型转换
     */
    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType, CopyOptions copyOptions) {
        if (null == collection) {
            return null;
        } else {
            return (List)(collection.isEmpty() ? new ArrayList(0) : (List)collection.stream().map((source) -> {
                T target = ReflectUtil.newInstanceIfPossible(targetType);
                copyProperties(source, target, copyOptions);
                return target;
            }).collect(Collectors.toList()));
        }
    }

    /**
     * 转换参数类型
     * @param type 当前参数类型
     * @return String 转换后的参数类型
     */
    public static String convertParamType(String type){
        String result = "Object";
        if(StrUtil.isEmpty(type)){
            return result;
        }
        type = type.toLowerCase();
        switch (type){
            case "int":
            case "integer":
            case "tinyint":
            case "smallint":
                result = "integer";
                break;
            case "boolean":
                result = "boolean";
                break;
            case "bigint":
            case "long":
                result = "long";
                break;
            case "double":
            case "decimal":
            case "number":
            case "float":
                result = "double";
                break;
            case "date":
            case "datetime":
            case "timestamp":
            case "char":
            case "varchar":
            case "string":
            case "text":
                result = "string";
                break;
            case "object":
                result = "object";
                break;
            case "array":
            case "array<object>":
                result = "array<object>";
                break;
            case "array<string>":
                result = "array<string>";
                break;
            case "array<integer>":
                result = "array<integer>";
                break;
            case "array<number>":
                result = "array<double>";
                break;
            case "array<boolean>":
                result = "array<boolean>";
                break;
            default:
                MsgCodeEnum msgCodeEnum = MsgCodeEnum.w_api_sql_request_param_type_illegal;
                throw new OpenException(msgCodeEnum.getCode(), String.format(msgCodeEnum.getDesc(), type));
        }
        return result;
    }
}
