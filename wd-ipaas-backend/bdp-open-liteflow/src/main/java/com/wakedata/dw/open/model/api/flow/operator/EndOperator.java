package com.wakedata.dw.open.model.api.flow.operator;

import com.wakedata.dw.open.model.api.flow.EndComponent;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import lombok.extern.slf4j.Slf4j;

/**
 * 结束算子
 *
 * @author wujunqiang
 * @since 2022/9/5 17:41
 */
@Slf4j
public class EndOperator extends AbstractOperator<EndComponent> implements Operator, InputMultiEdge<Operator> {

    public static final String END_OPERATOR_ID = "end";

    public static final EndOperator INSTANCE = new EndOperator();

    @Override
    public String getOperatorId() {
        return END_OPERATOR_ID;
    }

    @Override
    public String getName() {
        return END_OPERATOR_ID;
    }

    @Override
    public void accept(Visitor visitor) throws Exception {

    }

}
