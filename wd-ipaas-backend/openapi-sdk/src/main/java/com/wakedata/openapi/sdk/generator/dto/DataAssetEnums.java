package com.wakedata.openapi.sdk.generator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tanzhi
 * @title DataAssetEnums
 * @date 2019/11/29 16:54
 * @projectName bdp-open
 * @descriptor
 */
public class DataAssetEnums {

    @Getter
    public enum ReqMethod implements BaseDbEnum<String> {

        /**
         * 请求方式
         */
        GET("GET"),
        POST("POST");

        ReqMethod(String value) {
            this.value = value;
        }

        private String value;


    }

    @Getter
    public enum ReqProtocol implements BaseDbEnum<Integer> {
        /**
         * 传输协议
         */
        HTTP(1),
        HTTPS(2);

        private Integer value;

        ReqProtocol(Integer value) {
            this.value = value;
        }


    }

    @Getter
    public enum ResContentType implements BaseDbEnum<String> {
        /**
         * 返回值格式
         */
        JSON("application/json"),
        XML("application/xml"),
        ALL("application/json,application/xml");
        private String value;

        ResContentType(String value) {
            this.value = value;
        }
    }

    /**
     * 更新频率枚举类型
     */
    @Getter
    public enum UpdateFrequency implements BaseDbEnum<Integer> {
        /**
         * 小时
         */
        HOUR(1),
        /**
         * 天
         */
        DAY(2),
        /**
         * 周
         */
        WEEK(3),
        /**
         * 月
         */
        MONTH(4),
        /**
         * 年
         */
        YEAR(5),
        /**
         * 其他
         */
        OTHER(6);

        private final Integer value;

        UpdateFrequency(Integer value) {
            this.value = value;
        }

    }

    @Getter
    public enum PublicEnums implements BaseDbEnum<Integer> {

        PUBLIC(1),
        PRIVATE(0);

        private Integer value;

        PublicEnums(Integer value) {
            this.value = value;
        }
    }

    /**
     * 参数类型枚举类
     */
    @Getter
    public enum FiledTypeEnums implements BaseDbEnum<Integer> {
        /**
         * 请求参数
         */
        PARAMETERS(1, "请求参数"),
        /**
         * 返回结果
         */
        RESULT(2, "返回结果");

        private final Integer value;

        private final String desc;

        FiledTypeEnums(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }

    @Getter
    public enum FiledTypeAttrEnums implements BaseDbEnum<Integer> {
        OPERATOR(0),
        FILTER(1);


        private Integer value;

        FiledTypeAttrEnums(Integer value) {
            this.value = value;
        }
    }



    @Getter
    public enum DataAccessAppEnums implements BaseDbEnum<Integer> {
        CREATED(0),
        PASS(1),
        REFUSE(2);
        private Integer value;

        DataAccessAppEnums(Integer value) {
            this.value = value;
        }


    }

    @Getter
    public enum DataAccessAppAuthType implements BaseDbEnum<Integer> {
        NO_AUTH(0, "APP鉴权"),
        TOKEN_AUTH(1, "TOKEN鉴权");
        private Integer value;
        private String desc;

        DataAccessAppAuthType(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }

    @Getter
    public enum DataAccessPublishStatus implements BaseDbEnum<Integer> {
        ON_LINE(0, "上线"),
        OFF_LINE(1, "下线");
        private Integer value;
        private String desc;

        DataAccessPublishStatus(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }


    @Getter
    public enum DataApiAcceptType implements BaseDbEnum<Integer> {
        TOPIC(0, "mq和kafka接收地址"),
        HTTP(1, "http接收地址"),
        NOACCEPT(3, "http接收地址");
        private Integer value;
        private String desc;

        DataApiAcceptType(Integer value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }


    @Getter
    public enum DataApiType implements BaseDbEnum<Integer> {
        /**
         * 自定义SQL API
         */
        CUSTOM_SQL(0),
        /**
         * 单表API
         */
        NORMAL_TABLE(1),
        /**
         * 注册HTTP API
         */
        EXTERNAL_HTTP(2),
        /**
         * 服务编排
         */
        LITE_FLOW(3),
        /**
         * 事件发送
         */
        EVENT_SEND(4),
        /**
         * 事件接收
         */
        EVENT_RECEIVE(5),
        /**
         * 分支算子
         */
        FLOW_BRANCH(6),
        /**
         * WebService
         */
        WEB_SERVICE(7),
        /**
         *
         */
        FLOW_JUDGE(8);
        private final Integer value;

