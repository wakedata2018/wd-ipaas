package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.foreach.BreakNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 退出循环
 * @author luomeng
 * @date 2022/12/5 16:47
 */
@Service
public class BreakOperatorProcessor extends BaseOperatorProcessor {
    @Override
    protected String getOperatorClassName() {
        return BreakOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.foreach_break;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return BreakNodeComponent.class;
    }
}
