package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobQuery
 * @date 2022/10/25 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("XxlJob调用日志查询条件")
public class XxlJobInvokeLogQuery extends PageQuery {

    @ApiModelProperty("任务名称")
    private Integer taskId;

    @ApiModelProperty("执行状态")
    private Integer executeStatus;

}
