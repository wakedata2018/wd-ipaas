package com.wakedata.dw.open.userinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * ipaas用户信息
 * @author luomeng
 * @date 2022/8/4 10:49
 */
@Data
public class IpaasUserInfo implements Serializable {

    /**
     * ID
     */
    private Long id ;
    /** 租户id */
    private Long lesseeId ;
    /** 用户账号标识 */
    private String userIdentification;
    /** 手机号 */
    private String phone ;
    /** 用户昵称 */
    private String name ;
    /**
     * 关联惟客云租户id
     */
    private Long tenantId;
    /**
     * 关联租户名称
     */
    private String tenantName;
    /** 关联角色id */
    private String relateRoleId ;
    /**
     * 角色标识
     */
    private String roleIdentifier;
    /**
     * 是否是平台管理员
     */
    private Boolean isPlatformAdmin;
    /**
     * sessionId
     */
    private String sessionId;

}