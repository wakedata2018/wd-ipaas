package com.wakedata.dw.open.service.impl.xxljob.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.function.FuncExecutor;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.input.PathMapping;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.xxljob.XxlJobDO;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.parammapping.XxlJobTaskParamMapping;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;
import com.wakedata.dw.open.service.impl.utils.ParamBuildUtil;
import org.apache.commons.lang3.StringUtils;
import org.noear.snack.ONode;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author WangChenSheng
 * @descriptor 定时任务的参数相关helper类
 * @title XxlJobTaskParamHelper
 * @date 2022/10/27 16:48
 */
@Component
public class XxlJobTaskParamHelper {

    /**
     * 构建HEAD参数
     *
     * @param apiHeadParams apiHeadParams
     * @return Map<String, String>
     */
    public Map<String, String> buildHeadParams(List<XxlJobTaskParamMapping> apiHeadParams) {
        Map<String, String> headParams = new HashMap<>();
        for (XxlJobTaskParamMapping apiHeadParam : apiHeadParams) {
            buildXxlMappingValue(apiHeadParam);
            headParams.put(apiHeadParam.getField(), apiHeadParam.getValue());
        }
        return headParams;
    }

    /**
     * 构建QUERY参数
     *
     * @param apiQueryParams apiQueryParams
     * @return Map<String, Object>
     */
    public Map<String, Object> buildQueryParams(List<XxlJobTaskParamMapping> apiQueryParams) {
        Map<String, Object> queryParams = new HashMap<>();
        for (XxlJobTaskParamMapping apiQueryParam : apiQueryParams) {
            buildXxlMappingValue(apiQueryParam);
            queryParams.put(apiQueryParam.getField(), apiQueryParam.getValue());
        }
        return queryParams;
    }

    /**
     * 构建BODY参数
     *
     * @param apiBodyParams body参数
     * @return JSON
     */
    public JSON buildBodyParams(XxlJobTaskParamMapping apiBodyParams) {
        if (Objects.isNull(apiBodyParams) || StringUtils.isBlank(apiBodyParams.getJsonSchema())){
            return new JSONObject();
        }

        // 获取type = BODY的参数
        List<ApiRespParamDTO> apiRespParamDTOList = ParamBuildUtil.buildTree(apiBodyParams.getJsonSchema(), DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        List<ApiRespParamDTO> bodyList = new ArrayList<>();
        for (ApiRespParamDTO apiRespParamDTO : apiRespParamDTOList) {
            if (Objects.equals(apiRespParamDTO.getType(), HttpParamKind.BODY)){
                bodyList = apiRespParamDTO.getChildApiRespParams();
            }
        }

        // 处理参数映射
        return ParamMappingsUtils.alignMultiJsonArray(
                ParamMappingsUtils.alignFieldValues(
                        ParamBuildUtil.initMultiInputMappings(), ParamBuildUtil.convertRespParamMapping(bodyList, HttpParamKind.BODY)));
    }


    /**
     * 请求参数格式转换(List<Object> -> json)
     */
    public void convertParamsToJson(XxlJobDTO xxlJobDTO, XxlJobDO xxlJobDO){
        xxlJobDO.setApiHeadParam(JSONObject.toJSONString(xxlJobDTO.getApiHeadParams()));
        xxlJobDO.setApiQueryParam(JSONObject.toJSONString(xxlJobDTO.getApiQueryParams()));
        xxlJobDO.setApiBodyParam(JSONObject.toJSONString(xxlJobDTO.getApiBodyParams()));
    }

    /**
     * 请求参数格式转换(json -> List<Object>)
     */
    public void convertParamsToObject(XxlJobDO xxlJobDO){
        xxlJobDO.setApiHeadParams(JSONArray.parseArray(xxlJobDO.getApiHeadParam(), XxlJobTaskParamMapping.class));
        xxlJobDO.setApiQueryParams(JSONArray.parseArray(xxlJobDO.getApiQueryParam(), XxlJobTaskParamMapping.class));
        xxlJobDO.setApiBodyParams(JSONObject.parseObject((xxlJobDO.getApiBodyParam()),XxlJobTaskParamMapping.class));
    }

    /**
     * 处理函数值和固定值
     */
    private void buildXxlMappingValue(XxlJobTaskParamMapping xxlJobTaskParamMapping){

        JSON originJson = ParamMappingsUtils.multiInputMappingsConvert(ParamBuildUtil.initMultiInputMappings());
        ONode ctxNode = ONode.load(new HashMap<>());
        PathMapping.setByPath(ctxNode, IFunc.SEPARATOR_ASTERISK, originJson, false);

        // 设置value
        xxlJobTaskParamMapping.setValue(
                Objects.equals(xxlJobTaskParamMapping.getType(), ParamMappingTypeEnum.FUNCTION_TYPE.getType())
                        ?(String) FuncExecutor.getInstance().exec(ctxNode, xxlJobTaskParamMapping.getExpression())
                        : xxlJobTaskParamMapping.getFixedValue()
        );
    }

}
