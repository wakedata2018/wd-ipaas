package com.wakedata.dw.open.client;

import cn.hutool.core.thread.threadlocal.NamedThreadLocal;
import org.apache.http.client.config.RequestConfig;

/**
 * http执行上下文信息
 * @author luomeng
 * @date 2022/10/10 18:57
 */
public class HttpContextHolder {
    private static final ThreadLocal<RequestConfig> THREAD_LOCAL = new NamedThreadLocal<>("http_context_holder");

    public static void set(RequestConfig requestConfig) {
        THREAD_LOCAL.set(requestConfig);
    }

    public static RequestConfig get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
