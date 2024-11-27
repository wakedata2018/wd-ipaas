package com.wakedata.dw.open.service.vo.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubscribeRecordVo extends EventBaseVo implements Serializable {

    private Integer id;

    @ApiModelProperty("关联事件ID")
    private int eventId;

    @ApiModelProperty("kafka和mq的topic id列表")
    private Integer topicId;

    @ApiModelProperty("接收事件为http的http订阅信息id")
    private int httpId;

    @ApiModelProperty("是否是http接收方式")
    private Integer isHttpSubscriber;

    @ApiModelProperty("订阅地址类型")
    private Integer addressType;

    @ApiModelProperty("事件中心对应的id，同步成功后回写")
    private String eventCenterId;

    @ApiModelProperty("关联API信息")
    private Integer apiId;

}
