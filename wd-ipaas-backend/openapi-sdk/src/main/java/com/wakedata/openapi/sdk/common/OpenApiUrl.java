package com.wakedata.openapi.sdk.common;

/**
 * 请求url配置
 * @author luomeng
 * @date 2022/8/23 17:44
 */
public class OpenApiUrl {

    /**
     * todo 请求域名(需修改成对应环境的配置)
     */
    public static final String REQUEST_HOST = "https://bizpf-test-ipaas.wakedt.cn";

    /**
     * token相关api
     */
    public static class AccessToken{

        /**
         * 获取授权token
         */
        public static final String GET_ACCESS_TOKEN = REQUEST_HOST + "/dw/open/api/auth/get.access.token";

        /**
         * 刷新token
         */
        public static final String REFRESH_TOKEN = REQUEST_HOST + "/dw/open/api/auth/refresh.token";

        /**
         * 校验token是否有效
         */
        public static final String CHECK_TOKEN = REQUEST_HOST + "/dw/open/api/auth/check.token";

    }


}
