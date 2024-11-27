package com.wakedata.dw.open.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.json.JsonIgnore;

import java.math.BigDecimal;

/**
 * @author tanzhi
 * @title AppAccessDo
 * @projectName bdp-open
 * @date 2019/8/27 9:40
 * @description
 */
@Data
@ApiModel
public class AppAccessDo {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("访问者id")
    private Integer accessAppId;

    @ApiModelProperty("api名称")
    private String primaryName;

    @ApiModelProperty("apiPath")
    private String secondaryName;

    @ApiModelProperty("错误码(API错误统计列表) 或者 成功次数(API调用统计列表)")
    private Integer resultValue;

    @ApiModelProperty("上月的错误码(API错误统计列表) 或者 成功次数(API调用统计列表)")
    private Integer lastResultValue;

    @ApiModelProperty("失败次数")
    private Integer errorValue;

    @ApiModelProperty("调用时间")
    private String time;

    @ApiModelProperty("api接口分组名称")
    private String groupName;

    @ApiModelProperty("平均调用耗时")
    private BigDecimal takeTime;

    @ApiModelProperty("错误发生时间")
    private String updateTime;

    @ApiModelProperty("错误描述")
    private String resultDescription;
    /**
     * 统计调用次数，以便计算平均值
     */
    @JsonIgnore
    @ApiModelProperty("成功率")
    private Integer times;
    @JsonIgnore
    @ApiModelProperty(value = "调用方（应用名称）")
    private String appName;
    @JsonIgnore
    @ApiModelProperty(value = "调用方Id（应用appKey）")
    private String appKey;

}
