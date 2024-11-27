package com.wakedata.dw.open.constant;

/**
 * @author WangChenSheng
 * @data 2022/8/3 01:29
 */
public class DwOpenConstant {

    public static final Integer DEFAULT_PARENT_ID = 0;

    public static final Integer DEFAULT_LEVEL = 0;

    public static final Integer DEFAULT_FIRST = 0;

    /**
     * 用户密码加盐
     */
    public static final String USER_PASSWORD_SALT = "t4q3:$apr1$efgdut1y$1SCY";

    public static final String JOIN_POINT = ".";

    public static final String COLON = ":";

    public static final String BANK_STRING = "";

    /**
     * json标识
     */
    public static final String JSON_IDENTIFIER_LEFT = "{";
    public static final String JSON_IDENTIFIER_RIGHT = "}";

    /**
     * 中括号标识
     */
    public static final String MIDDLE_BRACKET_LEFT= "[";
    public static final String MIDDLE_BRACKET_RIGHT= "]";


    /**
     * 登录key
     */
    public static final String COOKIE_IPAAS_KEY = "ipaasJsid";

    /**
     * 权限后台登录key
     */
    public static final String COOKIE_PERMISSION_KEY = "dpjsid";

    /**
     * ipaas登录用户缓存key前缀
     */
    public static final String IPAAS_USER_INFO = "ipaas:userinfo:%s";

    /**
     * ipaas登录用户缓存时间
     */
    public static final Long IPAAS_USERINFO_EXPIRETIME = 1800L;


    /**
     * ACCESS_TOKEN 有效时间 秒
     */
    public static final Integer ACCESS_TOKEN_EXPIRE = 7200;

    /**
     * REFRESH_TOKEN 有效时间 天
     */
    public static final Integer REFRESH_TOKEN_EXPIRE = 30;

    /**
     * 临时token 有效时间 秒
     */
    public static final Integer TEST_ACCESS_TOKEN_EXPIRE = 600;

    /**
     * 开放平台默认api前缀
     */
    public static final String OPEN_API_PREFIX = "platform";

    /**
     * 开放平台api测试前缀
     */
    public static final String OPEN_API_TEST_PREFIX = "test";

    /**
     * ACCESS_TOKEN 前缀
     */
    public static final String ACCESS_TOKEN_PREFIX = "at";

    /**
     * REFRESH_TOKEN 前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = "rt";

    /**
     * 后台测试使用Token前缀
     */
    public static final String TEST_ACCESS_TOKEN_PREFIX = "testAt";

    /**
     * OpenContextInfo 前缀
     */
    public static final String OPEN_CONTEXT_INFO_PREFIX = "cxt";

    /**
     * 签名固定参数
     */
    public static final String SIGN_APP_KEY = "appKey";

    /**
     * 签名固定参数
     */
    public static final String SIGN_TIMESTAMP = "timestamp";

    /**
     * 签名固定参数
     */
    public static final String SIGN_ACCESS_TOKEN = "accessToken";

    /**
     * 签名时需排除参数
     */
    public static final String SIGN_EXCLUDE = "sign";

    /**
     * API路径分割最大数量
     */
    public static final int MAX_API_PATH_SLASH_SPLIT_COUNT = 4;

    /**
     * 响应参数中的记录日志的KEY
     */
    public static final String ENABLE_LOG = "__enable_log__";

    /**
     * webservice固定参数
     */
    public static final String WEB_SERVICE_METHOD = "wsMethod";
    public static final String WEB_SERVICE_XMLNS = "wsXmlns";
    public static final String WEB_SERVICE_ACCESS_WSDL = "accessWsdl";
    public static final String WEB_SERVICE_WSDL_SUFFIX = "?wsdl";

    /**
     * SwaggerJson固定参数
     */
    public static final String DATA_ASSET_API =  "dataAssetApi";
    public static final String API_ATTR = "apiAttr";
    public static final String REQ_METHOD = "reqMethod";

    /**
     * 响应参数默认父节点
     */
    public static final Integer RESPONSE_PARENT_ID = 0;

    /**
     * JSON SCHEMA初始id
     */
    public static final Integer JSON_SCHEMA_INIT_ID = 100;

    /**
     * Swagger的json串中,请求头对应的字符串
     */
    public static final String SWAGGER_REQUEST_HEAD = "header";

    /**
     * Api详情返回的KEY
     */
    public static final String API_DETAIL_HTTP_RESPONSE = "httpResponse";
    public static final String API_DETAIL_LIFT_FLOW_RESPONSE = "liftFlowResponse";

    /**
     * 响应模板解析设置算子参数名称
     */
    public static final String API_LITE_FLOW_RESPONSE_OPERATOR = "response_operator";

    /**
     * JsonSchema扩张Key
     */
    public static final String SCHEMA_PARAMETER_KEY_NAME = "name";
    public static final String EXPRESSION = "expression";
    public static final String PARAM_VALUE_TYPE = "paramValueType";
    public static final String DEFAULT_VALUE = "defaultValue";

    /**
     * 响应头
     */
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String REST_TEMPLATE_CUSTOMER = "restTemplateCustomer";

    /**
     * FILTER参数前缀
     */
    public static final String FILTER_PREFIX = "filter_";

    /**
     * api接口前缀
     */
    public static final String API_INTERFACE_PREFIX = "dw/open/api/";

    /**
     * 定时任务锁
     */
    public static final String XXL_JOB_THREAD_EXECUTOR = "ipaas.xxl.job.task";
    public static final String SCHEDULED_LOCK_KEY = "scheduled_lock_%s_%s";
    public static final Long REDIS_INCR_DEFAULT_VALUE = 1L;

    /**
     * 事件接收算子在服务编排GET请求中存放消息的QUERY参数名
     */
    public static final String EVENT_RECEIVE_QUERY_PARAM_NAME = "eventReceiveParams";

    /**
     * 通过RestTemplateUtil调用API时,用于存储调用来源的参数名(@ENUM ApiInvokeSource)
     */
    public static final String API_INVOKE_SOURCE_PARAM_NAME = "_ipaas_invoke_source_";

    /**
     * redis key
     */
    public static final String API_CHAIN_JSON_KEY = "openapi:liteflow";

    /**
     * redis time
     */
    public static final long API_CHAIN_JSON_EXPIRE_TIME = 600;

    /**
     * 数据表API请求体的参数类型-请求参数
     */
    public static final String NORMAL_TABLE_POST_PARAMETERS = "parameters";
    /**
     * 数据表API请求体的参数类型-过滤条件
     */
    public static final String NORMAL_TABLE_POST_FILTERS = "filters";

    /**
     * 覆盖导入描述
     */
    public static final String PRE_SWAGGER_CONVERT = "覆盖导入失败：";

    /**
     * 惟客云响应体参数映射规则ID值
     */
    public static final Integer WAKE_CLOUD_RESPONSE_PARAM_MAPPING_RULE_ID = 1;

    /**
     * 默认响应体参数映射规则ID值
     */
    public static final Integer DEFAULT_RESPONSE_PARAM_MAPPING_RULE_ID = 0;

    public static final String SWAGGER_IMPORT_MSG = "本次操作总共执行了%s数据, 成功%s条, 失败%s条";

}
