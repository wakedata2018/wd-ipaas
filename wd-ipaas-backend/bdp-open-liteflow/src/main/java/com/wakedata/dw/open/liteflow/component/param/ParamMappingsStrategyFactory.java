package com.wakedata.dw.open.liteflow.component.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.liteflow.DAGTaskEngine;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.param.strategy.MultiInputParamMappingsStrategy;
import com.wakedata.dw.open.liteflow.component.param.strategy.WithoutInputParamMappingsStrategy;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.flow.operator.global.GlobalOperator;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.model.api.flow.operator.edge.Edge;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author ZhangXueJun
 * @title RequestParamStrategyFactory
 * @date 2021/5/13 17:27
 * @projectName dw-open
 * @description
 */
public class ParamMappingsStrategyFactory {

    /**
     * 算子不存在错误提示模版
     */
    private static final String OPERATOR_NOT_EXITS_MSG = "operator [%s] not exits!";

    private static <T extends AbstractParamMappingsStrategy> T getRequestParamStrategy(
            Edge.Type type, JSONObject startInputParams) {
        AbstractParamMappingsStrategy requestParamStrategy = null;
        switch (type) {
            case multiInput:
                requestParamStrategy = new MultiInputParamMappingsStrategy(startInputParams);
                break;
            default:
                requestParamStrategy = new WithoutInputParamMappingsStrategy(startInputParams);
                break;
        }
        return (T) requestParamStrategy;
    }

    /**
     * 获取请求参数策略
     *
     * @param <T>
     * @param inputEdges
     * @param startInputParams
     * @param apiFlowSlot
     * @return
     */
    private static <T extends AbstractParamMappingsStrategy> T getRequestParamStrategy(
            Set<String> inputEdges,
            JSONObject startInputParams,
            ApiFlowSlot apiFlowSlot,
            DAGTaskEngine.OperatorContainer operatorContainer) {
        // 多条入边推导参数
        MultiInputParamMappingsStrategy paramStrategy = ParamMappingsStrategyFactory.getRequestParamStrategy(
                Edge.Type.multiInput, startInputParams
        );

        Map<String, JSON> multiInputMappings = Maps.newLinkedHashMap();
        JSON globalMultiInputMappings = apiFlowSlot.getOperatorResultSet(GlobalOperator.GLOBAL_OPERATOR_ID);

        // 复制StartNode节点：用于获取开始结果参数
        multiInputMappings.put(VertexOperator.VERTEX_OPERATOR_ID, startInputParams.getJSONObject(VertexOperator.VERTEX_OPERATOR_ID));
        Optional.ofNullable(globalMultiInputMappings).ifPresent(global -> multiInputMappings.put(GlobalOperator.GLOBAL_OPERATOR_ID, global));

        recursiveLoadSuperiorOperators(inputEdges, operatorContainer, apiFlowSlot, multiInputMappings);
        paramStrategy.setMultiInputMappings(multiInputMappings);
        return (T) paramStrategy;
    }

    /**
     * 递归加载上级算子节点
     *
     * @param inputOperatorIds   当前算子的入边算子节点id集合
     * @param operatorContainer  算子容器
     * @param apiFlowSlot        流程上下文存储桶
     * @param multiInputMappings 存放历史节点返回数据map
     */
    private static void recursiveLoadSuperiorOperators(Set<String> inputOperatorIds, DAGTaskEngine.OperatorContainer operatorContainer, ApiFlowSlot apiFlowSlot
            , Map<String, JSON> multiInputMappings) {
        for (String inputOperatorId : inputOperatorIds) {
            // 如果节点id是start节点，不处理
            if (inputOperatorId.equals(VertexOperator.VERTEX_OPERATOR_ID)) {
                continue;
            }
            // 根据节点id获取节点
            DAGTaskEngine.OperatorContext inputContext = operatorContainer.getOperatorContext(inputOperatorId);
            Preconditions.checkState(inputContext != null, String.format(OPERATOR_NOT_EXITS_MSG, inputOperatorId));
            Operator inputOperator = inputContext.getOperator();
            // 从上下文中获取对应的节点返回的数据放置在multiInputMappings中
            JSON json = apiFlowSlot.getOperatorResultSet(inputOperator.getName());
            multiInputMappings.put(inputOperator.getName(), json);
            // 递归加载上级数据
            Set<String> inputContextEdges = inputContext.getInputEdges();
            recursiveLoadSuperiorOperators(inputContextEdges, operatorContainer, apiFlowSlot, multiInputMappings);
        }
    }

    public static AbstractParamMappingsStrategy getRequestParamStrategy(NodeRunTimeContext context) {
        // 扁平化 header、query和filter有可能名字冲突，先忽略
        JSONObject startInputParams = context.getOperatorContainer().getStartInputParams();
        JSONObject flatStartInputParams = new JSONObject();
        for (HttpParamKind httpParamKind : HttpParamKind.values()) {
            if (httpParamKind == HttpParamKind.BODY) {
                continue;
            }

            JSONObject obj = startInputParams.getJSONObject(httpParamKind.name());
            if (MapUtils.isEmpty(obj)) {
                continue;
            }
            if (httpParamKind == HttpParamKind.FILTER) {
                flatStartInputParams.put(HttpParamKind.FILTER.name(), obj);
                continue;
            }
            if (httpParamKind == HttpParamKind.COMMON) {
                continue;
            }
            flatStartInputParams.putAll(obj);
        }

        flatStartInputParams.put(HttpParamKind.BODY.name(), startInputParams.get(HttpParamKind.BODY.name()));

        return getRequestParamStrategy(
                context.getInputEdges(),
                flatStartInputParams,
                context.getApiFlowSlot(),
                context.getOperatorContainer()
        );
    }
}
