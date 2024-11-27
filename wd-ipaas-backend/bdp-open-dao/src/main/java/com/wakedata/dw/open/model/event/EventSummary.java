package com.wakedata.dw.open.model.event;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * @author kuangjing@wakedata.com
 * @title 事件配置DO
 * @date 2021/10/22
 * @since v1.0.0
 */
@Data
public class EventSummary extends EventBasePo {

    private Integer id;
    /**
     * 事件名称
     */
    private String name;

    /**
     * 事件编码
     */
    private String code;

    /**
     * 事件描述
     */
    private String remark;

    /**
     * 消息最大长度，单位KB
     */
    private Integer messageMaxLength;

    /**
     * 状态[0-关闭；1-启用]
     */
    private Integer status;

    /**
     * 事件分析扩展点
     */
    private String eventExpandPoint;

    /**
     * 来源地址IDs
     */
    private String eventSourceAddressIds;

    private String eventCenterId;

    private Integer eventType;

}
