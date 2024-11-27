package com.wakedata.dw.open.service.approval.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author WangChenSheng
 * @title ApoAccessVo
 * @date 2022/8/3 23:42
 * @descriptor
 */
@Data
public class AppAccessVo {

    @ApiModelProperty("自增主键")
    private Integer dataAccessAppId;

    @ApiModelProperty("数据访问客户端")
    private String dataAccessAppName;

    @ApiModelProperty("数据访问key")
    private String dataAccessKey;

    @ApiModelProperty("数据访问密钥")
    private String dataAccessSecret;

    @ApiModelProperty("接入端描述")
    private String dataAccessDescription;

    @ApiModelProperty("负责人")
    private String inCharge;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("审批状态")
    private DataAssetEnums.DataAccessAppEnums status;

    @ApiModelProperty("审批意见")
    private String approvalMessage;

    @ApiModelProperty("鉴权方式 0:无鉴权 1：accessToken鉴权 2：连接器鉴权")
    private DataAssetEnums.DataAccessAppAuthType authType;

    @ApiModelProperty("已授权API数")
    private Integer apiNum;

    @ApiModelProperty("应用类型")
    private DataAssetEnums.AppType appType;

    @ApiModelProperty("低代码应用logo")
    private String lowCodeLogo;

    @ApiModelProperty("账号实体")
    private LowCodeAccountPo lowCodeAccountPo;

    @ApiModelProperty("应用发布状态")
    private DataAssetEnums.DataAccessPublishStatus publishStatus;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("鉴权配置id")
    private Long connectorAuthId;

    @ApiModelProperty("应用访问前缀,自定义输入部分.租户id(租户id使用hashids编码)")
    private String dataAccessPrefix;

    @ApiModelProperty("应用访问前缀,自定义输入部分")
    private String prefixCustom;

    @ApiModelProperty("应用访问前缀,租户id(租户id使用hashids编码)")
    private String prefixLesseeId;

    @ApiModelProperty("接口调用地址")
    private String apiAccessPrefix;

}
