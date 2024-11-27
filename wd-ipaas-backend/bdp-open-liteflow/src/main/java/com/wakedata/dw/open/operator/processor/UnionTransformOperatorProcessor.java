package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.sql.UnionNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.sql.UnionTransformOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * UnionAll算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 15:37
 */
@Service
public class UnionTransformOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return UnionTransformOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.transform_sql_union;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return UnionNodeComponent.class;
    }

}
