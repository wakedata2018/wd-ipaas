package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 17:41
 */
@Data
@ApiModel(value = "连接器平台api分组查询条件", description = "连接器平台api分组查询条件")
public class ConnectorApiGroupQuery {

    @ApiModelProperty(value = "api分组id")
    private Long id;

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty(value = "分组名称")
    private String groupName;

}
