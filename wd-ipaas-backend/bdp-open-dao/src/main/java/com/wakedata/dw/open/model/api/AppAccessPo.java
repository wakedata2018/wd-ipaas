package com.wakedata.dw.open.model.api;

import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.BaseLesseePo;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tanzhi
 * @title DataAccessAppPo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
@Table(name = "DW_OPEN_APP_ACCESS")
@ApiModel("应用配置")
public class AppAccessPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_ACCESS_APP_ID")
    @ApiModelProperty("自增主键")
    private Integer dataAccessAppId;

    @Column(name = "DATA_ACCESS_APP_NAME")
    @ApiModelProperty("数据访问客户端")
    private String dataAccessAppName;

    @Column(name = "DATA_ACCESS_KEY")
    @ApiModelProperty("数据访问key")
    private String dataAccessKey;

    @Column(name = "DATA_ACCESS_SECRET")
    @ApiModelProperty("数据访问密钥")
    private String dataAccessSecret;

    @Column(name = "DATA_ACCESS_DESCRIPTION")
    @ApiModelProperty("接入端描述")
    private String dataAccessDescription;

    @Column(name = "IN_CHARGE")
    @ApiModelProperty("负责人")
    private String inCharge;

    @Column(name = "USERNAME")
    @ApiModelProperty("用户名")
    private String username;

    @Column(name = "STATUS")
    @ApiModelProperty("审批状态")
    private DataAssetEnums.DataAccessAppEnums status;

    @Column(name = "APPROVAL_MESSAGE")
    @ApiModelProperty("审批意见")
    private String approvalMessage;

    @Column(name = "DATA_ACCESS_AUTH_TYPE")
    @ApiModelProperty("鉴权方式")
    private DataAssetEnums.DataAccessAppAuthType authType;

    @Transient
    @ApiModelProperty("已授权API数")
    private Integer apiNum;

    /**
     * 应用类型
     */
    @Column(name = "APP_TYPE")
    @ApiModelProperty("应用类型")
    private DataAssetEnums.AppType appType;

    /**
     * 低代码应用logo
     */
    @Column(name = "LOW_CODE_LOGO")
    @ApiModelProperty("低代码应用logo")
    private String lowCodeLogo;

    @Transient
    @ApiModelProperty("账号实体")
    private LowCodeAccountPo lowCodeAccountPo;

    @Column(name = "publish_status")
    @ApiModelProperty("应用发布状态")
    private DataAssetEnums.DataAccessPublishStatus publishStatus;

    @Transient
    @ApiModelProperty("创建时间区间-开始时间")
    private String startTime;

    @Transient
    @ApiModelProperty("创建时间区间-结束时间")
    private String endTime;

    @Column(name = "connector_auth_id")
    @ApiModelProperty("鉴权配置id")
    private Long connectorAuthId;

    @Column(name = "data_access_prefix")
    @ApiModelProperty("应用访问前缀,自定义输入部分.租户id(租户id使用hashids编码)")
    private String dataAccessPrefix;

    @Transient
    @ApiModelProperty("应用访问前缀,自定义输入部分")
    private String prefixCustom;
    @Transient
    @ApiModelProperty("应用访问前缀,租户id(租户id使用hashids编码)")
    private String prefixLesseeId;
    @Transient
    @ApiModelProperty("接口调用地址")
    private String apiAccessPrefix;
}