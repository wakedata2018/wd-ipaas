package com.wakedata.dw.open.liteflow.component.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.param.AbstractParamMappingsStrategy;
import com.wakedata.dw.open.liteflow.component.param.ParamMappingsStrategyFactory;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.utils.threadlocal.LiteFlowStartParamThreadLocal;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Map;

/**
 * 开始算子组件
 *
 * @author wujunqiang
 * @since 2022/9/8 19:36
 */
public class StartNodeComponent extends AbstractNodeComponent<VertexOperator> {

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        // 实际不会执行此方法，因为开始算子不会经过 AbstractNodeComponent.process() 方法
        return currentRequestParams;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_start_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        // 开始算子参数放入ThreadLocal、slot
        AbstractParamMappingsStrategy requestParamStrategy = ParamMappingsStrategyFactory.getRequestParamStrategy(threadLocal.get());
        JSONObject startInputParams = requestParamStrategy.getStartInputParams().getJSONObject(VertexOperator.VERTEX_OPERATOR_ID);
        LiteFlowStartParamThreadLocal.setStartParam(startInputParams.toJSONString());

        ApiFlowSlot apiFlowSlot = threadLocal.get().getApiFlowSlot();
        JSONObject fullOperatorResults = apiFlowSlot.getFullOperatorResults();
        if (fullOperatorResults.isEmpty()) {
            apiFlowSlot.storeStartResult(VertexOperator.VERTEX_OPERATOR_ID, startInputParams);
        }
        // 打印开始算子运行日志
        JSONObject printStartParams = new JSONObject();
        for (Map.Entry<String, Object> entry : startInputParams.entrySet()) {
            if (entry.getValue() != null) {
                printStartParams.put(entry.getKey(), entry.getValue());
            }
        }
        GlobalNodeComponent.storeGlobalParams(threadLocal.get());
        addStartLog(printStartParams);
    }

    /**
     * 输出start节点日志
     *
     * @param startInputParams start节点参数
     */
    private void addStartLog(JSONObject startInputParams) {
        ApiFlowSlot apiFlowSlot = threadLocal.get().getApiFlowSlot();
        String timeStr = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        apiFlowSlot.addLog(timeStr + " 算子[start]: 执行开始....");
        apiFlowSlot.addLog(timeStr + " 算子[start]: 获取请求参数信息成功！ \n" + ParamMappingsUtils.prettyFormatJson(startInputParams));
    }

}
