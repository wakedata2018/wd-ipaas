package com.wakedata.dw.open.liteflow.component.event;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wakedata.common.mq.MqProducerHelper;
import com.wakedata.common.mq.enums.RequestProtocolEnum;
import com.wakedata.common.mq.model.MqConfigParam;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.EventConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.model.api.flow.operator.event.EventSendOperator;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;
import com.wakedata.dw.open.parammapping.ExpressionParam;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.parammapping.ResponseParamMappings;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/10/24 15:54
 */
public class EventSendNodeComponent extends AbstractNodeComponent<EventSendOperator> {

    /**
     * QUEUE_ID模版
     */
    private static final String QUEUE_ID_TEMPLATE = "QUEUE_%s_%s_%s";

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        // 实际不会执行此方法，因为判断算子不会经过 AbstractNodeComponent.process() 方法
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_event_send_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        //获取事件发送算子
        EventSendOperator eventSendOperator = (EventSendOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        //事件发送算子开始执行，打印日志
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 事件发送算子[%s]: 执行开始....", eventSendOperator.getName()));
        //获取发送算子参数
        EventSendApiAttr eventSendApiAttr = (EventSendApiAttr) eventSendOperator.getComponent().getDataAssetApi().getApiAttr();
        //事件发送，构建参数结构，方便调用工具类进行转换
        List<ApiRespParamDTO> apiRespParamDTOList = buildApiRespParamDTOList(eventSendApiAttr.getMessageTemplate());
        // 获取所有上下文参数
        Map<String, JSON> contextParamMappings = convertContextParamMappings(apiFlowSlot.getFullOperatorResults());
        //复用工具方法，将jsonschema格式且带有引用值的数据装换成json格式的固定值
        JSON message = ParamMappingsUtils.alignMultiJsonArray(ParamMappingsUtils.alignFieldValues(contextParamMappings, convertRespParamMapping(apiRespParamDTOList.get(0).getChildApiRespParams(), HttpParamKind.BODY, eventSendOperator.getOperatorId())));
        //调用wd-common-mq模块的工具类发送事件
        MqProducerHelper.publishForCustomParam(message, buildMqConfigParam(eventSendApiAttr));
        //将消息体放入全局变量中
        storeOperatorResultSet(nodeRunTimeContext, message);
        //获取事件发送算子的消息内容，打印日志
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 事件发送算子[%s]: 获取发送的消息内容成功！\n", eventSendOperator.getName()) + ParamMappingsUtils.prettyFormatJson(message));
        //事件发送算子执行结束，打印日志
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 事件发送算子[%s]: 执行结束....！\n", eventSendOperator.getName()));
    }

    /**
     * 根据发送算子的内容创建发送至MQ的配置
     *
     * @param eventSendApiAttr 发送算子配置
     * @return MQ配置参数
     */
    private MqConfigParam buildMqConfigParam(EventSendApiAttr eventSendApiAttr) {
        MqConfigParam customParam = new MqConfigParam();
        customParam.setTopic(eventSendApiAttr.getTopic());
        // 使用雪花算法生成queueId避免发消失时候提示生产者组已经创建异常
        String queueId = String.format(QUEUE_ID_TEMPLATE, IdUtil.getSnowflake(1L,1L).nextIdStr(), eventSendApiAttr.getId(), eventSendApiAttr.getApiId());
        customParam.setQueueId(queueId);
        customParam.setBootstrapServers(eventSendApiAttr.getClusterAddress());
        customParam.setTag(eventSendApiAttr.getTag());
        RequestProtocolEnum protocol = RequestProtocolEnum.covert(eventSendApiAttr.getMqType());
        customParam.setProtocol(protocol);
        String accessKey = eventSendApiAttr.getAccessKey();
        String secretKey = eventSendApiAttr.getSecretKey();
        if (RequestProtocolEnum.ROCKET_MQ == protocol && StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
            customParam.setRocketMqAccessKey(accessKey);
            customParam.setRocketMqSecretKey(secretKey);
        }
        if (RequestProtocolEnum.KAFKA == protocol && StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
            customParam.setSecurityProtocol(EventConstant.KAFKA_SECURITY_PROTOCOL);
            customParam.setSaslMechanism(EventConstant.KAFKA_SASL_MECHANISM);
            customParam.setSaslJaasConfig(String.format(EventConstant.KAFKA_SASL_JAAS_CONFIG, accessKey, secretKey));
        }
        return customParam;
    }

