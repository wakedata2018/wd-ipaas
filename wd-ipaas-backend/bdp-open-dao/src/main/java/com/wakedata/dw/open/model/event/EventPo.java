package com.wakedata.dw.open.model.event;

import lombok.Data;

import javax.persistence.*;

/**
 * @author kuangjing@wakedata.com
 * @title 事件配置DO
 * @date 2021/10/22
 * @since v1.0.0
 */
@Data
@Table(name = "DW_OPEN_EVENT")
public class EventPo extends EventBasePo {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 事件名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 事件编码
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 事件描述
     */
    @Column(name = "REMARK")
    private String remark;

    /**
     * 消息最大长度，单位KB
     */
    @Column(name = "MESSAGE_MAX_LENGTH")
    private Integer messageMaxLength;

    /**
     * 状态[0-关闭；1-启用]
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 事件分析扩展点
     */
    @Column(name = "EVENT_EXPAND_POINT")
    private String eventExpandPoint;

    /**
     * 来源地址IDs
     */
    @Column(name = "EVENT_SOURCE_ADDRESSIDS")
    private String eventSourceAddressIds;


    @Column(name = "EVENT_CENTER_ID")
    private String eventCenterId;

    /**
     * 租户ID，因为该类无法继承BaseLesseePo类，所以单独在此类上加上租户ID
     */
    @Column(name = "LESSEE_ID")
    private Long lesseeId;

}
