package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum;
import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * api审批表对应实体类
 *
 * @author yiyufeng
 * @title ApprovalPo
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
@Table(name = "dw_open_approval")
public class AppApprovalPo extends BaseLesseePo {

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @Id
    @GeneratedValue(generator = "JDBC", strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Integer approvalId;

    /**
     * 创建审批单的用户标识，申请人
     */
    @ApiModelProperty("创建审批单的用户标识，申请人")
    @Column(name = "user_identification")
    private String userIdentification;

    /**
     * 审批主体
     */
    @ApiModelProperty("审批主体")
    @Column(name = "approval_subject")
    private String approvalSubject;

    /**
     * 审批的业务
     */
    @ApiModelProperty("审批的业务")
    @Column(name = "approval_business_type")
    private ApprovalBusinessTypeEnum approvalBusinessType;

    /**
     * 审批内容
     */
    @ApiModelProperty("审批内容")
    @Column(name = "approval_body")
    private String approvalBody;

    /**
     * 审批状态(0;新建 1 审批中 2 审批通过 3 审批拒绝)
     */
    @ApiModelProperty("审批状态")
    @Column(name = "approval_status")
    private ApprovalStatusEnum approvalStatus;

    /**
     * 审批信息
     */
    @ApiModelProperty("审批信息")
    @Column(name = "approval_message")
    private String approvalMessage;

    /**
     * 数据资产ID
     */
    @ApiModelProperty("数据资产ID")
    @Column(name = "data_asset_api_id")
    private Integer dataAssetId;

    /**
     * 接入应用ID
     */
    @ApiModelProperty("接入应用ID")
    @Column(name = "data_access_app_id")
    private Integer accessAppId;

    /**
     * K2表单地址
     */
    @ApiModelProperty("K2表单地址")
    @Column(name = "form_url")
    private String formUrl;

    /**
     * 应用授权状态，0：未授权，1：已授权
     */
    @ApiModelProperty("应用授权状态，0：未授权，1：已授权")
    @Column(name = "app_auth_status")
    private DataAssetEnums.AppAuthStatus appAuthStatus;

    /**
     * 有效状态（有效/无效）
     */
    @ApiModelProperty("有效状态，0：无效，1：有效")
    @Column(name = "active_status")
    private DataAssetEnums.ActiveStatus activeStatus;
}
