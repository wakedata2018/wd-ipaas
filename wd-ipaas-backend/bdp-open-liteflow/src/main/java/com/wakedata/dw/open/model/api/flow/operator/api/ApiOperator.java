package com.wakedata.dw.open.model.api.flow.operator.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author ZhangXueJun
 * @title ApiOperator
 * @date 2021/3/22 11:33
 * @projectName dw-open
 * @description
 */
@Data
public class ApiOperator extends AbstractOperator<ApiComponent>
        implements Operator<ApiComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 请求参数：同一个算子具备多个算子的请求映射
     */
    private List<RequestParamMapping> requestParamMappings = Lists.newArrayList();

    /**
     * 出编算子集合
     */
    private Set<String> outputOperators;

    /**
     * For 序列化
     */
    public ApiOperator() {
        super();
        this.outputOperators = Sets.newHashSet();
    }

    public ApiOperator(ApiComponent component) {
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
