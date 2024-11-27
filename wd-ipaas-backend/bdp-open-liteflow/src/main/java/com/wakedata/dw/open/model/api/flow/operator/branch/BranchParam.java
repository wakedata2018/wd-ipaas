package com.wakedata.dw.open.model.api.flow.operator.branch;

import com.wakedata.dw.open.condition.Condition;
import lombok.Data;

import java.util.List;

/**
 * 分支算子条件封装类
 *
 * @author wujunqiang
 * @since 2022/11/25 11:51
 */
@Data
public class BranchParam {

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 分支条件
     */
    private List<Condition> conditions;

    /**
     * 判断条件（用于前端展示）
     */
    private String judgmentConditions;

    /**
     * 分支条件规则
     */
    private String branchRuleExpression;

}
