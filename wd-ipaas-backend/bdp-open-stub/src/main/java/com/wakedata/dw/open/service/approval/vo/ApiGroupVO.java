package com.wakedata.dw.open.service.approval.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 接口分组列表数据
 *
 * @author wangchensheng
 * @since 2022/8/3 7:05 PM
 */
@Data
@ApiModel("接口分组列表数据")
public class ApiGroupVO {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("接口分组名称")
    private String groupName;

    @ApiModelProperty("接口分组编码")
    private String groupCode;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("接口分组公共路径")
    private String groupPath;

    @ApiModelProperty("接口分组描述")
    private String groupDesc;

    @ApiModelProperty("父id")
    private Integer parentId;

    @ApiModelProperty("等级")
    private Integer level;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
