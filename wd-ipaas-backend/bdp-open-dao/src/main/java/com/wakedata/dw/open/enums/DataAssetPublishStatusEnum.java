package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * @author yiyufeng
 * @title DataAssetPublishStatusEnum
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Getter
public enum DataAssetPublishStatusEnum implements BaseDbEnum<Integer> {
    /**
     * 未发布状态
     */
    UN_PUBLISH(0, "未发布"),
    /**
     * 已发布状态
     */
    PUBLISH(1, "已发布");

    private final Integer value;
    private final String description;

    DataAssetPublishStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
