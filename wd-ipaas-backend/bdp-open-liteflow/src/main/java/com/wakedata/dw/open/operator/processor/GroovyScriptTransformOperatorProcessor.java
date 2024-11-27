package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.script.GroovyScriptNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.script.GroovyScriptTransformOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

/**
 * Groovy脚本算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 11:06
 */
@Service
public class GroovyScriptTransformOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return GroovyScriptTransformOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.transform_groovery_script;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildBaseOperatorTemplate(((GroovyScriptTransformOperator) operator).getResultData(), operateId, operatorName, operator.getDesc());
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return GroovyScriptNodeComponent.class;
    }

}
