package com.wakedata.dw.open.model.query;

import lombok.Data;

/**
 * 授权API列表查询条件
 * @author 佟蕊
 */
@Data
public class AuthApiListQuery {

    /**
     * ipaas租户id
     */
    private Long lesseeId;
    /**
     * 应用id
     */
    private Integer appId;
    /**
     * API id
     */
    private Integer apiId;
    /**
     * 审批业务枚举类(2:API申请 3:应用授权) {@link com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum}
     */
    private Integer approvalBusinessType;
    /**
     * 审批状态(0;新建 1 审批中 2 审批通过 3 审批失败) {@link com.wakedata.dw.open.enums.ApprovalStatusEnum}
     */
    private Integer approvalStatus;
    /**
     * 授权状态（0:未授权 1:已授权） {@link com.wakedata.dw.open.enums.DataAssetEnums.AppAuthStatus}
     */
    private Integer appAuthStatus;

    /**
     * 有效状态（0：无效，1：有效） {@link com.wakedata.dw.open.enums.DataAssetEnums.ActiveStatus}
     */
    private Integer activeStatus;

    /**
     * API 发布状态（0：未发布，1：已发布）{@link com.wakedata.dw.open.enums.DataAssetPublishStatusEnum}
     */
    private Integer dataAssetPublishStatus;

    /**
     * API 发布状态（0：未发布，1：已发布）{@link com.wakedata.dw.open.enums.DataAssetEnums.PublicEnums}
     */
    private Integer secret;

}
