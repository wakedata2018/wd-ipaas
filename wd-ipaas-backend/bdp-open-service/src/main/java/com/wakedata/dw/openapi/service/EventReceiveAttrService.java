package com.wakedata.dw.openapi.service;

/**
 * 接收算子业务接口
 *
 * @author wujunqiang
 * @since 2022/10/22 09:51
 */
public interface EventReceiveAttrService {

    /**
     * 动态注册订阅者
     *
     * @param eventReceiveId 事件接收算子id
     */
    void onlineAddSubscribe(Integer eventReceiveId);

    /**
     * 动态注销订阅者
     *
     * @param id dw_open_api_event_receive_attr.id
     */
    void deleteSubscribe(Integer id);

}
