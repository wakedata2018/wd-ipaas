package com.wakedata.dw.open.model.api.flow.operator;

import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.VertexComponent;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 虚拟算子, 用于串联整个工作流
 *
 * @author ZhangXueJun
 * @title VertexOperator
 * @date 2019/10/26 16:24
 * @projectName dw-stream
 * @description
 */
@Slf4j
public class VertexOperator extends AbstractOperator<VertexComponent> implements Operator, OutputMultiEdge<Operator> {

    public static final VertexOperator INSTANCE = new VertexOperator();

    private Set<String> outputOperators;

    public VertexOperator() {
        this.outputOperators = Sets.newHashSet();
    }

    @Override
    public String getOperatorId() {
        return VERTEX_OPERATOR_ID;
    }

    @Override
    public String getName() {
        return VERTEX_OPERATOR_ID;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
        if(visitor instanceof OpVisitor){
            ((OpVisitor)visitor).visit(this);
        }
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