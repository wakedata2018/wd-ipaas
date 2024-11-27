package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * 审批业务枚举类
 *
 * @author yiyufeng
 * @title ApprovalBusinessTypeEnum
 * @projectName bdp-open
 * @date
 * @description
 */
@Getter
public enum ApprovalBusinessTypeEnum implements BaseDbEnum<Integer> {

    /**
     * 数据访问权限审批
     */
    @Deprecated
    DATA_ACCESS(0, "数据访问"),
    /**
     * 帆软的数据资产权限
     */
    @Deprecated
    FR_DATA_ASSET(1, "自助分析平台数据资产"),
    /**
     * API申请
     */
    API(2, "API申请"),
    /**
     * APP应用授权申请
     */
    APP(3, "应用授权");

    private final Integer value;
    private final String description;

    ApprovalBusinessTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
