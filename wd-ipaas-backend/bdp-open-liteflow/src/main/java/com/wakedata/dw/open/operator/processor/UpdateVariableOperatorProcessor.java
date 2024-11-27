package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.variable.UpdateVariableNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.variable.UpdateVariableOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 更新变量算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 15:55
 */
@Service
public class UpdateVariableOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return UpdateVariableOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.update_variable;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return UpdateVariableNodeComponent.class;
    }

}
