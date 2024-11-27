package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.trycatch.TryCatchNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 捕获异常算子执行器
 *
 * @author wujunqiang
 * @since 2023/3/16 10:19
 */
@Service
public class TryCatchOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return TryCatchOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.try_catch;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return TryCatchNodeComponent.class;
    }

}
