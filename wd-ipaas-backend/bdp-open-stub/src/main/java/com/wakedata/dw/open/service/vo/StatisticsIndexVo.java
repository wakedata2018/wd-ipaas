package com.wakedata.dw.open.service.vo;

import com.wakedata.dw.open.model.domain.AppAccessDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author tanzhi
 * @title StatisticsIndexVo
 * @date 2019/12/4 14:28
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class StatisticsIndexVo {

    @ApiModelProperty("平台数量")
    private Integer platformTotal;

    @ApiModelProperty("API调用次数")
    private Integer apiTotal;

    @ApiModelProperty("API开放量")
    private Integer published;

    @ApiModelProperty("API申请方")
    private Integer apps;

    @ApiModelProperty("API调用异常数")
    private Integer errors;

    @ApiModelProperty("API访问次数")
    private List<AppAccessDo> groupByAccessMethod;

    @ApiModelProperty("API调用方次数排名")
    private List<AppAccessDo> groupByAccessApp;

    @ApiModelProperty("不同接口分类的API分析")
    private List<AppAccessDo> groupByApiGroup;

    @ApiModelProperty("API调用时长排名")
    private List<AppAccessDo> groupByElapsed;

    @ApiModelProperty("API调用错误类型分布")
    private List<AppAccessDo> groupByResultCode;

}
