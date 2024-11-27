package com.wakedata.dw.open.service.utils;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.google.common.collect.Lists;
import com.wakedata.dw.open.model.api.ApiRulePo;

import java.util.List;

/**
 * @author ZhangXueJun
 * @title SentinelUtils
 * @date 2021/2/24 10:16
 * @projectName dw-open
 * @description
 */
public class SentinelUtils {

    public static void initFlowRules(String resource, Integer qps){
        List<FlowRule> rules = Lists.newArrayList();
        FlowRule rule = new FlowRule();
        rule.setResource(String.valueOf(resource));
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(qps);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

    public static  void initFlowRules(List<ApiRulePo> apiRules) {
        List<FlowRule> rules = Lists.newArrayList();
        for (ApiRulePo apiRule : apiRules) {
            FlowRule rule = new FlowRule();
            rule.setResource(String.valueOf(apiRule.getDataAssetApiId()));
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(apiRule.getQps());
            rules.add(rule);
        }
        FlowRuleManager.loadRules(rules);
    }
}
