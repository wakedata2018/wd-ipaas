package com.wakedata.dw.open.model.api.flow.operator.foreach;

import cn.hutool.core.collection.CollUtil;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * for循环算子
 *
 * @author luomeng
 * @date 2022/12/5 16:19
 */
@Data
public class ForOperator extends AbstractOperator<ForComponent>
        implements Operator<ForComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 出边算子集合
     */
    private Set<String> outputOperators;

    /**
     * 循环算子中子流程第一层算子id，并行流程可能包含多个算子，由前端传参，不传子流程无法执行
     */
    private Set<String> subFirstOperators;

    /**
     * 循环内部算子,无需前端传参，在编排运行时根据子流程的parentOperatorId进行构造
     */
    private Set<String> subOperators;

    @Override
    public void accept(Visitor visitor) throws Exception {

    }

    @Override
    public Set<String> getOutputOperators() {
        return outputOperators;
    }

    @Override
    public void addOutOperators(Operator... outputOperators) {
        for (Operator outputOperator : outputOperators) {
            this.outputOperators.add(outputOperator.getOperatorId());
        }
    }

    /**
     * 批量添加内部算子
     *
     * @param subOperatorIds
     */
    public void batchAddSubOperator(Set<String> subOperatorIds) {
        this.subOperators = new HashSet<>();
        if (CollUtil.isNotEmpty(subOperatorIds)) {
            this.subOperators.addAll(subOperatorIds);
        }
    }
}


