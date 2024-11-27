package com.wakedata.openapi.sdk.common;

/**
 * 常量配置
 * @author luomeng
 * @date 2022/8/23 17:46
 */
public interface OpenApiConstant {

    /**
     * 超时时间
     */
    Integer TIMEOUT = 60000;

    /**
     * URL连接符
     */
    String URL_JOIN_QUESTION = "?";

    /**
     * 请求参数：时间戳
     */
    String REQ_PARAM_TIMESTAMP = "timestamp";

    /**
     * 请求参数：token
     */
    String REQ_PARAM_ACCESS_TOKEN = "accessToken";

    /**
     * 请求参数：签名
     */
    String REQ_PARAM_SIGN = "sign";

}
