package com.wakedata.dw.open.model.api.flow.operator.sql;

import com.google.common.collect.Lists;
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
 * sql算子
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/2/14 18:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SqlOperator extends AbstractOperator<SqlComponent>
        implements Operator<SqlComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 请求参数：同一个算子具备多个算子的请求映射
     */
    private List<RequestParamMapping> requestParamMappings = Lists.newArrayList();

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

    }
}
