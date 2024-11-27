package com.wakedata.dw.open.liteflow.component.variable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.variable.helper.VariableUtil;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.operator.variable.CreateVariableOperator;
import com.wakedata.dw.open.model.api.flow.operator.variable.model.VariableParams;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.utils.ParamBuildUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author WangChenSheng
 * @descriptor
 * @title VariableNodeComponent
 * @date 2022/11/15 15:39
 */
public class CreateVariableNodeComponent extends AbstractNodeComponent<CreateVariableOperator> {

    private static final String BODY = "body";

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_create_variable_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();

        CreateVariableOperator variableOperator = (CreateVariableOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 创建变量算子[%s]: 执行开始....", variableOperator.getName()));

        // 获取创建变量算子中的变量
        VariableParams variableParams = variableOperator.getComponent().getVariableParams();
        if (Objects.isNull(variableParams)){
            variableParams = new VariableParams();
        }
        // 获取type为body的参数并转换
        List<ApiRespParamDTO> apiRespParamDTOList = ParamBuildUtil.buildTree(variableParams.getVariableJsonSchema(), DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        JSON message = VariableUtil.convertParams(apiFlowSlot, apiRespParamDTOList);

        // HEAD QUERY BODY结构 放入编排上下文
        storeOperatorResultSet(apiFlowSlot, nodeRunTimeContext.getOperator().getName(), message);

        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 创建变量算子[%s]: 获取参数成功！\n", variableOperator.getName()) + ParamMappingsUtils.prettyFormatJson(message));
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 创建变量算子[%s]: 执行结束....！\n", variableOperator.getName()));
    }

    /**
     * 将创建变量算子放入编排上下文
     * 此处不用抽象类的方法是因为抽象类的方法会过滤value为null的字段
     */
    private void storeOperatorResultSet(ApiFlowSlot apiFlowSlot, String name, JSON message) {
        JSONObject params = (JSONObject) message;

        JSONObject fullOperatorResults = apiFlowSlot.getFullOperatorResults();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(HttpParamKind.BODY.name(), params.getJSONObject(BODY));
        fullOperatorResults.put(name, jsonObject);
    }

}
