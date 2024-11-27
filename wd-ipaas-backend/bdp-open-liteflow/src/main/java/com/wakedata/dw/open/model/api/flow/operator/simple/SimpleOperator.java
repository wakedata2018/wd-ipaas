package com.wakedata.dw.open.model.api.flow.operator.simple;

import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;

import java.util.Set;

/**
 * For Testing
 *
 * @author ZhangXueJun
 * @title MockOperator
 * @date 2021/5/8 11:00
 * @projectName dw-open
 * @description
 */
public class SimpleOperator extends AbstractOperator<SimpleComponent>
        implements Operator<SimpleComponent>, OutputMultiEdge<Operator> {

    private Set<String> outputOperators;

    /**
     * For 序列化
     */
    public SimpleOperator() {
        super();
        this.outputOperators = Sets.newHashSet();
    }

    public SimpleOperator(SimpleComponent component) {
        super(component);
        this.outputOperators = Sets.newHashSet();
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
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
