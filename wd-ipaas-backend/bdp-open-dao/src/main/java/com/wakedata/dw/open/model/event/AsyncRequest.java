package com.wakedata.dw.open.model.event;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


/**
 * 异步请求体包装类
 *
 * @author caoshuang
 * @date 2021/10/20
 */
@Data
@Builder
@ToString
public class AsyncRequest {

    private static final long serialVersionUID = 3742138562071709867L;

    /**
     * 订阅地址Id,作为kafka/rocketMq队列名称使用
     */
    private String subscribeAddressId;

    /**
     * 消息体
     */
    private SendEventDTO sendEventDTO;

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * 登录账号名
     */
    private String authAccount;

    /**
     * 登录密码
     */
    private String authPassword;

    /**
     * topic
     */
    private List<TopicPo> topics;

    /**
     * 分区号
     */
    private String partition;

    /**
     * 认证令牌
     */
    private String authToken;

}
