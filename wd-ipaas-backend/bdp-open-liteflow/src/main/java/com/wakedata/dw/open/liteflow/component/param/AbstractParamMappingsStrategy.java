package com.wakedata.dw.open.liteflow.component.param;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ZhangXueJun
 * @title ImputedReques
 * @date 2021/5/13 17:24
 * @projectName dw-open
 * @description
 */
public abstract class AbstractParamMappingsStrategy implements ParamMappingsStrategy {

    protected final JSONObject startInputParams;

    public AbstractParamMappingsStrategy(JSONObject startInputParams) {
        this.startInputParams = startInputParams;
    }

    public JSONObject getStartInputParams() {
        return startInputParams;
    }
}
