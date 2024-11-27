package com.wakedata.dw.open.liteflow.component.param;

import com.alibaba.fastjson.JSON;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;

import java.util.List;

/**
 * @author ZhangXueJun
 * @title RequestParamStrategy
 * @date 2021/5/13 17:29
 * @projectName dw-open
 * @description
 */
public interface ParamMappingsStrategy {

    /**
     * 获取参数映射后结果
     *
     * @return
     * @param paramMappings
     */
    JSON getParamMappingsResult(List<? extends AbstractParamMapping> paramMappings);
}
