package com.wakedata.dw.open.service.lessee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author luomeng
 * @Description ipaas登录用户信息
 * @createTime 2022-08-07 09:47:00
 */
@Data
@ApiModel(value = "ipaas用户信息")
public class IpaasUserInfoDTO implements Serializable {

    /**
     * ID
     */
    @ApiModelProperty(value = "id")
    private Long id ;
    /** 租户id */
    @ApiModelProperty(value = "租户id")
    private Long lesseeId ;

    @ApiModelProperty(value = "hashids加密后的租户id")
    private String lesseeIdCode;

    /** 用户账号标识 */
    @ApiModelProperty(value = "用户账号标识")
    private String userIdentification;
    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    private String phone ;
    /** 用户昵称 */
    @ApiModelProperty(value = "用户昵称")
    private String name ;
    /**
     * 关联惟客云租户id
     */
    @ApiModelProperty(value = "关联惟客云租户id")
    private Long tenantId;
    /**
     * 关联租户名称
     */
    @ApiModelProperty(value = "关联租户名称")
    private String tenantName;
    /** 关联角色id */
    @ApiModelProperty(value = "关联角色id")
    private String relateRoleId ;
    /**
     * 角色标识
     */
    @ApiModelProperty(value = "角色标识")
    private String roleIdentifier;
    /**
     * 是否是平台管理员
     */
    @ApiModelProperty(value = "是否是平台管理员")
    private Boolean isPlatformAdmin;
    /**
     * sessionId
     */
    @ApiModelProperty(value = "sessionId")
    private String sessionId;


}
