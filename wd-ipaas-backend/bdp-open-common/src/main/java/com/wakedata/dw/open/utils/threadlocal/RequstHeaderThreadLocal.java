package com.wakedata.dw.open.utils.threadlocal;

import java.util.Map;

/**
 * RequstHeaderThreadLocal header信息
 * @author 佟蕊
 */
public final class RequstHeaderThreadLocal {

    /**
     * header信息
     */
    private static final ThreadLocal<Map<String,Object>> headParamThreadLocal = new ThreadLocal<>();

    public static Map getHeadParam(){
        Map<String,Object> headParam = headParamThreadLocal.get();
        return headParam;
    }
    public static void setHeadParam(Map<String,Object> headParam){
        headParamThreadLocal.set(headParam);
    }
    public static void removeHeadParam(){
        headParamThreadLocal.remove();
    }
}
