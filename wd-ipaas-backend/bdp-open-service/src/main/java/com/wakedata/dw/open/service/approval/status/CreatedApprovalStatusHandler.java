package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import org.springframework.stereotype.Service;

/**
 * @author yiyufeng
 * @title AuthorityListReq
 * @projectName bdp-open
 * @date
 * @description 新建审批
 */
@Service
public class CreatedApprovalStatusHandler extends AbstractApprovalStatusHandler {
    @Override
    public ApprovalStatusEnum approvalStatus() {
        return ApprovalStatusEnum.CREATED;
    }

    @Override
    public void approvalSuccess(AppApprovalPo approval) {
    }

    @Override
    public void approvalFail(AppApprovalPo approval) {
        notImplement();
    }
}
