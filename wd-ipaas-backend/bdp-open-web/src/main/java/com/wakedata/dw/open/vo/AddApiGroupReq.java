package com.wakedata.dw.open.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author tanzhi
 * @title AddApiGroupReq
 * @date 2019/12/10 15:22
 * @projectName bdp-open
 * @descriptor
 */
@Data
@ApiModel("新增api接口分组")
public class AddApiGroupReq {
    private Integer id;

    @NotNull(message = "groupName")
    @Length(max = 30, message = "groupName最大长度30")
    @ApiModelProperty("接口分组名称")
    private String groupName;

    @NotNull(message = "groupCode")
    @Length(max = 30, message = "groupCode最大长度30")
    @ApiModelProperty("接口分组编码")
    private String groupCode;

    @NotNull(message = "groupPath")
    @Length(max = 225, message = "groupPath最大长度225")
    @ApiModelProperty("接口分组公共路径")
    private String groupPath;

    @Length(max = 225, message = "groupDesc最大长度225")
    @ApiModelProperty("接口分组描述")
    private String groupDesc;

    private Integer parentId;

    @ApiModelProperty("层级")
    private Integer level;

}
