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
@ApiModel("XxlJob分页查询条件DTO")
public class XxlJobQuery extends PageQuery {

    @ApiModelProperty("任务名称")
    private Integer id;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务状态")
    private Integer taskType;

    @ApiModelProperty("定时任务唯一标识：时间戳")
    private String timeStamp;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", hidden = true)
    private Long lesseeId;

}
