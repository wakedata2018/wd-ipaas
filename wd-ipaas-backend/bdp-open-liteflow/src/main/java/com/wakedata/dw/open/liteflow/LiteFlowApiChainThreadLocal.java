package com.wakedata.dw.open.liteflow;

/**
 * 存储api执行链的id，主要用于透传参数到CustomerApiJsonFlowParse类中
 *
 * @author luomeng
 * @date 2022/10/13 16:11
 */
public class LiteFlowApiChainThreadLocal {

    /**
     * 存储api chain对应的key
     */
    private static final ThreadLocal<Long> API_CHAIN_THREAD_LOCAL = new ThreadLocal<>();

    public static Long getApiChainKey() {
        return API_CHAIN_THREAD_LOCAL.get();
    }

    public static void setApiChainKey(Long dataAssetApiId) {
        API_CHAIN_THREAD_LOCAL.set(dataAssetApiId);
    }

    public static void removeApiChainKey() {
        API_CHAIN_THREAD_LOCAL.remove();
    }

}
