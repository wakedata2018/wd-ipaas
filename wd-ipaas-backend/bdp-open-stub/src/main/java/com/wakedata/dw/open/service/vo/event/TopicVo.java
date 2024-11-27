package com.wakedata.dw.open.service.vo.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@ApiModel("topic")
public class TopicVo extends EventBaseVo{
    @ApiModelProperty("topic id")
    private Integer id;

    @ApiModelProperty("地址类型")
    @NotNull(message = "订阅地址类型不能为空")
    private Integer addressType;

    @ApiModelProperty("topic")
    @NotBlank(message = "topic不能为空")
    private String topic;

    @ApiModelProperty("过滤标签")
    private String tag;

    @ApiModelProperty("描述")
    private String desc;

    @ApiModelProperty("topic的来源方式，0：订阅topic,1:仓库topic")
    private Integer addressWay;

    @ApiModelProperty("事件中心DW_OPEN_EVENT_SUBSCRIBE_ADDRESS表对应的id")
    private String eventCenterId;






}
