package com.wakedata.dw.open.model.api.flow.operator.foreach;

import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;

import java.util.Set;

/**
 * 退出循环算子
 *
 * @author luomeng
 * @date 2022/12/5 16:48
 */
@Data
public class BreakOperator extends AbstractOperator<BreakComponent>
        implements Operator<BreakComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 出边算子集合
     */
    private Set<String> outputOperators;

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
}
