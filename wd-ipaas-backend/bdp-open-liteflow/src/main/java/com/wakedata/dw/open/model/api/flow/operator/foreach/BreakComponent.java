package com.wakedata.dw.open.model.api.flow.operator.foreach;

import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.condition.Condition;
import lombok.Data;

import java.util.List;

/**
 * 退出循环组件
 *
 * @author luomeng
 * @date 2022/12/5 16:48
 */
@Data
public class BreakComponent extends AbstractComponent {
    /**
     * 退出循环条件
     */
    private List<Condition> conditionList;
    /**
     * 规则表达式
     */
    private String ruleExpression;

}
