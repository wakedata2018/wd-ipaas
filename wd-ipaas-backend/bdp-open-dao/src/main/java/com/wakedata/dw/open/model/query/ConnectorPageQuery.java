package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/19 16:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "连接器平台查询条件", description = "连接器平台查询条件，支持分页")
public class ConnectorPageQuery extends PageQuery{

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "平台分类id")
    private Long groupId;

    @ApiModelProperty(value = "平台唯一标识")
    private String authType;

    @ApiModelProperty(value = "启用状态")
    private Integer enableStatus;

    @ApiModelProperty(value = "名称")
    private String name;

}
