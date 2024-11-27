package com.wakedata.dw.open.function.custom;

import java.util.List;

/**
 * 自定义函数获取
 * @author luomeng
 * @date 2022/10/31 20:42
 */
public interface CustomFunctionHelper {

    /**
     * 查找自定义函数数据
     * @param functionName 函数名
     * @param lesseeId 租户ID
     * @return
     */
    CustomFunction getCustomFunctionByName(String functionName, Long lesseeId);

    /**
     * 获取所有已上线的自定义函数
     * @param lesseeId 租户id
     * @return
     */
    List<CustomFunction> getPublishCustomFunctionList(Long lesseeId);
}
