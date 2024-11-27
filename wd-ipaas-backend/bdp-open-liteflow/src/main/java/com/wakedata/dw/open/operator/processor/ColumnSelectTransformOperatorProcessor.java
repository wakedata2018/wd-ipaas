package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.transform.ColumnSelectNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.transform.ColumnSelectTransformOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 选择列算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 15:43
 */
@Service
public class ColumnSelectTransformOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return ColumnSelectTransformOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.transform_select_column;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return ColumnSelectNodeComponent.class;
    }

}
