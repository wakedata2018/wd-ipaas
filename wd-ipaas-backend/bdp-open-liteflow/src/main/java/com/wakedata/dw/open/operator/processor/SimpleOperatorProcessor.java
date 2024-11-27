package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.simple.SimpleOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * @author wujunqiang
 * @since 2022/11/24 15:34
 */
@Service
public class SimpleOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return SimpleOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return null;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return SimpleNodeComponent.class;
    }

}
