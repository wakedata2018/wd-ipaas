package com.wakedata.dw.open.service;

/**
 * @author yiyufeng
 * @title ItemOrderCurdService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface ItemOrderCurdService<S> {

    /**
     * 改变顺序
     *
     * @param orderSubject
     * @param toOrderSubject
     * @return
     */
    Integer changeItemOrder(S orderSubject, S toOrderSubject);

    /**
     * 读和操作锁定
     *
     * @param orderSubject
     * @return
     */
    Boolean canReOrderOperateLock(S orderSubject);

    /**
     * 删除项目顺序
     * @param orderSubject
     * @return
     */
    Integer removeOrderItem(S orderSubject);

    /**
     * 添加项目
     * @param orderSubject
     * @return
     */
    Integer appendOrderItem(S orderSubject);
}
