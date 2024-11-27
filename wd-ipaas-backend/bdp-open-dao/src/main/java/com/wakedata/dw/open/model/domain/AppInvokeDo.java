package com.wakedata.dw.open.model.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wq
 * @title AppInvokeDo
 * @date 2020/9/14 20:04
 * @projectName bdp-open
 * @description
 */
@Data
@ApiModel
public class AppInvokeDo {
    /**
     * 接入应用id
     */
    @ApiModelProperty("接入应用id")
    private String appId;

    /**
     * 接入应用名称
     */
    @ApiModelProperty("接入应用名称")
    private String appName;

    /**
     * api主题
     */
    @ApiModelProperty("api主题")
    private String apiGroup;

    /**
     * api名称
     */
    @ApiModelProperty("api名称")
    private String apiName;

    /**
     * api调用时间
     */
    @ApiModelProperty("api调用时间")
    private String apiInvokeTime;

    /**
     * api花费时间
     */
    @ApiModelProperty("api花费时间")
    private String takeTime;

    /**
     * 调用状态
     */
    @ApiModelProperty("调用状态")
    private String status;

    @ApiModelProperty("日志id")
    private Integer id;

    @ApiModelProperty("访问ip")
    private String ip;
}
