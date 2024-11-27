package com.wakedata.dw.open.constant;

/**
 * 第三方认证json解析
 *
 * @author 佟蕊
 */
public interface ThirdAuthConstant {

    /**
     * CONFIGS信息
     */
    String CONFIGS_KEY = "key";
    String CONFIGS_VALUE = "value";
    String CONFIGS_LOCATION = "location";

    /**
     * AUTHORIZATIONAPI信息
     */
    String AUTHORIZATIONAPI_APPLICATION_NAME = "applicationName";
    String AUTHORIZATIONAPI_AUTHORIZATION_API = "authorizationApi";
    String AUTHORIZATIONAPI_JSON = "json";
    String AUTHORIZATIONAPI_QUERY = "query";
    String AUTHORIZATIONAPI_REQUESTTYPE = "requestType";
    String AUTHORIZATIONAPI_REQUESTURL = "requestUrl";
    String AUTHORIZATIONAPI_CONTEXTPATH = "contextPath";

    /**
     * 调用执行服务信息
     */
    String INVOKE_APPLICATION_NAME = "applicationName";
    String INVOKE_REQUESTURL = "requestUrl";
    String INVOKE_REQUESTTYPE = "requestType";
    String INVOKE_CONTEXTPATH = "contextPath";
    String INVOKE_EXCUTEPARAMJSON = "excuteParamJson";
    String INVOKE_ORIGINALPARAMJSON = "originalParamJson";

    /**
     * 包含第三方认证的特殊参数
     */
    String PARAM_OBJECT = "paramObject";

    /**
     * 绑定单个/多个应用，前端传递信息
     */
    String BIND_APP_AUTH_ID="authId";
    String BIND_APP_AUTH_LIST="authList";
    String BIND_APP_AUTH_LIST_APIID="apiId";

    /**
     * 前端传递过来的低代码类型
     */
    String LOW_CODE_TYPE="lowCodeType";

    /**
     * 惟客云登录
     */
    String WK_LOGIN_APPLICATION_NAME="wk-oauth-wakecloud";
    String WK_LOGIN_CONTENT_PATH="";
    String WK_LOGIN_METHOD_URL="/wakecloud/login";
    String WK_LOGIN_USERNAME="userName";
    String WK_LOGIN_PWD="password";


    /**
     * 惟客云默认参数
     */
    enum WdAuthParamEnum{
        /**
         * 配置参数
         */
        COMMON_TENANT_ID("tenantId","租户id"),
        COMMON_APP_BU_ID("appBuId","应用业务单元id"),
        SQL_TENANT_ID("tenant_id","sql使用租户id"),
        SQL_APP_BU_ID("app_bu_id","sql使用应用业务单元id"),
        RESULT_CODE("code","错误码"),
        RESULT_MSG("msg","结果说明"),
        RESULT_DATA("data","返回数据"),
        RESULT_SUCCESS("success","处理结果"),
        RESULT_TOTAL_COUNT("totalCount","分页查询总数")
        ;


        private String code;
        private String desc;

        WdAuthParamEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
