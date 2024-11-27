package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.transform.RowSelectNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.transform.RowSelectTransformOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 选择行算子执行器
 * @author wujunqiang
 * @since 2022/11/24 15:47
 */
@Service
public class RowSelectTransformOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return RowSelectTransformOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.transform_select_row;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return RowSelectNodeComponent.class;
    }

}
