package com.wakedata.dw.open.enums;

import lombok.Getter;

/**
 * 消息监听执行服务编排结果枚举类
 *
 * @author wujunqiang
 * @since 2022/10/26 11:26
 */
@Getter
public enum ExecuteStatusEnum {

    /**
     * 执行成功
     */
    SUCCESS(0, "执行成功"),
    /**
     * 执行失败
     */
    FAILED(1, "执行失败"),
    /**
     * 执行异常
     */
    EXCEPTION(2, "执行异常");

    private final Integer code;

    private final String desc;

    ExecuteStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
