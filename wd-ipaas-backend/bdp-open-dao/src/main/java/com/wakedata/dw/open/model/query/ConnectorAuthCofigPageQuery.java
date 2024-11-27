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
@ApiModel(value = "连接器鉴权配置查询条件", description = "连接器鉴权配置查询条件，支持分页")
public class ConnectorAuthCofigPageQuery extends PageQuery{

    @ApiModelProperty(value = "租户id")
    private Long lesseeId;

    @ApiModelProperty(value = "连接器id")
    private Long connectorId;

    @ApiModelProperty(value = "鉴权配置名称")
    private String name;

    @ApiModelProperty(value = "连接器名称")
    private String connectorName;

    @ApiModelProperty(value = "状态 0：未启用 1：已启用")
    private Integer status;

}
