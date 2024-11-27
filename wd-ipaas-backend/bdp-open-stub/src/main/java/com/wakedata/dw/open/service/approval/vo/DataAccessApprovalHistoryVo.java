package com.wakedata.dw.open.service.approval.vo;

import lombok.Data;

/**
 * @author yiyufeng
 * @title DataAccessApprovalHistoryVo
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
public class DataAccessApprovalHistoryVo extends ApprovalHistoryVo {

    private String dataAssetName;

    private String dataAssetDescription;

    private String dataAssetApiUri;

    private String dataAccessAppName;

}
