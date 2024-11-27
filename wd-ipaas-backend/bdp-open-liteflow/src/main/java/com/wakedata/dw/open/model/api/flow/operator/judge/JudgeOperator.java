package com.wakedata.dw.open.model.api.flow.operator.judge;

import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 判断算子
 *
 * @author wujunqiang
 * @since 2022/9/19 16:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JudgeOperator extends AbstractOperator<JudgeComponent>
        implements Operator<JudgeComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

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
