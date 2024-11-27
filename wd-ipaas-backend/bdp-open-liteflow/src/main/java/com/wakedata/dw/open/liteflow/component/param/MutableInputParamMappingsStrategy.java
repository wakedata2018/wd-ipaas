package com.wakedata.dw.open.liteflow.component.param;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ZhangXueJun
 * @title MutableInputRequestParamStrategy
 * @date 2021/5/14 15:05
 * @projectName dw-open
 * @description
 */
public abstract class MutableInputParamMappingsStrategy extends AbstractParamMappingsStrategy {

    public MutableInputParamMappingsStrategy(JSONObject startInputParams) {
        super(startInputParams);
    }
}