        DataApiType(Integer value) {
            this.value = value;
        }

        public boolean fromDatasource() {
            return this.value <= NORMAL_TABLE.value;
        }

    }

    @Getter
    public enum DataApiOperationType implements BaseDbEnum<Integer> {
        /**
         * 增加
         */
        INSERT(0),

        /**
         * 删除
         */
        DELETE(1),

        /**
         * 修改
         */
        UPDATE(2),

        /**
         * 查询
         */
        QUERY(3);

        private Integer value;

        DataApiOperationType(Integer value) {this.value = value;}

    }


    @Getter
    @AllArgsConstructor
    public enum AppType implements BaseDbEnum<Integer> {
        /**
         * ipaas应用
         */
        IPAAS_APP(0, "ipaas应用"),

        /**
         * 低代码应用
         */
        LOW_CODE_APP(1, "低代码应用");

        private final Integer value;
        private final String desc;
    }

    /**
     * 应用授权类型
     */
    @Getter
    @AllArgsConstructor
    public enum AppAuthType implements BaseDbEnum<Integer> {
        /**
         * 惟客云授权
         */
        WAKE_CLOUD(0, "惟客云授权"),

        /**
         * 其他授权类型
         */
        OTHERS(1, "其他授权类型");

        private final Integer value;
        private final String desc;
    }

    /**
     * 授权状态（未授权/已授权、已解绑）
     */
    @Getter
    @AllArgsConstructor
    public enum AppAuthStatus implements BaseDbEnum<Integer> {
        /**
         * 未授权
         */
        UNAUTHORIZED(0, "未授权"),

        /**
         * 已授权
         */
        AUTHORIZED(1, "已授权");

        private final Integer value;
        private final String desc;
    }

    /**
     * 有效状态（有效/无效）
     */
    @Getter
    @AllArgsConstructor
    public enum ActiveStatus implements BaseDbEnum<Integer> {
        /**
         * 未授权
         */
        DISABLE(0, "无效"),

        /**
         * 已授权
         */
        ACTIVE(1, "有效");

        private final Integer value;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum LowCodeAccessType implements BaseDbEnum<String> {
        /**
         * 低代码预览模式
         */
        LOW_CODE_PREVIEW("preview", "低代码预览模式"),

        /**
         * 低代码发布模式
         */
        LOW_CODE_RELEASE("release", "低代码发布模式");

        private final String value;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum AssetDataTypeEnum implements BaseDbEnum<String> {
        /**
         * json-schema类型，body参数中只有json-schema格式
         */
        JSON("json", "json"),

        /**
         * xml类型
         */
        XML("xml", "xml类型"),

        /**
         * integer类型
         */
        INTEGER("integer", "integer类型"),

        /**
         * boolean类型
         */
        BOOLEAN("boolean", "boolean类型"),

        /**
         * array类型
         */
        ARRAY("array", "array类型"),

        /**
         * object类型
         */
        OBJECT("object", "object类型"),

        /**
         * string类型
         */
        STRING("string", "string类型");

        private final String value;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    public enum ApiResponseType implements BaseDbEnum<Integer> {
        /**
         * 响应体
         */
        BODY(0),
        /**
         * 响应头
         */
        HEAD(1);

        private final Integer value;
    }

    @Getter
    @AllArgsConstructor
    public enum ApiResponseBusinessType implements BaseDbEnum<Integer> {
        /**
         * 普通http响应模板
         */
        HTTP_RESULT(0),
        /**
         * 流程编排结果响应模板
         */
        LITEFLOW_RESULT(1);

        private final Integer value;
    }

    @Getter
    @AllArgsConstructor
    public enum ParamValueType implements BaseDbEnum<Integer> {
        /**
         * 引用类型
         */
        REFERENCE(0),
        /**
         * 固定类型
         */
        FIX(1);

        private final Integer value;
    }

    /**
     * 表达式类型
     */
    @Getter
    @AllArgsConstructor
    public enum ExpressionType implements BaseDbEnum<String> {
        /**
         * 算子之间
         */
        BETWEEN_OPERATOR("BETWEEN_OPERATOR"),
        /**
         * 流程编排结果集类型
         */
        LITEFLOW_RESULT("LITEFLOW_RESULT");

        private final String value;
    }

}