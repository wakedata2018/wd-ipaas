package com.wakedata.dw.open.liteflow.component.param.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.liteflow.component.param.AbstractParamMappingsStrategy;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 用于入边为start的 算子参数映射
 *
 * @author ZhangXueJun
 * @title SourceRequestParamStrategy
 * @date 2021/5/13 17:27
 * @projectName dw-open
 * @description
 */
public class WithoutInputParamMappingsStrategy extends AbstractParamMappingsStrategy {
    public WithoutInputParamMappingsStrategy(JSONObject startInputParams) {
        super(startInputParams);
    }

    @Override
    public JSON getParamMappingsResult(List<? extends AbstractParamMapping> paramMappings) {
        if (CollectionUtils.isEmpty(paramMappings)) {
            return startInputParams;
        }

        JSON jsonObject = ParamMappingsUtils.mappingJson(startInputParams, paramMappings);
        return startInputParams;
    }
}
