package com.wakedata.dw.open.model.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author wq
 * @title DataAccessApprovalDo
 * @date 2020/9/2 16:00
 * @projectName bdp-open
 * @description
 */
@Data
public class DataAccessApprovalDo {
    /**
     * 审批id
     */
    private Integer approvalId;
    /**
     * 接入名称
     */
    private String accessAppName;
    /**
     * 授权人
     */
    private String approvalInCharge;
    /**
     * 授权时间
     */
    private Date approvalTime;
}
