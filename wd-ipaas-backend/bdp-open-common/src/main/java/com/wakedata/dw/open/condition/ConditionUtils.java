package com.wakedata.dw.open.condition;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OperatorLogicalEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.condition.Condition;
import com.wakedata.dw.open.condition.ConditionValue;
import com.wakedata.dw.open.parammapping.ConditionParamMapping;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 条件判断工具类
 *
 * @author luomeng
 * @date 2022/9/19 18:58
 */
@Slf4j
public class ConditionUtils {

    /**
     * 表达式执行器
     */
    private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
    private static final String CONDITION_VALUE1 = "field1";
    private static final String CONDITION_VALUE2 = "field2";


    /**
     * 校验规则表达式
     *
     * @param conditionList 条件列表
     * @param ruleExpression 规则表达式
     * @return
     */
    public static boolean checkRuleExpression(List<Condition> conditionList, String ruleExpression) {
        if (CollUtil.isEmpty(conditionList) || StrUtil.isBlank(ruleExpression)) {
            return false;
        }
        //预设条件
        Map<String, Boolean> conditionResultMap = new HashMap<>();
        conditionList.forEach(cond -> {
            conditionResultMap.put(cond.getId(), true);
        });
        ruleExpression = replaceRuleExpression(ruleExpression, conditionResultMap);
        try {
            //表达式校验
            return (Boolean) SCRIPT_ENGINE_MANAGER.getEngineByName("js").eval(ruleExpression);
        } catch (ScriptException exception) {
            return false;
        }
    }

    /**
     * 执行多条件判断
     *
     * @param conditionList  条件列表
     * @param dataContext    数据上下文
     * @param ruleExpression 规则表达式
     * @return
     */
    public static boolean exec(List<Condition> conditionList, JSON dataContext, String ruleExpression) {
        if (CollUtil.isEmpty(conditionList)) {
            return false;
        }
        Map<String, Boolean> conditionResultMap = new HashMap<>();
        conditionList.forEach(cond -> {
            conditionResultMap.put(cond.getId(), exec(cond, dataContext));
        });
        if (StrUtil.isBlank(ruleExpression)) {
            ruleExpression = getDefaultRuleExpresion(conditionResultMap, OperatorLogicalEnum.LOGICAL_AND);
        } else {
            ruleExpression = replaceRuleExpression(ruleExpression, conditionResultMap);
        }
        try {
            log.info("多条件判断执行表达式：{}", ruleExpression);
            return (Boolean) SCRIPT_ENGINE_MANAGER.getEngineByName("js").eval(ruleExpression);
        } catch (ScriptException exception) {
            log.error("多条件判断执行出错，ruleExpression = {}", ruleExpression, exception);
            return false;
        }
    }

    /**
     * 执行条件判断
     *
     * @param condition   条件
     * @param dataContext 数据上下文
     * @return
     */
    public static boolean exec(Condition condition, JSON dataContext) {
        if (condition.getValue1() == null || StrUtil.isBlank(condition.getOperator())) {
            return false;
        }
        boolean rs = false;
        try {
            //解析v1和v2的值
            List<ConditionParamMapping> paramMappings = new ArrayList<>();
            ConditionParamMapping conditionParamMappings1 = getConditionParamMappings(CONDITION_VALUE1, condition.getValue1());
            if (conditionParamMappings1 != null) {
                paramMappings.add(conditionParamMappings1);
            }
            ConditionParamMapping conditionParamMappings2 = getConditionParamMappings(CONDITION_VALUE2, condition.getValue2());
            if (conditionParamMappings2 != null) {
                paramMappings.add(conditionParamMappings2);
            }
            ParamMappingsUtils.MappingFieldResults mappingFieldResults = ParamMappingsUtils.alignFieldValues(dataContext, paramMappings);
            Object v1 = mappingFieldResults.getFieldValues().get(CONDITION_VALUE1);
            Object v2 = mappingFieldResults.getFieldValues().get(CONDITION_VALUE2);
            //执行判断
            if (condition.getValue1() != null) {
                v1 = condition.cast(DataTypeEnum.getEnumByType(condition.getValue1().getDataType()), v1);
            }
            if (condition.getValue2() != null) {
                v2 = condition.cast(DataTypeEnum.getEnumByType(condition.getValue2().getDataType()), v2);
            }

            rs = condition.conditionCompare(v1, v2);
        } catch (OpenException e) {
            String message = e.getMessage() + ", data=" + JSON.toJSONString(condition);
            log.error(message, e);
            throw new OpenException(message, e.getCause());
        }
        return rs;
    }

    /**
     * 替换规则表达式
     *
     * @param ruleExpression
     * @param conditionResultMap
     * @return
     */
    public static String replaceRuleExpression(String ruleExpression, Map<String, Boolean> conditionResultMap) {
        //去除所有空白字符
        ruleExpression = ruleExpression.replaceAll("\\s*", "");
        ruleExpression = "(" + ruleExpression + ")";
        //替换逻辑运算符
        for (OperatorLogicalEnum logicalEnum : OperatorLogicalEnum.values()) {
            ruleExpression = ruleExpression.replaceAll(logicalEnum.getCode(), logicalEnum.getScript());
        }
        //替换参数结果
        for (Map.Entry<String, Boolean> conditionResult : conditionResultMap.entrySet()) {
            Pattern compile = Pattern.compile("(\\(|&&|\\|\\|)" + conditionResult.getKey() + "(&&|\\|\\||\\))");
            Matcher matcher = compile.matcher(ruleExpression);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String group = matcher.group();
                matcher.appendReplacement(sb, group.replace(conditionResult.getKey(), String.valueOf(conditionResult.getValue())));
            }
            matcher.appendTail(sb);
            ruleExpression = sb.toString();
        }
        return ruleExpression;
    }

    /**
     * 组装规则表达式
     *
     * @param conditionResultMap
     * @param logical
     * @return
     */
    public static String getDefaultRuleExpresion(Map<String, Boolean> conditionResultMap, OperatorLogicalEnum logical) {
        StringBuilder ruleExpression = new StringBuilder();
        for (Map.Entry<String, Boolean> conditionResult : conditionResultMap.entrySet()) {
            ruleExpression.append(conditionResult.getValue()).append(logical.getScript());
        }
        ruleExpression.append("true");
        return ruleExpression.toString();
    }


    /**
     * 转换条件参数
     *
     * @param field          属性名
     * @param conditionValue
     * @return
     */
    private static ConditionParamMapping getConditionParamMappings(String field, ConditionValue conditionValue) {
        if (conditionValue == null) {
            return null;
        }
        ConditionParamMapping conditionParamMapping = new ConditionParamMapping();
        conditionParamMapping.setOperatorId(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
        conditionParamMapping.setField(field);
        conditionParamMapping.setDataType(conditionValue.getDataType());
        conditionParamMapping.setType(conditionValue.getType());
        conditionParamMapping.setFixedValue(conditionValue.getExpression());
        conditionParamMapping.setExpression(conditionValue.getExpression());
        conditionParamMapping.setExpressionIsJson(Boolean.FALSE);
        return conditionParamMapping;
    }


}
