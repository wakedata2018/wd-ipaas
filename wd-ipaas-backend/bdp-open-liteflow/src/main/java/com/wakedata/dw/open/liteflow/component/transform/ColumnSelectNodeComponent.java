package com.wakedata.dw.open.liteflow.component.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.parammapping.ResponseParamMappings;
import com.wakedata.dw.open.model.api.flow.operator.transform.ColumnSelectTransformOperator;

import java.util.List;

/**
 * @author ZhangXueJun
 * @title ColumnSelectNodeComponent
 * @date 2021/5/25 17:02
 * @projectName dw-open
 * @description
 */
public class ColumnSelectNodeComponent extends AbstractNodeComponent<ColumnSelectTransformOperator> {

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext,JSONObject currentRequestParams) throws Exception {
        // 复制上游节点
        JSONObject jsonObject = new JSONObject();
        for (String inputEdge : nodeRunTimeContext.getInputEdges()) {
            Operator operator = nodeRunTimeContext.getOperatorContainer().getOperator(inputEdge);
            jsonObject.put(operator.getName(), getOperatorResultSet(nodeRunTimeContext,operator.getName()));
        }
        return jsonObject;
    }

    @Override
    protected List<ResponseParamMappings> getResponseParamMappings() {
        ColumnSelectTransformOperator operator = (ColumnSelectTransformOperator) threadLocal.get().getOperator();
        return operator.getResponseParamMappings();
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_select_column_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        // 1、处理请求参数映射
        beforeProcessInternal();

        // 2、内部处理算子
        processInternal();

        // 3、处理响应参数映射
        afterProcessInternal();
    }
}