package com.wakedata.dw.open.service.impl.api.strategy;

/**
 * @author ZhangXueJun
 * @title ApiInvorkStrategy
 * @date 2021/5/27 17:40
 * @projectName dw-open
 * @description
 */
public interface ApiInvokeStrategy<T> {

    T invoke();

}
