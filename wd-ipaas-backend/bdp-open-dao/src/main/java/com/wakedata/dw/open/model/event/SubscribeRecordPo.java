package com.wakedata.dw.open.model.event;

import com.wakedata.dw.open.model.BasePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wanghu@wakedata.com
 * @title 订阅记录
 * @date 2021/7/23
 * @since v1.0.0
 */
@Data
@Table(name = "DW_OPEN_EVENT_SUBSCRIBE_ADDRESS_RELA")
public class SubscribeRecordPo extends BasePo {


    @Id
    @Column(name = "ID")
    private Integer id;

    /**
     * 关联事件ID
     */
    @Column(name = "EVENT_ID")
    private Integer eventId;

    /**
     * kafka和mq的topic id列表
     */
    @Column(name = "TOPIC_ID")
    private Integer topicId;

    /**
     *接收事件为http的http订阅信息id
     */
    @Column(name = "HTTP_ID")
    private Integer httpId;

    @Column(name = "EVENT_CENTER_ID")
    private String eventCenterId;


    @Column(name ="API_ID")
    private Integer apiId;

}
