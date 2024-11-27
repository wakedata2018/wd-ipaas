package com.wakedata.dw.open.utils.threadlocal;

/**
 * LiteFlowStartParamThreadLocal liteflow start节点参数信息
 * @author 佟蕊
 */
public final class LiteFlowStartParamThreadLocal {

    /**
     * start节点参数信息
     */
    private static final ThreadLocal<String> startParamThreadLocal = new ThreadLocal<>();

    public static String getStartParam(){
        String startParam = startParamThreadLocal.get();
        return startParam;
    }

    public static void setStartParam(String startParam){
        startParamThreadLocal.set(startParam);
    }

    public static void removeStartParam(){
        startParamThreadLocal.remove();
    }
}

