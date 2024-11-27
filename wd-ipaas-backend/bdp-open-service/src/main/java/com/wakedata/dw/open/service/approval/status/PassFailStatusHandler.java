package com.wakedata.dw.open.service.approval.status;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import org.springframework.stereotype.Service;

/**
 * @author yiyufeng
 * @title PassFailStatusHandler
 * @projectName bdp-open
 * @date
 * @description 审批异常处理
 */
@Service
public class PassFailStatusHandler extends AbstractApprovalStatusHandler {
    @Override
    public ApprovalStatusEnum approvalStatus() {
        return ApprovalStatusEnum.PASS_FAIL;
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
