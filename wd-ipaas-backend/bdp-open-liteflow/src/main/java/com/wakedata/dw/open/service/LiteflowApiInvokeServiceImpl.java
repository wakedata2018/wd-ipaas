package com.wakedata.dw.open.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.exception.OperatorOpenException;
import com.wakedata.dw.open.liteflow.CustomerApiJsonFlowParse;
import com.wakedata.dw.open.liteflow.DAGTaskEngine;
import com.wakedata.dw.open.liteflow.LiteFlowApiChainThreadLocal;
import com.wakedata.dw.open.liteflow.LiteflowApiInvokeService;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.liteflow.utils.LiteFlowUtils;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.parammapping.*;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.DataApiAccessServiceImpl;
import com.wakedata.dw.open.service.impl.api.GlobalParamServiceImpl;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.enums.FlowParserTypeEnum;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.property.LiteflowConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 编排执行
 * @author luomeng
 * @date 2022/12/14 14:28
 */
@Slf4j
public class LiteflowApiInvokeServiceImpl implements LiteflowApiInvokeService {

    @Resource
    private DataApiAccessServiceImpl dataApiAccessService;
    @Resource
    private GlobalParamServiceImpl globalParamService;

    @Override
    public JSONObject invokeApi(ApiFlowAttr apiAttr,Integer dataAssetApiId, Integer appId, JSONObject requestParams) {

        DAGTaskEngine.OperatorContainer operatorContainer =
                DAGTaskEngine.execute(apiAttr, appId, requestParams, dataApiAccessService, globalParamService);
        FlowExecutor executor = new FlowExecutor();
        LiteflowConfig liteflowConfig = new LiteflowConfig();

        JSONObject queryParam = requestParams.getJSONObject(HttpParamKind.QUERY.name());
        boolean enableLog = queryParam != null && queryParam.containsKey(RequestParamUtils.ENABLE_LOG) && queryParam.getBoolean(RequestParamUtils.ENABLE_LOG);
        if (enableLog) {
            liteflowConfig.setEnableLog(true);
        } else {
            liteflowConfig.setEnableLog(false);
        }
        LiteFlowApiChainThreadLocal.setApiChainKey(apiAttr.getApiId().longValue());
        dataApiAccessService.getApiFlowAttrService().saveLiteFlowIfNecessary(apiAttr, false);
        String ruleSource = String.format("%s:%s", FlowParserTypeEnum.TYPE_EL_JSON.getType(), CustomerApiJsonFlowParse.class.getName());
        liteflowConfig.setRuleSource(ruleSource);
        executor.setLiteflowConfig(liteflowConfig);
        executor.init(false);
        LiteflowResponse response = executor.execute2Resp(
                LiteFlowUtils.getMainChainName(apiAttr.getApiId()),
                operatorContainer,
                ApiFlowSlot.class
        );

        ApiFlowSlot apiFlowSlot = response.getContextBean(ApiFlowSlot.class);

        if (apiFlowSlot.isHappenError()) {
            if (apiFlowSlot.getException() instanceof OperatorOpenException) {
                OperatorOpenException operatorOpenException = (OperatorOpenException) apiFlowSlot.getException();
                throw new OpenException(OpenApiMsgCodeEnum.w_liteflow_exception_msg.getCode(),"API编排执行【" + operatorOpenException.getOperatorName() + "】步骤发生错误：\n"
                        + ExceptionUtils.getStackTrace(operatorOpenException.getCause()));
            }
            throw new OpenException(OpenApiMsgCodeEnum.w_liteflow_exception_msg.getCode(),"API编排执行失败：\n" + ExceptionUtils.getStackTrace(apiFlowSlot.getException()));
        }
        // execute2Resp之后执行，确保所有算子已经执行完毕
        JSONObject results = getFinalResult(apiFlowSlot,dataAssetApiId);
        String timeStr = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        apiFlowSlot.addLog(timeStr + " 算子[end]: 执行完成，返回结果：\n" + ParamMappingsUtils.prettyFormatJson(results));
        if (liteflowConfig.getEnableLog()) {
            results.put(RequestParamUtils.ENABLE_LOG, apiFlowSlot.getNotepad());
        }
        return results;
    }

    /**
     * 按参数模板获取最终结果集
     *
     * @param apiFlowSlot liteflow上下文
     * @return
     */
    public JSONObject getFinalResult(ApiFlowSlot apiFlowSlot,Integer dataAssetApiId) {
        String timeStr = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        apiFlowSlot.addLog(timeStr + " 算子[end]: 执行开始，获取所有数据上下文：\n" + ParamMappingsUtils.prettyFormatJson(apiFlowSlot.getFullOperatorResults()));
        JSONObject jsonObject = new JSONObject();
        // 获取所有上下文参数
        Map<String, JSON> contextParamMappings = convertContextParamMappings(apiFlowSlot.getFullOperatorResults());
        //结果参数模板
        Map<HttpParamKind, List<ApiRespParamDTO>> groupResponseParams = getApiResponseParamListMap(dataAssetApiId);
        //head参数只有key-value形式，body参数不需要解析根节点
        jsonObject.put(HttpParamKind.HEAD.name(),handleRespParamMapping(contextParamMappings,groupResponseParams.get(HttpParamKind.HEAD),HttpParamKind.HEAD));
        List<ApiRespParamDTO> bodyRespParams = groupResponseParams.get(HttpParamKind.BODY);
        JSON bodyResponse = null;
        if(CollUtil.isNotEmpty(bodyRespParams)){
            bodyResponse = handleRespParamMapping(contextParamMappings,bodyRespParams.get(0).getChildApiRespParams(),HttpParamKind.BODY);
        }
        jsonObject.put(HttpParamKind.BODY.name(),bodyResponse);
        return jsonObject;
    }

