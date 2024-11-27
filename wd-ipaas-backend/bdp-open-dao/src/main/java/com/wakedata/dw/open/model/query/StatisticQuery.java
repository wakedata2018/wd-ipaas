package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author WangChenSheng
 * @descriptor
 * @title StatisticQuery
 * @date 2022/9/15 11:29
 */
@Getter
@Setter
@ToString
@ApiModel("统计相关接口查询条件")
public class StatisticQuery extends PageQuery{

    @ApiModelProperty("主键Id")
    private Integer id;

    @ApiModelProperty("API ID")
    private Integer assetApiId;

    @ApiModelProperty("API名称和apiUrl(联合模糊查询)")
    private String apiName;

    @ApiModelProperty("应用id")
    private Integer appId;

    @ApiModelProperty("应用名称(模糊查询0")
    private String appName;

    @ApiModelProperty("调用状态")
    private Boolean status;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty("调用结果：1 成功 2 失败")
    private Integer invokeStatus;

}
