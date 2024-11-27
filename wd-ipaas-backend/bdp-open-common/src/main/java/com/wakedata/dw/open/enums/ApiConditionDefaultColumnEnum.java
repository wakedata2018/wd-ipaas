package com.wakedata.dw.open.enums;

/**
 * API访问默认参数枚举类
 *
 * @author wujunqiang
 * @since 2022/8/8 4:01 PM
 */
public enum ApiConditionDefaultColumnEnum {

    /**
     * accessToken
     */
    SIGN_ACCESS_TOKEN("accessToken", "accessToken", "string", 32, true, false),

    APP_KEY("appKey", "应用key", "string", 32, true, false),
    /**
     * 流水号
     */
    SEQ_NO("seqNo", "请求流水号，唯一", "string", 32, true, false),
    /**
     * 请求时间戳
     */
    TIMESTAMP("timestamp", "请求时间戳", "string", 32, true, false),
    /**
     * 版本号
     */
    VERSION("version", "版本，默认为1.0", "string", 32, true, false),
    /**
     * hbase rowkey
     */
    ROW_KEY("rowkey", "hbase rowkey", "string", null, true, false),
    /**
     * 分页参数，页码，默认1
     */
    PAGE_NO("pageNo", "页码，默认1", "integer", null, true, false),
    /**
     * 分页参数，页大小，默认10
     */
    PAGE_SIZE("pageSize", "页大小，默认10", "integer", null, true, false),
    /**
     * 是否打印运行日志
     */
    ENABLE_LOG("__enable_log__", "是否打印运行日志", "boolean", null, false, false),
    /**
     * 签名
     */
    SIGN("sign", "签名，使用api测试功能时可忽略", "string", 32, true, false),
    /**
     * 是否访问wsdl（仅WebService类型的API会有此参数）
     */
    ACCESS_WSDL("accessWsdl", "是否访问wsdl", "boolean", null, true, false),

    /**
     * 排序字段
     */
    ORDER_BY("orderBy", "排序字段，支持多个字段，用逗号分隔字段和排序方式，用分号分隔对象，如：id,asc;createTime,desc", "string", null, false, false)
    ;

    /**
     * 对应列（请求参数）
     */
    private final String assetColumns;

    /**
     * 描述
     */
    private final String descriptions;

    /**
     * 数据类型
     */
    private final String assetDatatype;

    /**
     * 长度
     */
    private final Integer assetColumnsLength;

    /**
     * 是否必须
     */
    private final Boolean required;

    /**
     * 是否支持多值
     */
    private final Boolean multiValue;

    ApiConditionDefaultColumnEnum(String assetColumns, String descriptions, String assetDatatype, Integer assetColumnsLength
            , Boolean required, Boolean multiValue) {
        this.assetColumns = assetColumns;
        this.descriptions = descriptions;
        this.assetDatatype = assetDatatype;
        this.assetColumnsLength = assetColumnsLength;
        this.required = required;
        this.multiValue = multiValue;
    }

    public String getAssetColumns() {
        return assetColumns;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public String getAssetDatatype() {
        return assetDatatype;
    }

    public Integer getAssetColumnsLength() {
        return assetColumnsLength;
    }

    public Boolean getRequired() {
        return required;
    }

    public Boolean getMultiValue() {
        return multiValue;
    }
}
