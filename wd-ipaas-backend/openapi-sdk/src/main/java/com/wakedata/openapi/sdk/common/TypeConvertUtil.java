package com.wakedata.openapi.sdk.common;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换
 * @author luomeng
 * @date 2022/10/26 16:38
 */
public class TypeConvertUtil {

    /**
     * 对象转map<string,string>
     * @param data
     * @return
     */
    public static Map<String,String> convert(Object data){
        if(ObjectUtil.isNull(data)){
            return null;
        }
        Map<String,Object> res = JSONUtil.toBean(JSONUtil.toJsonStr(data), Map.class);
        Map<String,String> result = new HashMap<>();
        for(Map.Entry<String,Object> map : res.entrySet()){
            Object value = map.getValue();
            if(value instanceof JSON){
                value = JSONUtil.toJsonStr(value);
            }
            result.put(map.getKey(),String.valueOf(value));
        }
        return result;
    }

    /**
     * 转换属性类型
     * @param type
     * @return
     */
    public static String convertFileType(String type){
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
                result = "Integer";
                break;
            case "long":
            case "bigint":
                result = "Long";
                break;
            case "boolean":
                result = "Boolean";
                break;
            case "decimal":
            case "number":
            case "float":
            case "double":
                result = "Double";
                break;
            case "date":
            case "datetime":
            case "timestamp":
                result = "Date";
                break;
            case "string":
            case "varchar":
                result = "String";
                break;
            case "object":
                result = "Object";
                break;
            case "array":
            case "array<object>":
                result = "List<Object>";
                break;
            case "array<string>":
                result = "List<String>";
                break;
            case "array<integer>":
                result = "List<Integer>";
                break;
            case "array<number>":
                result = "List<Double>";
                break;
            case "array<boolean>":
                result = "List<Boolean>";
                break;
            default:

                break;
        }
        return result;
    }

}
