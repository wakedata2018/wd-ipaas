package com.wakedata.dw.open.operator;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;

/**
 * 算子执行器接口
 *
 * @author wujunqiang
 * @since 2022/11/24 10:19
 */
public interface OperatorProcessor {

    /**
     * 根据不同的API算子构建对应的LiteFlowAllOperatorTemplateDTO
     *
     * @param operator     算子
     * @param apiId        流程编排API ID（部分算子需要用到这个参数）
     * @param operateId    算子id
     * @param operatorName 算子名称
     * @return LiteFlowAllOperatorTemplateDTO
     */
    LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName);

    /**
     * 根据不同的算子获取对应的lite flow组件类
     *
     * @return lite flow组件类
     */
    Class<? extends AbstractNodeComponent> getNodeComponent();

}
