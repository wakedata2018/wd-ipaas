package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.branch.BranchNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.branch.BranchOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 分支算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/28 11:43
 */
@Service
public class BranchOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return BranchOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.branch;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return BranchNodeComponent.class;
    }

}
