package com.wakedata.dw.open.swagger.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

/**
 * Swagger doc对象值获取工具类
 *
 * @author chenshaopeng
 * @date 2021/10/29
 */
public class SwaggerDataUtils {

    private final static String PATH_SEPARATOR = "\\.";

    private final static String PARAM_SEPARATOR = "\\|";

    /**
     *
     *
     * @param jsonOrObject 源对象，可以是JSONObject或Map
     * @param paramPath 需要获取的参数路径，可以使用“.”获取多层级数据；
     *                  也可以通过“|”获取多个多个参数，返回第一个不为空的数据
     * @return
     */
    public static Object getObject(Object jsonOrObject, String paramPath){
        String[] keys = paramPath.split(PARAM_SEPARATOR);
        if(keys.length == 0){
            keys = new String[]{paramPath};
        }

        Object json = null;
        for(String fullPath : keys){
            if(json != null){
                return json;
            }
            json = jsonOrObject;
            String[] paths = fullPath.split(PATH_SEPARATOR);
            if(paths.length == 0){
                paths = new String[]{fullPath};
            }
            for (String path : paths) {
                if(jsonOrObject == null){
                    break;
                }
                if(json instanceof JSONObject){
                    json = ((JSONObject) json).get(path);
                } else if (json instanceof LinkedTreeMap) {
                    json = ((LinkedTreeMap) json).get(path);
                }else {
                    json = null;
                    break;
                }
            }
        }
        return json;
    }

    public static String getString(Object jsonObject, String paramPath){
        return (String) getObject(jsonObject, paramPath);
    }

    public static Integer getInt(Object jsonObject, String paramPath){
        return (Integer) getObject(jsonObject, paramPath);
    }

    public static Boolean getBoolean(Object jsonObject, String paramPath){
        return (Boolean) getObject(jsonObject, paramPath);
    }

    public static List<Object> getList(Object jsonObject, String paramPath){
        return (List<Object>) getObject(jsonObject, paramPath);
    }


}
