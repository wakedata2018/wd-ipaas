package com.wakedata.dw.open.service.approval.vo;

import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import lombok.Data;

import java.util.Date;

/**
 * @author yiyufeng
 * @title ApprovalHistoryVo
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
public class ApprovalHistoryVo {

    private Integer approvalId;

    private String submitter;

    private Date submitTime;

    private ApprovalStatusEnum status;

    private String message;

    private String formUrl;

    private String apiName;

    private Integer dataAssetApiId;
    private Integer accessAppId;

    private Boolean isCollect;

    public static void copyApproval2ApprovalHistory(AppApprovalPo approval, ApprovalHistoryVo approvalHistory) {
        approvalHistory.setApprovalId(approval.getApprovalId());
        approvalHistory.setSubmitter(approval.getUserIdentification());
        approvalHistory.setStatus(approval.getApprovalStatus());
        approvalHistory.setSubmitTime(approval.getCreateTime());
        approvalHistory.setMessage(approval.getApprovalMessage());
        approvalHistory.setFormUrl(approval.getFormUrl());
        approvalHistory.setDataAssetApiId(approval.getDataAssetId());
        approvalHistory.setAccessAppId(approval.getAccessAppId());
    }
}
