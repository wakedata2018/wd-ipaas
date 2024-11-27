package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yiyufeng
 * @title AbstractApprovalStatusHandler
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
public abstract class AbstractApprovalStatusHandler {

    /**
     * 当前能够处理的审批状态节点
     *
     * @return
     */
    public abstract ApprovalStatusEnum approvalStatus();

    /**
     * 审批成功
     *
     * @param approval
     */
    public abstract void approvalSuccess(AppApprovalPo approval);

    /**
     * 未实现处理丢出异常
     */
    protected void notImplement() {
        log.info("申请订单状态转换校验规则未实现");
        throw new OpenException("功能未实现");
    }

    /**
     * 不支持的直接状态转换
     *
     * @param toApprovalStatus
     */
    protected void unSupportTransform(ApprovalStatusEnum toApprovalStatus) {
        throw new OpenException(String.format("状态{%s}不能转换为状态{%s}", this.approvalStatus(), toApprovalStatus));
    }

    /**
     * 审批失败
     *
     * @param approval
     */
    public abstract void approvalFail(AppApprovalPo approval);

}
