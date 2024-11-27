package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/9/29 15:48
 */
@Getter
public enum AuditLogResultEnum implements BaseDbEnum<Integer> {
    /**
     * 操作日志结果枚举
     */
    SUCCESS(1, "成功"),
    FAILURE(0, "失败");


    private final Integer value;
    private final String description;

    AuditLogResultEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
