package com.wakedata.dw.open.parammapping.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * JsonUtil
 *
 * @author focus
 * @date 2021/11/3
 **/

public class JsonUtil {

    private static final String PRE_ARR = "[";
    private static final String END_ARR = "]";
    private static final String PRE_JSON = "{";
    private static final String END_JSON = "}";

    public static boolean isJsonArr(Object value) {
        return value instanceof JSONArray;
    }

    public static boolean isJson(Object value) {
        return value instanceof JSONObject;
    }

    public static boolean isJson(String str) {
        str = str.trim();
        return str.startsWith(PRE_JSON) && str.endsWith(END_JSON);
    }

    public static boolean isJsonArr(String str) {
        str = str.trim();
        return str.startsWith(PRE_ARR) && str.endsWith(END_ARR);
    }

}
