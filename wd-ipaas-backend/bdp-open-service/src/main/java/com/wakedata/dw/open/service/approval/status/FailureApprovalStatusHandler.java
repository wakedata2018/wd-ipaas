package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import org.springframework.stereotype.Service;

/**
 * @author yiyufeng
 * @title FailureApprovalStatusHandler
 * @projectName bdp-open
 * @date
 * @description 审批不通过处理
 */
@Service
public class FailureApprovalStatusHandler extends AbstractApprovalStatusHandler {
    @Override
    public ApprovalStatusEnum approvalStatus() {
        return ApprovalStatusEnum.FAILURE_APPROVAL;
    }

    @Override
    public void approvalSuccess(AppApprovalPo approval) {
        notImplement();
    }

    @Override
    public void approvalFail(AppApprovalPo approval) {
        notImplement();
    }

}
