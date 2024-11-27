package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 17:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "连接器平台api分页查询条件", description = "连接器平台api分页查询条件")
public class ConnectorApiPageQuery extends PageQuery{

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty(value = "接口分组id")
    private Long apiGroupId;

    @ApiModelProperty(value = "api名称")
    private String apiName;

    @ApiModelProperty(value = "api地址")
    private String apiMethod;

}
