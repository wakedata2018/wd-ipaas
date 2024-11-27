package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

@Getter
/**
 * @author yiyufeng
 * @title ApprovalStatusEnum
 * @projectName bdp-open
 * @date
 * @description
 */
public enum ApprovalStatusEnum implements BaseDbEnum<Integer> {

    /**
     * 新建
     */
    CREATED(0, "未审批"),
    /**
     * 审批中
     */
    IN_APPROVAL(1, "审批中"),
    /**
     * 审批通过
     */
    APPROVAL(2, "已通过"),
    /**
     * 审批失败
     */
    FAILURE_APPROVAL(3, "已拒绝"),
    /**
     * 处理失败
     */
    PASS_FAIL(4, "处理失败"),
    ;

    private Integer value;
    private String description;

    ApprovalStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }
}
