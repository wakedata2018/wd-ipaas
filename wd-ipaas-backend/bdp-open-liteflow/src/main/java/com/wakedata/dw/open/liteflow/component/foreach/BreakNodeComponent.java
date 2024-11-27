package com.wakedata.dw.open.liteflow.component.foreach;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.condition.ConditionUtils;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakComponent;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import com.yomahub.liteflow.slot.Slot;
import com.yomahub.liteflow.util.LiteFlowProxyUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * 循环退出组件
 *
 * @author luomeng
 * @date 2022/12/5 16:36
 */
@Slf4j
public class BreakNodeComponent extends AbstractNodeComponent<BreakOperator> {


    /**
     * 退出循环算子需要强制指定组件类型为BREAK
     * @param type
     */
    @Override
    public void setType(NodeTypeEnum type) {
        super.setType(NodeTypeEnum.BREAK);
    }

    @Override
    public void processCall() throws Exception {
        boolean breakFlag = processBreak();
        Slot slot = this.getSlot();
        Class<?> originalClass = LiteFlowProxyUtil.getUserClass(this.getClass());
        slot.setBreakResult(originalClass.getName(), breakFlag);
    }

    /**
     * 退出循环
     *
     * @return
     * @throws Exception
     */
    public boolean processBreak() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        BreakOperator breakOperator = (BreakOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        BreakComponent component = breakOperator.getComponent();
        JSONObject fullOperatorResults = apiFlowSlot.getFullOperatorResults();
        //校验是否可以退出循环
        boolean result = ConditionUtils.exec(component.getConditionList(), fullOperatorResults, component.getRuleExpression());
        JSONObject results = (JSONObject) ObjectUtil.defaultIfNull(getOperatorResultSet(nodeRunTimeContext, nodeRunTimeContext.getOperatorContainer().getOperator(breakOperator.getParentOperatorId()).getName()), new JSONObject());
        JSONObject body = ObjectUtil.defaultIfNull(results.getJSONObject(HttpParamKind.BODY.name()), new JSONObject());
        addLog("执行第" + ObjectUtil.defaultIfNull(body.getInteger(ForNodeComponent.LoopFieldEnum.COUNTER.getField()), CommonConstant.ZERO) + "次退出循环校验，校验结果：" + result);
        return result;
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_for_break_operator_error;
    }
}
