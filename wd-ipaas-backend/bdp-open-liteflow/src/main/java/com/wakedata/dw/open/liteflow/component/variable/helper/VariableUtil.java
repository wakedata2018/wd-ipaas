package com.wakedata.dw.open.liteflow.component.variable.helper;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.utils.ParamBuildUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author WangChenSheng
 * @descriptor
 * @title VariableHelper
 * @date 2022/11/15 19:35
 */
@Component
public class VariableUtil {

    public static final String PRE_JSON_PATH = "$.";

    /**
     * 构建树
     *
     * @param parentParams parentParams
     * @param parentIdToApiRes parentIdToApiRes
     * @return List<ApiRespParamDTO>
     */
    public static List<ApiRespParamDTO> buildApiRespParamTree(List<ApiRespParamDTO> parentParams, Map<Integer, List<ApiRespParamDTO>> parentIdToApiRes) {

        if (CollectionUtils.isEmpty(parentParams)) {
            return Collections.emptyList();
        }

        List<ApiRespParamDTO> paramDtoList = new ArrayList<>(parentParams.size());
        for (ApiRespParamDTO parentParam : parentParams) {
            ApiRespParamDTO parentParamDTO = new ApiRespParamDTO();
            BeanUtil.copyProperties(parentParam, parentParamDTO);

            // 获取子树
            List<ApiRespParamDTO> childApiRespParams = parentIdToApiRes.get(parentParam.getId());
            parentParamDTO.setChildApiRespParams(buildApiRespParamTree(childApiRespParams, parentIdToApiRes));

            paramDtoList.add(parentParamDTO);
        }

        return paramDtoList;
    }

    /**
     * 将jsonSchema转换成json的格式
     *
     * @param apiFlowSlot 上下文参数
     * @param apiRespParamDTOList 变量算子参数树
     * @return 变量算子参数
     */
    public static JSON convertParams(ApiFlowSlot apiFlowSlot, List<ApiRespParamDTO> apiRespParamDTOList) {
        // 获取上下文参数
        Map<String, JSON> contextParamMappings = convertContextParamMappings(apiFlowSlot.getFullOperatorResults());

        return ParamMappingsUtils.alignMultiJsonArray(
                ParamMappingsUtils.alignFieldValues(
                        contextParamMappings, ParamBuildUtil.convertRespParamMapping(apiRespParamDTOList, HttpParamKind.BODY)));
    }

    /**
     * 转换参数上下文对象结构
     *
     * @param fullOperatorResults 全局参数池中的结果参数
     * @return Map<String, JSON>
     */
    public static Map<String, JSON> convertContextParamMappings(JSONObject fullOperatorResults) {
        Map<String, JSON> paramMappings = Maps.newLinkedHashMap();
        for (Map.Entry<String, Object> entry : fullOperatorResults.entrySet()) {
            paramMappings.put(entry.getKey(), (JSON) entry.getValue());
        }
        return paramMappings;
    }

}
