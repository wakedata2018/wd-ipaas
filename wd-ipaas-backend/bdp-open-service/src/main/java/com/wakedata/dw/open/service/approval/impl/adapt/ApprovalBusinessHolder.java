package com.wakedata.dw.open.service.approval.impl.adapt;

import com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.approval.ApprovalBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 * @author yiyufeng
 * @title ApprovalBusinessHolder
 * @projectName bdp-open
 * @date
 * @description
 */
public class ApprovalBusinessHolder {

    @Autowired
    private List<ApprovalBusiness> approvalBusinessList;

    public ApprovalBusiness getApprovalBusinessImpl(ApprovalBusinessTypeEnum approvalBusinessTypeEnum) {
        for (ApprovalBusiness approvalBusiness : approvalBusinessList) {
            if (approvalBusinessTypeEnum == approvalBusiness.getBusinessType()) {
                return approvalBusiness;
            }
        }
        throw new OpenException(String.format("unknown approval type %s", approvalBusinessTypeEnum));
    }

}
