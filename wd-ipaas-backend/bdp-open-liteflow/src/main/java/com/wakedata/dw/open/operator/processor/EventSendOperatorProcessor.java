package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.event.EventSendNodeComponent;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.event.EventSendOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

/**
 * 事件发送算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 11:22
 */
@Service
public class EventSendOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return EventSendOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.event_send;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildEventSendOperatorTemplate(((EventSendOperator) operator), operateId, operatorName);
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return EventSendNodeComponent.class;
    }

    /**
     * 构建事件发送算子类型的响应参数模版
     *
     * @param operator     事件发送算子
     * @param operateId    算子id
     * @param operatorName 算子名称
     * @return 参数模版
     */
    private LiteFlowAllOperatorTemplateDTO buildEventSendOperatorTemplate(EventSendOperator operator, String operateId, String operatorName) {
        EventSendApiAttr eventSendApiAttr = (EventSendApiAttr) operator.getComponent().getDataAssetApi().getApiAttr();
        String messageTemplate = eventSendApiAttr.getMessageTemplate();
        return buildBaseOperatorTemplate(messageTemplate, operateId, operatorName, operator.getDesc());
    }

}
