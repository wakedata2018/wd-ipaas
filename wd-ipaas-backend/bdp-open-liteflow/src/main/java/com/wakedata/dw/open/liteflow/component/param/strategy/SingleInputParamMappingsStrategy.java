package com.wakedata.dw.open.liteflow.component.param.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.liteflow.component.param.MutableInputParamMappingsStrategy;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;

import java.util.List;

/**
 * @author ZhangXueJun
 * @title InputRequestParamStrategy
 * @date 2021/5/13 17:38
 * @projectName dw-open
 * @description
 */
public class SingleInputParamMappingsStrategy extends MutableInputParamMappingsStrategy {

    public SingleInputParamMappingsStrategy(JSONObject startInputParams) {
        super(startInputParams);
    }

    @Override
    public JSON getParamMappingsResult(List<? extends AbstractParamMapping> paramMappings) {
        return ParamMappingsUtils.mappingJson(startInputParams, paramMappings);
    }
}
