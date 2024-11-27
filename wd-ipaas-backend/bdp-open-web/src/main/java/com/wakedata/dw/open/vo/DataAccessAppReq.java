package com.wakedata.dw.open.vo;

import com.wakedata.dw.open.enums.DataAssetEnums;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author tanzhi
 * @title DataAccessAppReq
 * @date 2019/12/14 21:55
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class DataAccessAppReq {


    private Integer dataAccessAppId;

    @NotNull(message = "dataAccessAppName不能为空")
    @Length(max = 255, message = "dataAccessAppName最大长度为255")
    private String dataAccessAppName;

    @Length(max = 255, message = "dataAccessDescription最大长度为255")
    private String dataAccessDescription;

    @Length(max = 32, message = "inCharge最大长度为32")
    private String inCharge;

    private DataAssetEnums.DataAccessAppEnums status;

    /**
     * 授权类型
     */
    private DataAssetEnums.DataAccessAppAuthType authType;

    /**
     * 应用类型
     */
    private DataAssetEnums.AppType appType;

    /**
     * 低代码应用logo
     */
    private String lowCodeLogo;

    /**
     * 低代码应用绑定的wake账号
     */
    private String wakeUserName;

    /**
     * 低代码应用绑定的wake密码
     */
    private String wakePassword;

    /**
     * 应用授权类型 WAKE_CLOUD：惟客云授权，OTHERS：其他授权
     */
    @ApiModelProperty("应用授权类型 WAKE_CLOUD：惟客云授权，OTHERS：其他授权")
    private DataAssetEnums.AppAuthType appAuthType;

    /**
     * 授权配置，json串形式，如惟客云应用授权 ：{"id": 1,"tenantId": 1,"appName": "绿城中国"}
     */
    @ApiModelProperty("授权配置，json串形式，如惟客云应用授权：{\"id\": 1,\"tenantId\": 1,\"appName\": \"绿城中国\"}")
    private String apiAuthConfig;

    @ApiModelProperty("鉴权配置id")
    private Long connectorAuthId;


}
