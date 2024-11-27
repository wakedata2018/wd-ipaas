package com.wakedata.dw.open.model.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wq
 * @title ApiWarnDo
 * @date 2020/9/22 16:28
 * @projectName dw-open
 * @description
 */
@Data
public class ApiWarnDo {

    @ApiModelProperty("api告警id")
    private Integer apiWarnId;

    @ApiModelProperty("api告警名称")
    private String apiWarnName;

    @ApiModelProperty("api主题描述")
    private String apiWarnDesc;

    @ApiModelProperty("api主题id")
    private Integer apiGroupId;

    @ApiModelProperty("api 主题")
    private String apiGroup;

    @ApiModelProperty("api Id")
    private Integer apiId;

    @ApiModelProperty("api 名称")
    private String apiName;

    @ApiModelProperty("api 路径")
    private String apiMethod;

    @ApiModelProperty("状态")
    private Boolean status;

    @ApiModelProperty("email")
    private String email;

    @ApiModelProperty("电话号码")
    private String phone;

    @ApiModelProperty("延时超过多少秒")
    private Integer ruleSecond;

    @ApiModelProperty("错误次数超过多少")
    private Integer ruleErrorTimes;

    @ApiModelProperty("告警描述")
    private String warnDesc;

}
