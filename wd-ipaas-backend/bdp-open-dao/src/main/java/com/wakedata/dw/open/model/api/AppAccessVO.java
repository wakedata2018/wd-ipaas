package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 应用详情基础信息VO
 * @author 佟蕊
 */
@Data
@ApiModel("应用详情基础信息VO")
public class AppAccessVO extends BaseLesseePo {

    @ApiModelProperty("自增主键")
    private Integer dataAccessAppId;

    @ApiModelProperty("应用名称")
    private String dataAccessAppName;

    @ApiModelProperty("数据访问key")
    private String dataAccessKey;

    @ApiModelProperty("数据访问密钥")
    private String dataAccessSecret;

    @ApiModelProperty("应用发布状态")
    private DataAssetEnums.DataAccessPublishStatus publishStatus;

    @ApiModelProperty("已授权api数量")
    private Long authedApiCount;

    @ApiModelProperty("未授权api数量")
    private Long unAuthApiCount;

    @ApiModelProperty("已授权应用名称")
    private String authorizedAppName;


    @ApiModelProperty("鉴权配置id")
    private Long connectorAuthId;

    @ApiModelProperty("鉴权配置名称")
    private String connectorAuthName;

    @ApiModelProperty("应用访问前缀,自定义输入部分.租户id(租户id使用hashids编码)")
    private String dataAccessPrefix;

    @ApiModelProperty("应用访问前缀,自定义输入部分")
    private String prefixCustom;

    @ApiModelProperty("应用访问前缀,租户id(租户id使用hashids编码)")
    private String prefixLesseeId;

    @ApiModelProperty("接口调用地址")
    private String apiAccessPrefix;

    @ApiModelProperty("鉴权方式 0:无鉴权 1：accessToken鉴权 2：连接器鉴权")
    private DataAssetEnums.DataAccessAppAuthType authType;

}