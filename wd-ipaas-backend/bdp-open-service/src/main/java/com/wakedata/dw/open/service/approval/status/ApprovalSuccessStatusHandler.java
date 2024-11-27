package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import org.springframework.stereotype.Service;

/**
 * @author yiyufeng
 * @title ApprovalSuccessStatusHandler
 * @projectName bdp-open
 * @date
 * @description
 * @description 成功审批
 */
@Service

public class ApprovalSuccessStatusHandler extends AbstractApprovalStatusHandler {
    @Override
    public ApprovalStatusEnum approvalStatus() {
        return ApprovalStatusEnum.APPROVAL;
    }

    @Override
    public void approvalSuccess(AppApprovalPo approval) {
        unSupportTransform(ApprovalStatusEnum.APPROVAL);
    }

    @Override
    public void approvalFail(AppApprovalPo approval) {
        notImplement();
    }
}
