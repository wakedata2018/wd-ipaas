package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.event.EventReceiveNodeComponent;
import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.event.EventReceiveOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

/**
 * 事件接收算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 11:25
 */
@Service
public class EventReceiveOperatorProcessor extends BaseOperatorProcessor {

    @Override
    protected String getOperatorClassName() {
        return EventReceiveOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.event_receive;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildEventReceiveOperatorTemplate(((EventReceiveOperator) operator), operateId, operatorName);
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return EventReceiveNodeComponent.class;
    }

    /**
     * 构建事件接收算子类型的响应参数模版
     *
     * @param operator     事件接收算子
     * @param operateId    算子id
     * @param operatorName 算子名称
     * @return 参数模版
     */
    private LiteFlowAllOperatorTemplateDTO buildEventReceiveOperatorTemplate(EventReceiveOperator operator, String operateId, String operatorName) {
        EventReceiveApiAttr eventReceiveApiAttr = (EventReceiveApiAttr) operator.getComponent().getDataAssetApi().getApiAttr();
        String messageTemplate = eventReceiveApiAttr.getMessageTemplate();
        return buildBaseOperatorTemplate(messageTemplate, operateId, operatorName, operator.getDesc());
    }

}
