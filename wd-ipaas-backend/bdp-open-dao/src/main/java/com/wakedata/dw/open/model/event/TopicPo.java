package com.wakedata.dw.open.model.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author wanghu@wakedata.com
 * @date 2021/8/18
 * @since v1.0.0
 */
@Getter
@Setter
@Table(name = "DW_OPEN_EVENT_TOPIC")
public class TopicPo extends EventBasePo {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * kafka的topic
     */
    @Column(name = "TOPIC")
    private String topic;
    /**
     * 过滤标签
     */
    @Column(name = "TAG")
    private String tag;


    @Column(name = "REMARK")
    private String desc;

    @Column(name = "ADDRESS_TYPE")
    private Integer addressType;

    @Column(name = "ADDRESS_WAY")
    private Integer addressWay;

    /**
     * 事件中心DW_OPEN_EVENT_SUBSCRIBE_ADDRESS表对应的id
     */
    @Column(name = "EVENT_CENTER_ID")
    private String eventCenterId;

    /**
     * 租户ID，因为该类无法继承BaseLesseePo类，所以单独在此类上加上租户ID
     */
    @Column(name = "LESSEE_ID")
    private Long lesseeId;

}
