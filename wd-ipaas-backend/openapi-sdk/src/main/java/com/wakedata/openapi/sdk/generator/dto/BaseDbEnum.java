package com.wakedata.openapi.sdk.generator.dto;

/**
 * @author yiyufeng
 * @title BaseDbEnum
 * @projectName bdp-open
 * @date 2019/8/16 10:22
 * @descprition
 */
public interface BaseDbEnum<T> {
    /**
     * 获取值
     *
     * @return
     */
    T getValue();
}
