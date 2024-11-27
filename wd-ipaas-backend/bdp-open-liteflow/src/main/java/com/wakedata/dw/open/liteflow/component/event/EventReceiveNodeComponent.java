package com.wakedata.dw.open.liteflow.component.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.model.api.flow.operator.event.EventReceiveOperator;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Map;

/**
 * 事件接收算子Liteflow组件
 *
 * @author wujunqiang
 * @since 2022/10/25 14:39
 */
public class EventReceiveNodeComponent extends AbstractNodeComponent<EventReceiveOperator> {

    /**
     * POST请求方式流程编排中，从开始算子中获取消息内容的路径
     */
    private static final String EVENT_RECEIVE_OPERATOR_POST_PARAM_KEY = "$.start.BODY.";
    /**
     * GET请求方式流程编排中，从开始算子中获取消息内容的路径
     */
    private static final String EVENT_RECEIVE_OPERATOR_GET_PARAM_KEY = "$.start.QUERY.";


    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_event_receive_operator_not_find_error;
    }

    @Override
    public void processCall() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        //获取事件接收算子
        EventReceiveOperator eventReceiveOperator = (EventReceiveOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        //事件接收算子开始执行，打印日志
        String operatorName = eventReceiveOperator.getName();
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 事件接收算子[%s]: 执行开始....", operatorName));
        // 获取所有上下文参数
        Map<String, JSON> contextParamMappings = convertContextParamMappings(apiFlowSlot.getFullOperatorResults());
        JSONObject messageJsonObject;
        String referenceValueExpression;
        //接收算子数据结构中的reqMethod字段已经透传流程编排的请求方式（GET、POST），当流程编排是POST请求时进入
        if (DataAssetEnums.ReqMethod.POST.equals(eventReceiveOperator.getComponent().getDataAssetApi().getReqMethod())) {
            referenceValueExpression = EVENT_RECEIVE_OPERATOR_POST_PARAM_KEY + operatorName;
            String str = (String) JSONPath.eval(ParamMappingsUtils.multiInputMappingsConvert(contextParamMappings), referenceValueExpression);
            messageJsonObject = JSON.parseObject(str);
            storeOperatorResultSet(nodeRunTimeContext, messageJsonObject);
        } else {
            referenceValueExpression = EVENT_RECEIVE_OPERATOR_GET_PARAM_KEY + DwOpenConstant.EVENT_RECEIVE_QUERY_PARAM_NAME;
            String str = (String) JSONPath.eval(ParamMappingsUtils.multiInputMappingsConvert(contextParamMappings), referenceValueExpression);
            messageJsonObject = JSON.parseObject(str);
            if (messageJsonObject.containsKey(operatorName)) {
                storeOperatorResultSet(nodeRunTimeContext, messageJsonObject.getJSONObject(operatorName));
            }
        }
        //获取事件接收算子的消息内容，打印日志
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 事件接收算子[%s]: 获取接收的消息内容成功！\n", operatorName) + ParamMappingsUtils.prettyFormatJson(messageJsonObject));
        //事件接收算子执行结束，打印日志
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 事件接收算子[%s]: 执行结束....！\n", operatorName));
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
}
