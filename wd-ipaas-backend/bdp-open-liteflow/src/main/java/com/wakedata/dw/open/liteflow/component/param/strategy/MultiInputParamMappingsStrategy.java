package com.wakedata.dw.open.liteflow.component.param.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.parammapping.ResponseKind;
import com.wakedata.dw.open.liteflow.component.param.MutableInputParamMappingsStrategy;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ZhangXueJun
 * @title InputRequestParamStrategy
 * @date 2021/5/13 17:38
 * @projectName dw-open
 * @description
 */
public class MultiInputParamMappingsStrategy extends MutableInputParamMappingsStrategy {

    @Setter
    private Map<String, JSON> multiInputMappings = Maps.newLinkedHashMap();

    public MultiInputParamMappingsStrategy(JSONObject startInputParams) {
        super(startInputParams);
    }

    @Override
    public JSON getParamMappingsResult(List<? extends AbstractParamMapping> paramMappings) {
        ParamMappingsUtils.MappingFieldResults fieldResults = ParamMappingsUtils.MappingFieldResults.getDefault();

        Map<String, ? extends List<? extends AbstractParamMapping>> groupRequestMappings = paramMappings.stream()
                .filter(Objects::nonNull)
                .collect(
                Collectors.groupingBy(
                        AbstractParamMapping::getOperatorId, Collectors.toList())
        );

        for (Map.Entry<String, ? extends List<? extends AbstractParamMapping>> mapEntry : groupRequestMappings.entrySet()) {
            List<? extends AbstractParamMapping> mappings = mapEntry.getValue();

            ParamMappingsUtils.MappingFieldResults results = ParamMappingsUtils.alignFieldValues(multiInputMappings, mappings);

            if (results.getResponseKind() == ResponseKind.JSON_ARRAY) {
                fieldResults.setResponseKind(results.getResponseKind());
            }

            fieldResults.getFieldValues().putAll(results.getFieldValues());

            if (results.getMaxRows() > fieldResults.getMaxRows()) {
                fieldResults.setMaxRows(results.getMaxRows());
            }
        }
        return ParamMappingsUtils.alignMultiJsonArray(fieldResults);
    }
}
