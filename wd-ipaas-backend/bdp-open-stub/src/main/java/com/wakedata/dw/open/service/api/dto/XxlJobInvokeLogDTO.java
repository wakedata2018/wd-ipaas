package com.wakedata.dw.open.service.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobInvokeLogDTO
 * @date 2022/11/2 17:15
 */
@Data
@ApiModel("定时任务调用日志DTO")
public class XxlJobInvokeLogDTO {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private Integer taskId;

    /**
     * 接入应用的id
     */
    @ApiModelProperty("接入应用的id")
    private Integer dataAssetAppId;

    /**
     * 数据apiId
     */
    @ApiModelProperty("数据apiId")
    private Integer dataAssetApiId;

    /**
     * 接口分组id
     */
    @ApiModelProperty("接口分组id")
    private Integer apiGroupId;

    /**
     * 解析之后的HEAD
     */
    @ApiModelProperty("解析之后的HEAD")
    private String apiHeadParam;

    /**
     * 解析之后的QUERY
     */
    @ApiModelProperty("解析之后的QUERY")
    private String apiQueryParam;

    /**
     * 解析之后的BODY
     */
    @ApiModelProperty("解析之后的BODY")
    private String apiBodyParam;

    /**
     * 执行状态
     */
    @ApiModelProperty("执行状态")
    private Integer invokeStatus;

    /**
     * 执行结果(包含正确执行结果以及错误详情)
     */
    @ApiModelProperty("执行结果(包含正确执行结果以及错误详情)")
    private String invokeResult;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 更新人
     */
    @ApiModelProperty("更新人")
    private String updateBy;
}
