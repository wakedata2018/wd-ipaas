package com.wakedata.dw.open.model.api.flow.operator.branch;

import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 分支算子
 *
 * @author wujunqiang
 * @since 2022/11/4 17:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BranchOperator extends AbstractOperator<BranchComponent>
        implements Operator<BranchComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

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
