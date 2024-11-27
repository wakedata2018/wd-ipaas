package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.judge.JudgeNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.judge.JudgeOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import org.springframework.stereotype.Service;

/**
 * 判断算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 15:49
 */
@Service
public class JudgeOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return JudgeOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.judge;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return JudgeNodeComponent.class;
    }

}
