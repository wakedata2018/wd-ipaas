package com.wakedata.dw.open.liteflow.component.judge;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.condition.ConditionUtils;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.flow.operator.judge.JudgeOperator;
import com.wakedata.dw.open.model.api.flow.operator.judge.JudgeParam;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 判断算子LiteFlow组件
 *
 * @author wujunqiang
 * @since 2022/9/19 17:25
 */
public class JudgeNodeComponent extends AbstractNodeComponent<JudgeOperator> {

    /**
     * 判断算子运行结果KEY
     */
    private static final String KEY_RESULT = "result";

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        // 实际不会执行此方法，因为判断算子不会经过 AbstractNodeComponent.process() 方法
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_judge_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        // 1、拿到判断算子对应的Operator
        JudgeOperator judgeOperator = (JudgeOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        JudgeParam judgeParam = ObjectUtil.isNotEmpty(judgeOperator.getComponent()) ? judgeOperator.getComponent().getDataAssetApi() : new JudgeParam();
        // 打印判断算子运行日志
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 判断算子[%s]: 执行开始....", judgeOperator.getName()));
        JSONObject judgeParamJsonObject= (JSONObject) JSONObject.toJSON(judgeParam);
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 判断算子[%s]: 获取请求参数信息成功！\n", judgeOperator.getName()) + ParamMappingsUtils.prettyFormatJson(judgeParamJsonObject));
        // 2、根据设置的判断条件计算出结果（true还是false），（已经封装好工具类，直接传入比较值内容和参数池中的算子返回结果json串）
        boolean result = ConditionUtils.exec(judgeParam.getComparisonValue(), apiFlowSlot.getFullOperatorResults());
        // 3、根据上一步的判断条件结果找到对应的出边算子，并递归获取到后面的出边算子（如果后面的出边算子是判断算子就不继续往后获取），将要执行的算子放入流程编排上下文
        // 4、当判断逻辑已经执行拿到结果后，根据结果获取当前判断算子相应的出边，如果没有出边，直接不进行迭代
        String judgeOperatorId = judgeOperator.getOperatorId();
        String operatorId = judgeParam.getJudgeOutputOperatorId(result, judgeOperatorId);
        if (StringUtils.isNotBlank(operatorId)) {
            iteratorSetOutputOperators(operatorId);
        }
        // 5、如果当前判断算子在子流程中，还需要设置父算子之后的算子id放入afterJudgeOperatorIds集合中
        super.iteratorSetOutputOperatorsFromSubElement(judgeOperatorId, judgeOperator.getParentOperatorId());
        // 6、 打印判断算子运行结果
        JSONObject judgeResult = new JSONObject();
        judgeResult.put(KEY_RESULT, result);
        judgeResult.put(KEY_AFTER_JUDGE_OPERATOR_IDS, apiFlowSlot.getAfterJudgeOperatorIds());
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 判断算子[%s]: 执行结果..... \n", judgeOperator.getName()) + ParamMappingsUtils.prettyFormatJson(judgeResult));
        apiFlowSlot.setIsHaveJudgeOperatorBefore(Boolean.TRUE);
    }

}