    private List<ApiRespParamDTO> buildApiRespParamDTOList(String messageTemplate) {
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setId(ApiResponseHelper.VIRTUAL_ID);
        apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
        apiRespParamDTO.setType(HttpParamKind.BODY);
        apiRespParamDTO.setResponsePostData(messageTemplate);
        apiRespParamDTO.setBusinessType(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        apiRespParamDTO.setAssetColumns(ApiResponseHelper.VIRTUAL_ASSET_COLUMN);
        apiRespParamDTO.setAssetDataType(ApiResponseHelper.VIRTUAL_ASSET_DATA_TYPE);
        return ApiResponseHelper.buildApiResponseDTOList(Collections.singletonList(apiRespParamDTO));
    }

    /**
     * 转换参数上下文对象结构
     *
     * @param fullOperatorResults 全局参数池中的结果参数
     * @return Map<String, JSON>
     */
    private Map<String, JSON> convertContextParamMappings(JSONObject fullOperatorResults) {
        Map<String, JSON> paramMappings = Maps.newLinkedHashMap();
        for (Map.Entry<String, Object> entry : fullOperatorResults.entrySet()) {
            paramMappings.put(entry.getKey(), (JSON) entry.getValue());
        }
        return paramMappings;
    }

    /**
     * 响应参数转换
     *
     * @param respParamDTOList
     * @return
     */
    private List<? extends AbstractParamMapping> convertRespParamMapping(List<ApiRespParamDTO> respParamDTOList, HttpParamKind httpParamKind, String operatorId) {
        List<ResponseParamMappings> mappings = Lists.newArrayList();
        if (CollUtil.isEmpty(respParamDTOList)) {
            return mappings;
        }
        for (ApiRespParamDTO respParamDTO : respParamDTOList) {
            ResponseParamMappings responseMappings = getResponseParamMappings(httpParamKind, respParamDTO, operatorId);
            if (isAddChildRespParams(respParamDTO)) {
                responseMappings.setType(ParamMappingTypeEnum.REFERENCE_TYPE.getType());
                responseMappings.setExpressionIsJson(Boolean.TRUE);
                responseMappings.setExpression(JSONUtil.toJsonStr(getChildExpression(respParamDTO.getChildApiRespParams(), new JSONObject())));
            }
            mappings.add(responseMappings);
        }
        return mappings;
    }

    /**
     * 构造响应参数
     *
     * @param httpParamKind
     * @param respParamDTO
     * @return
     */
    private static ResponseParamMappings getResponseParamMappings(HttpParamKind httpParamKind, ApiRespParamDTO respParamDTO, String operatorId) {
        ResponseParamMappings responseMappings = new ResponseParamMappings();
        responseMappings.setOperatorId(operatorId);
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
     * 是否添加子集响应参数
     *
     * @param respParamDTO
     * @return
     */
    private Boolean isAddChildRespParams(ApiRespParamDTO respParamDTO) {
        return CollUtil.isNotEmpty(respParamDTO.getChildApiRespParams())
                && (StrUtil.isEmpty(respParamDTO.getParamValueType())
                || (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getDefaultValue()))
                || (ParamMappingTypeEnum.REFERENCE_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getExpression()))
                || (ParamMappingTypeEnum.FUNCTION_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getExpression()))
        );
    }

    /**
     * 设置表达式参数
     *
     * @param childApiRespParams
     * @param result
     * @return
     */
    private JSONObject getChildExpression(List<ApiRespParamDTO> childApiRespParams, JSONObject result) {
        for (ApiRespParamDTO respParam : childApiRespParams) {
            ExpressionParam expression = new ExpressionParam();
            expression.setOperatorId(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
            expression.setOperatorName(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
            expression.setDataType(respParam.getAssetDataType());
            expression.setType(respParam.getParamValueType());
            if (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(respParam.getParamValueType())) {
                expression.setValue(respParam.getDefaultValue());
            } else {
                expression.setValue(respParam.getExpression());
            }
            if (isAddChildRespParams(respParam)) {
                expression.setType(null);
                expression.setExpressionIsJson(Boolean.TRUE);
                expression.setValue(getChildExpression(respParam.getChildApiRespParams(), new JSONObject()));
            }
            result.put(respParam.getAssetColumns(), expression);
        }
        return result;
    }
}
