package com.wakedata.dw.open.model.event;

import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

/**
 * 同步请求体包装类
 *
 * @author caoshuang
 * @date 2021/10/20
 */
@Getter
@ToString
public class SyncRequest extends AsyncRequest implements Serializable {

    private static final long serialVersionUID = -2566372277802502433L;

    /**
     * 启用ACK确认
     */
    public final static boolean enableAck = true;


    public SyncRequest(AsyncRequest asyncRequest) {
        super(asyncRequest.getSubscribeAddressId()
                , asyncRequest.getSendEventDTO()
                , asyncRequest.getRequestUrl()
                , asyncRequest.getAuthAccount()
                , asyncRequest.getAuthPassword()
                , asyncRequest.getTopics()
                , asyncRequest.getPartition()
                , asyncRequest.getAuthToken());
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
