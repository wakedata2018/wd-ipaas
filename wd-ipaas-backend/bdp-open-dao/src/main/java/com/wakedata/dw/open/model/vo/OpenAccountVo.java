package com.wakedata.dw.open.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luomeng
 * @date 2022/8/3 17:04
 */
@Data
public class OpenAccountVo implements Serializable {
    /**
     * id
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
    /** 密码 */
    private String password ;
    /** 状态（启用、禁用） */
    private Integer status ;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /** 关联角色id */
    private String relateRoleId ;
    /** 是否是管理员 */
    private Integer isAdmin ;
    /** 创建人 */
    private String createBy ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updateBy ;
    /** 更新时间 */
    private Date updateTime ;
}
