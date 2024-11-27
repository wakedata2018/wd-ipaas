package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * @author wujunqiang
 * @since 2023/2/20 19:22
 */
@Getter
public enum RedisLockConfigTypeEnum implements BaseDbEnum<Integer> {

    /**
     * 数据表API
     */
    NORMAL_TABLE(0),
    /**
     * 自定义SQL API
     */
    CUSTOM_SQL_API(1),
    /**
     * 自定义SQL算子
     */
    CUSTOM_SQL_OPERATOR(2);

    private final Integer value;

    RedisLockConfigTypeEnum(Integer value) {
        this.value = value;
    }

}
