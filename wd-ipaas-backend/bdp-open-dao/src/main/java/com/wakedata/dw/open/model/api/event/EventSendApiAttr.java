package com.wakedata.dw.open.model.api.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.dw.open.model.api.AbstractApiAttr;

import javax.persistence.Column;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author caoshuang
 * @date 2021/10/21
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "dw_open_api_event_send_attr")
@ToString
public class EventSendApiAttr extends AbstractApiAttr {

    /**
     * 事件编码
     */
    @Column(name = "event_code")
    private String eventCode;

    /**
     * 算子发送的topic
     */
    @Column(name = "topic")
    private String topic;

    /**
     * 集群地址
     */
    @Column(name = "cluster_address")
    private String clusterAddress;

    /**
     * MQ类型 2：KAFKA、3：ROCKET_MQ
     *
     * @see com.wakedata.common.mq.enums.RequestProtocolEnum
     */
    @Column(name = "mq_type")
    private Integer mqType;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 租户Id
     */
    @Column(name = "lessee_id")
    private Long lesseeId;

    /**
     * 请求服务端所需的accessKey
     */
    @Column(name = "access_key")
    private String accessKey;

    /**
     * 请求服务端所需的secretKey
     */
    @Column(name = "secret_key")
    private String secretKey;

    /**
     * 标签
     */
    @Column(name = "tag")
    private String tag;

    /**
     * 消息内容模板
     */
    @Column(name = "message_template")
    private String messageTemplate;

    /**
     * 配置名（唯一）
     */
    @Column(name = "config_name")
    private String configName;

}