package com.wakedata.dw.lowcode.util;

import lombok.experimental.UtilityClass;

/**
 * @author wanghu@wakedata.com
 * @title 应用id上下文
 * @date 2021/12/2
 * @since v1.0.0
 */
@UtilityClass
public class AppIdContext {

    private static final ThreadLocal<Integer> APP_ID_THREAD_LOCAL = new ThreadLocal<>();

    public Integer getAppId() {
        return APP_ID_THREAD_LOCAL.get();
    }

    public void setAppId(Integer appId) {
        APP_ID_THREAD_LOCAL.set(appId);
    }

    public void cleanAppId() {
        APP_ID_THREAD_LOCAL.remove();
    }
}
