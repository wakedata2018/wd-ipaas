package com.wakedata.dw.open.enums;

/**
 * 这个枚举类型标记为作废，使用com.wakedata.common.mq.enums.RequestProtocolEnum
 *
 * @author wanghu@wakedata.com
 * @title 事件源类型枚举
 * @date 2021/7/22
 * @since v1.0.0
 */
@Deprecated
public enum EventSourceTypeEnum {
    /**
     * HTTP
     */
    HTTP(1, "http"),
    KAFKA(2, "kafka"),
    ROCKET_MQ(3, "rocketmq");

    EventSourceTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    private final Integer code;
    private final String type;

    public Integer getValue() {
        return code;
    }

    public String getDesc() {
        return type;
    }
}
