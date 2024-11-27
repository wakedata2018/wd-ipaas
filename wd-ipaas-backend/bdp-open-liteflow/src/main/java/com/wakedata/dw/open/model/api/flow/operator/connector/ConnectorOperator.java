package com.wakedata.dw.open.model.api.flow.operator.connector;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

/**
 * 连接器算子
 *
 * @author wujunqiang
 * @since 2022/11/17 10:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectorOperator extends AbstractOperator<ConnectorComponent>
        implements Operator<ConnectorComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 请求参数：同一个算子具备多个算子的请求映射
     */
    private List<RequestParamMapping> requestParamMappings = Lists.newArrayList();

    /**
     * 出编算子集合
     */
    private Set<String> outputOperators;

    public ConnectorOperator() {
        super();
        this.outputOperators = Sets.newHashSet();
    }

    public ConnectorOperator(ConnectorComponent component) {
        super(component);
        this.outputOperators = Sets.newHashSet();
    }

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
