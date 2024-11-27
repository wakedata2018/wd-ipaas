package com.wakedata.dw.open.model.api.flow.operator.event;

import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/10/24 15:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EventSendOperator extends AbstractOperator<EventSendComponent>
        implements Operator<EventSendComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

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
        // 暂定这样写，后续有问题再改
        for (Operator outputOperator : outputOperators) {
            this.outputOperators.add(outputOperator.getOperatorId());
        }
    }
}
