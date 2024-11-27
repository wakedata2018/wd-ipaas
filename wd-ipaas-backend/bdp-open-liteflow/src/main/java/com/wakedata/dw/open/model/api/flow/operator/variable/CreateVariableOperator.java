package com.wakedata.dw.open.model.api.flow.operator.variable;

import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author WangChenSheng
 * @descriptor
 * @title CreateVariableOperator
 * @date 2022/11/15 15:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateVariableOperator extends AbstractOperator<CreateVariableComponent>
        implements Operator<CreateVariableComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 出编算子集合
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
