package com.wakedata.dw.open.model.api.flow.operator.global;

import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.VertexComponent;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月21日 19:41:30
 */
@Slf4j
public class GlobalOperator extends AbstractOperator<VertexComponent> implements Operator, OutputMultiEdge<Operator> {
    private Set<String> outputOperators;

    public GlobalOperator() {
        this.outputOperators = Sets.newHashSet();
    }

    @Override
    public String getOperatorId() {
        return GLOBAL_OPERATOR_ID;
    }

    @Override
    public String getName() {
        return GLOBAL_OPERATOR_ID;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {

    }

    @Override
    public boolean isMarked() {
        return true;
    }

    @Override
    public Set<String> getOutputOperators() {
        return outputOperators;
    }

    @Override
    public void addOutOperators(Operator... outputOperators) {
        for (Operator operator : outputOperators) {
            this.outputOperators.add(operator.getOperatorId());
        }
    }
}
