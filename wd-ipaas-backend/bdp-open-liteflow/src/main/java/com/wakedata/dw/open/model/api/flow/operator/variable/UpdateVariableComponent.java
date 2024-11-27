package com.wakedata.dw.open.model.api.flow.operator.variable;

import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.model.api.flow.operator.api.PublicKind;
import com.wakedata.dw.open.model.api.flow.operator.variable.model.VariableParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author WangChenSheng
 * @descriptor
 * @title VariableComponent
 * @date 2022/11/15 15:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateVariableComponent extends AbstractComponent {

    private VariableParams variableParams;

    private PublicKind publicKind = PublicKind.self;

}