    /**
     * 转换参数上下文对象结构
     * @param fullOperatorResults
     * @return
     */
    private Map<String,JSON> convertContextParamMappings(JSONObject fullOperatorResults){
        Map<String, JSON> paramMappings = Maps.newLinkedHashMap();
        for(Map.Entry<String,Object> entry : fullOperatorResults.entrySet()){
            paramMappings.put(entry.getKey(), (JSON) entry.getValue());
        }
        return paramMappings;
    }

    /**
     * 响应参数映射
     * @param contextParamMappings
     * @param respParamDTOList
     * @return
     */
    private JSON handleRespParamMapping(Map<String,JSON> contextParamMappings,List<ApiRespParamDTO> respParamDTOList,HttpParamKind httpParamKind){
        if(CollUtil.isEmpty(respParamDTOList)){
            return null;
        }
        return ParamMappingsUtils.alignMultiJsonArray(
                ParamMappingsUtils.alignFieldValues(
                        contextParamMappings,convertRespParamMapping(respParamDTOList,httpParamKind)));
    }

    /**
     * 响应参数转换
     * @param respParamDTOList
     * @return
     */
    private List<? extends AbstractParamMapping> convertRespParamMapping(List<ApiRespParamDTO> respParamDTOList, HttpParamKind httpParamKind){
        List<ResponseParamMappings> mappings = Lists.newArrayList();
        if(CollUtil.isEmpty(respParamDTOList)){
            return mappings;
        }
        for (ApiRespParamDTO respParamDTO : respParamDTOList){
            ResponseParamMappings responseMappings = getResponseParamMappings(httpParamKind, respParamDTO);
            if(isAddChildRespParams(respParamDTO)){
                responseMappings.setType(ParamMappingTypeEnum.REFERENCE_TYPE.getType());
                responseMappings.setExpressionIsJson(Boolean.TRUE);
                responseMappings.setExpression(JSONUtil.toJsonStr(getChildExpression(respParamDTO.getChildApiRespParams(),new JSONObject())));
            }
            mappings.add(responseMappings);
        }
        return mappings;
    }

    /**
     * 是否添加子集响应参数
     * @param respParamDTO
     * @return
     */
    private Boolean isAddChildRespParams(ApiRespParamDTO respParamDTO){
        return CollUtil.isNotEmpty(respParamDTO.getChildApiRespParams())
                && ( StrUtil.isEmpty(respParamDTO.getParamValueType())
                ||(ParamMappingTypeEnum.FIXED_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getDefaultValue()))
                || (ParamMappingTypeEnum.REFERENCE_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getExpression()))
                || (ParamMappingTypeEnum.FUNCTION_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getExpression()))
        );
    }

    /**
     * 设置表达式参数
     * @param childApiRespParams
     * @param result
     * @return
     */
    private JSONObject getChildExpression(List<ApiRespParamDTO> childApiRespParams,JSONObject result){
        for(ApiRespParamDTO respParam : childApiRespParams){
            ExpressionParam expression = new ExpressionParam();
            expression.setOperatorId(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
            expression.setOperatorName(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
            expression.setDataType(respParam.getAssetDataType());
            expression.setType(respParam.getParamValueType());
            if(ParamMappingTypeEnum.FIXED_TYPE.getType().equals(respParam.getParamValueType())){
                expression.setValue(respParam.getDefaultValue());
            }else{
                expression.setValue(respParam.getExpression());
            }
            if(isAddChildRespParams(respParam)){
                expression.setType(null);
                expression.setExpressionIsJson(Boolean.TRUE);
                expression.setValue(getChildExpression(respParam.getChildApiRespParams(),new JSONObject()));
            }
            result.put(respParam.getAssetColumns(),expression);
        }
        return result;
    }

    /**
     * 构造响应参数
     * @param httpParamKind
     * @param respParamDTO
     * @return
     */
    private static ResponseParamMappings getResponseParamMappings(HttpParamKind httpParamKind, ApiRespParamDTO respParamDTO) {
        ResponseParamMappings responseMappings = new ResponseParamMappings();
        responseMappings.setOperatorId(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
        responseMappings.setField(respParamDTO.getAssetColumns());
        responseMappings.setDataType(respParamDTO.getAssetDataType());
        responseMappings.setType(respParamDTO.getParamValueType());
        responseMappings.setFixedValue(respParamDTO.getDefaultValue());
        responseMappings.setExpression(respParamDTO.getExpression());
        responseMappings.setHttpParamKind(httpParamKind);
        responseMappings.setExpressionIsJson(Boolean.FALSE);
        return responseMappings;
    }


    /**
     * 获取响应参数模板
     * @return
     */
    private Map<HttpParamKind, List<ApiRespParamDTO>> getApiResponseParamListMap(Integer dataAssetApiId) {
        List<ApiRespParamDTO> liftFlowRespParamDTOList = dataApiAccessService.getApiResponseParamService().findLiteflowResult(dataAssetApiId);
        List<ApiRespParamDTO> apiRespParamDTOList = ApiResponseHelper.buildApiResponseDTOList(liftFlowRespParamDTOList);

        return apiRespParamDTOList.stream()
                .filter(x -> Objects.equals(x.getType(), HttpParamKind.HEAD)
                        || Objects.equals(x.getType(), HttpParamKind.BODY))
                .collect(Collectors.groupingBy(ApiRespParamDTO::getType, Collectors.toList())
                );
    }
}
