package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import org.springframework.stereotype.Service;

@Service
/**
 * @author yiyufeng
 * @title AuthorityMineReq
 * @projectName bdp-open
 * @date
 * @description 审批中处理
 */
public class InApprovalStatusHandler extends AbstractApprovalStatusHandler {
    @Override
    public ApprovalStatusEnum approvalStatus() {
        return ApprovalStatusEnum.IN_APPROVAL;
    }

    @Override
    public void approvalSuccess(AppApprovalPo approval) {
    }

    @Override
    public void approvalFail(AppApprovalPo approval) {
        notImplement();
    }

}
