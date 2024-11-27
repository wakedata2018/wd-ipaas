package com.wakedata.dw.open.liteflow.component.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.model.api.flow.operator.transform.RowSelectTransformOperator;


/**
 * [?(@.ID > 250)]
 *
 * @author ZhangXueJun
 * @title ColumnSelectNodeComponent
 * @date 2021/5/25 17:02
 * @projectName dw-open
 * @description
 */
public class RowSelectNodeComponent extends AbstractNodeComponent<RowSelectTransformOperator> {

    @Override
    protected void beforeProcessInternal() throws Exception {
        super.beforeProcessInternal();
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext,JSONObject currentRequestParams) throws Exception {
        String inputEdge = nodeRunTimeContext.getInputEdges().iterator().next();
        if (inputEdge.equals(VertexOperator.VERTEX_OPERATOR_ID)) {
            return new JSONObject();
        }

        Operator inputOperator = nodeRunTimeContext.getOperatorContainer().getOperator(inputEdge);
        JSON json = getOperatorResultSet(nodeRunTimeContext,inputOperator.getName());
        RowSelectTransformOperator operator = (RowSelectTransformOperator) nodeRunTimeContext.getOperator();
        return AbstractNodeComponent.jsonParse(JSONPath.eval(json, operator.getRowExpression()));
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_select_row_operator_error;
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