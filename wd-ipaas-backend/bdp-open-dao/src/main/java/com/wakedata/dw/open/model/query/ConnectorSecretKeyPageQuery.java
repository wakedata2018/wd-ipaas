package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wujunqiang
 * @since 2022/11/21 16:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "平台密钥查询条件", description = "平台密钥查询条件，支持分页")
public class ConnectorSecretKeyPageQuery extends PageQuery {

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty("是否启用")
    private Integer isEnable;

    @ApiModelProperty(value = "租户ID", hidden = true)
    private Long lesseeId;

}
