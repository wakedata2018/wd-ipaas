package com.wakedata.dw.open.model.lessee;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Ipaas账号-1、开发平台账号，主要用来登录开放平台，做应用和api等的管理，分两个角色，管理员和开发者
 * @author luomeng
 * @date 2022/8/2 16:51
 */
@Data
@Table(name="dw_open_account")
public class OpenAccountPo extends BasePo {

    /** ID */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    /** 租户id */
    private Long lesseeId ;
    /** 用户账号标识 */
    private String userIdentification ;
    /** 手机号 */
    private String phone ;
    /** 用户昵称 */
    private String name ;
    /** 密码 */
    private String password ;
    /** 状态（启用、禁用） */
    private Integer status ;
    /** 关联角色id */
    private String relateRoleId ;
    /** 是否是管理员 */
    private Integer isAdmin ;
    /** 创建人 */
    private String createBy ;
    /** 更新人 */
    private String updateBy ;
}
