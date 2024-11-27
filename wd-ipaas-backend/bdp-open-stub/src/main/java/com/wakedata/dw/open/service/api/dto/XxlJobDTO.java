package com.wakedata.dw.open.service.api.dto;

import com.wakedata.dw.open.parammapping.XxlJobTaskParamMapping;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobDTO
 * @date 2022/10/25 12:13
 */
@Data
public class XxlJobDTO implements Serializable {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("接入应用的id")
    @NotNull(message = "dataAssetAppId不能为空")
    private Integer dataAssetAppId;

    @ApiModelProperty("数据apiId")
    @NotNull(message = "dataAssetApiId不能为空")
    private Integer dataAssetApiId;

    @ApiModelProperty("接口分组id")
    @NotNull(message = "apiGroupId不能为空")
    private Integer apiGroupId;

    @ApiModelProperty("api请求头参数")
    private List<XxlJobTaskParamMapping> apiHeadParams;

    @ApiModelProperty("apiQuery参数")
    private List<XxlJobTaskParamMapping> apiQueryParams;

    @ApiModelProperty("api请求体")
    private XxlJobTaskParamMapping apiBodyParams;

    @ApiModelProperty("任务名称")
    @NotNull(message = "任务名称不能为空")
    private String taskName;

    @ApiModelProperty("任务状态：0 禁用 1 启用")
    private Integer taskType;

    @ApiModelProperty("任务执行类型：1 永久 2 自定义")
    @NotNull(message = "任务执行类型不能为空")
    private Integer taskExecuteType;

    @ApiModelProperty("自定义起始时间")
    private Date taskStartTime;

    @ApiModelProperty("自定义结束时间")
    private Date taskEndTime;

    @ApiModelProperty("执行次数")
    private Long taskExecuteAmount;

    @ApiModelProperty("定时任务表达式")
    @NotNull(message = "定时任务表达式不能为空")
    private String taskCron;

    @ApiModelProperty("定时任务描述")
    private String taskDesc;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("定时任务唯一标识：时间戳")
    private String xxlTimeStamp;

    @ApiModelProperty("最后一次执行时间")
    private Date executeTime;

    @ApiModelProperty("app名称")
    private String dataAssetAppName;

    @ApiModelProperty("跳过api下线校验")
    private Boolean isSkip;

}
