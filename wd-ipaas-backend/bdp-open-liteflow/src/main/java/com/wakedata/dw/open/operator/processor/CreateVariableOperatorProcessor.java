package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.variable.CreateVariableNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.variable.CreateVariableOperator;
import com.wakedata.dw.open.model.api.flow.operator.variable.model.VariableParams;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

/**
 * @author WangChenSheng
 * @descriptor 创建变量算子执行器
 * @title CreateVariableProcessor
 * @date 2022/12/13 09:57
 */
@Service
public class CreateVariableOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return CreateVariableOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.create_variable;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildCreateVariableOperatorTemplate(((CreateVariableOperator) operator), operateId, operatorName);
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return CreateVariableNodeComponent.class;
    }

    private LiteFlowAllOperatorTemplateDTO buildCreateVariableOperatorTemplate(CreateVariableOperator operator, String operateId, String operatorName) {
        VariableParams variableParams = operator.getComponent().getVariableParams();
        return buildBaseOperatorTemplate(variableParams.getVariableJsonSchema(), operateId, operatorName , operator.getDesc());
    }
}
