package com.wakedata.dw.open.liteflow.component.branch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.condition.ConditionUtils;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.flow.operator.branch.BranchAttribute;
import com.wakedata.dw.open.model.api.flow.operator.branch.BranchOperator;
import com.wakedata.dw.open.model.api.flow.operator.branch.BranchParam;
import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分支算子LiteFlow组件
 *
 * @author wujunqiang
 * @since 2022/11/4 17:33
 */
public class BranchNodeComponent extends AbstractNodeComponent<BranchOperator> {

    /**
     * 分支规则结果Key
     */
    private static final String KEY_RULE_RESULTS = "RuleResults";

    /**
     * 分支信息Key
     */
    private static final String KEY_BRANCH_INFOS = "BranchInfos";

    /**
     * 分支条件规则Key
     */
    private static final String KEY_RULES = "Rules";

    /**
     * 分支条件表达式Key
     */
    private static final String KEY_BRANCH_RULE_EXPRESS = "BranchRuleExpress";

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        // 实际不会执行此方法，因为分支算子不会经过 AbstractNodeComponent.process() 方法
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_branch_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        // 1.运行前准备、打印日志
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        BranchOperator branchOperator = (BranchOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 分支算子[%s]: 执行开始....", branchOperator.getName()));

        // 2.获取当前分支算子组件中定义的规则集合，遍历集合将计算每个规则的结果放入 ruleResultMap 中
        Map<String, Boolean> ruleResultMap = new HashMap<>(16);
        Map<String, JSONObject> branchInfoMap = new HashMap<>(16);
        BranchAttribute attribute = branchOperator.getComponent().getDataAssetApi();
        JSONObject fullOperatorResults = apiFlowSlot.getFullOperatorResults();
        for (BranchParam branchParam : attribute.getBranchParams()) {
            String branchName = branchParam.getBranchName();
            ruleResultMap.put(branchName, ConditionUtils.exec(branchParam.getConditions(), fullOperatorResults, branchParam.getBranchRuleExpression()));
            // 记录分支条件日志
            JSONObject branchInfo = new JSONObject();
            branchInfo.put(KEY_BRANCH_RULE_EXPRESS, branchParam.getBranchRuleExpression());
            JSONObject ruleInfos = new JSONObject();
            branchParam.getConditions().forEach(x -> ruleInfos.put(x.getId(), x.parseConditionStr()));
            branchInfo.put(KEY_RULES, ruleInfos);
            branchInfoMap.put(branchName, branchInfo);
        }

        // 3.找到当前分支算子的出边、以及对应的出边分支规则，比对规则，如果符合规则将出边算子以及后续算子id放入上下文的 afterJudgeOperatorIds 中
        String currentOperatorId = branchOperator.getOperatorId();
        List<ApiRuleAttr> apiRuleAttrs = attribute.getApiAttrs().stream().filter(x -> currentOperatorId.equals(x.getFromOperatorId())).collect(Collectors.toList());
        for (ApiRuleAttr apiRuleAttr : apiRuleAttrs) {
            if (ruleResultMap.get(apiRuleAttr.getRuleName())) {
                super.iteratorSetOutputOperators(apiRuleAttr.getToOperatorId());
            }
        }

        // 4.如果当前分支算子在子流程中，还需要设置父算子之后的算子id放入afterJudgeOperatorIds集合中
        super.iteratorSetOutputOperatorsFromSubElement(currentOperatorId, branchOperator.getParentOperatorId());

        // 5.保存分支算子中所有规则的结果到上下文中，并输出日志
        JSONObject branchOperatorInfo = new JSONObject();
        branchOperatorInfo.put(KEY_BRANCH_INFOS, branchInfoMap);
        branchOperatorInfo.put(KEY_RULE_RESULTS, ruleResultMap);
        branchOperatorInfo.put(KEY_AFTER_JUDGE_OPERATOR_IDS, apiFlowSlot.getAfterJudgeOperatorIds());
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 分支算子[%s]: 执行结果..... \n", branchOperator.getName()) + ParamMappingsUtils.prettyFormatJson(branchOperatorInfo));
        apiFlowSlot.setIsHaveJudgeOperatorBefore(Boolean.TRUE);
    }

}
