package com.wakedata.dw.open.service.lessee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

/**
 * @author luomeng
 * @date 2022/8/2 17:23
 */
@Data
@ApiModel("ipaas账号数据")
public class OpenAccountDTO implements Serializable {

    @ApiModelProperty("id")
    private Long id ;
    /** 租户id */
    @ApiModelProperty("租户id")
    private Long lesseeId ;
    /** 用户账号标识 */
    @ApiModelProperty("用户账号标识")
    @NotEmpty(message = "Ipaas账号不能为空")
    private String userIdentification;
    /** 手机号 */
    @ApiModelProperty("手机号")
    private String phone ;
    /** 用户昵称 */
    @ApiModelProperty("用户昵称")
    private String name ;
    /** 密码 */
    @ApiModelProperty("密码")
    private String password ;
    @ApiModelProperty("是否修改密码 true/false")
    private Boolean isUpdatePassword;
    /** 状态（启用、禁用） */
    @ApiModelProperty("状态（启用、禁用）")
    private Integer status ;
    @ApiModelProperty("关联租户id")
    private Long tenantId;
    @ApiModelProperty("关联租户名称")
    private String tenantName;
    /** 关联角色id */
    @ApiModelProperty("关联角色id")
    @NotEmpty(message = "用户角色不能为空")
    private String relateRoleId ;
    /** 是否是管理员 */
    @ApiModelProperty("是否是管理员")
    private Integer isAdmin ;
    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy ;
    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private Date createTime ;
    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy ;
    /** 更新时间 */
    @ApiModelProperty("更新时间")
    private Date updateTime ;
}
