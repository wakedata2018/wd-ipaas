package com.wakedata.dw.open.parammapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 响应结果类型
 *
 * @author ZhangXueJun
 * @title ResponseKind
 * @date 2021/5/12 15:31
 * @projectName dw-open
 * @description
 */
public enum ResponseKind {
    JSON_OBJECT,
    JSON_ARRAY,
    ;

    public static ResponseKind get(Class<? extends JSON> clazz) {
        if (clazz == JSONObject.class) {
            return JSON_OBJECT;
        }
        return JSON_ARRAY;
    }
}
