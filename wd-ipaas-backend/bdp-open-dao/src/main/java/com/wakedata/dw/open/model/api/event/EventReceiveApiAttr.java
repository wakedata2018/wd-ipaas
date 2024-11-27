package com.wakedata.dw.open.model.api.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author caoshuang
 * @date 2021/10/21
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "dw_open_api_event_receive_attr")
@ToString
public class EventReceiveApiAttr extends AbstractApiAttr {

    /**
     * 事件编码
     */
    @Column(name = "event_code")
    private String eventCode;

    /**
     * MQ类型 2：KAFKA、3：ROCKET_MQ
     *
     * @see com.wakedata.common.mq.enums.RequestProtocolEnum
     */
    @Column(name = "mq_type")
    private Integer mqType;

    /**
     * 集群地址
     */
    @Column(name = "cluster_address")
    private String clusterAddress;

    /**
     * 算子订阅的topic
     */
    @Column(name = "topic")
    private String topic;

    /**
     * 订阅状态
     */
    @Column(name = "sub_status")
    private Integer subStatus;

    /**
     * Api流程的url
     */
    @Column(name = "api_url")
    private String apiUrl;

    /**
     * Api的请求方法
     */
    @Column(name = "api_method")
    private String apiMethod;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
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
     * 算子节点步骤名
     */
    @Column(name = "operator_name")
    private String operatorName;

}