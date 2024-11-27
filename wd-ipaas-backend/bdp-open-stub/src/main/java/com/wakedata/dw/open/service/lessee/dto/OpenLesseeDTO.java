package com.wakedata.dw.open.service.lessee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luomeng
 * @date 2022/8/2 17:24
 */
@Data
@ApiModel("ipaas租户数据")
public class OpenLesseeDTO implements Serializable {

    @ApiModelProperty("id")
    private Long id ;
    /** 名称 */
    @ApiModelProperty("名称")
    private String name ;
    /** 可创建应用数 */
    @ApiModelProperty("可创建应用数")
    private Integer appNum ;
    /** 关联惟客云租户id */
    @ApiModelProperty("关联惟客云租户id")
    private Long tenantId ;
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
