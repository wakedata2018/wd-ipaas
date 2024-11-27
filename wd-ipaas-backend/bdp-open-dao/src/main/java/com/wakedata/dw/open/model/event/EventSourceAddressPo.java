package com.wakedata.dw.open.model.event;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author wanghu@wakedata.com
 * @title 事件源DO
 * @date 2021/7/22
 * @since v1.0.0
 */
@Data
@Table(name = "DW_OPEN_EVENT_SOURCE_ADDRESS")
public class EventSourceAddressPo extends BasePo{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 是否启用
     */
    @Column(name = "ENABLE")
    private Boolean enable;

    /**
     * kafka/mq 地址
     */
    @Column(name = "BOOTSTRAP_SERVERS")
    private String bootstrapServers;

    /**
     * kafka/mq 账户
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * kafka/mq 密码
     */
    @Column(name = "PASSWORD")
    private String password;

    /**
     * <p>
     * 地址类型
     */
    @Column(name = "EVENT_TYPE")
    private Integer eventType;

    /**
     * topics，多个topicId 逗号隔开
     */
    @Column(name = "TOPICS")
    private String topics;


    @Column(name = "EVENT_CENTER_ID")
    private String eventCenterId;


}
