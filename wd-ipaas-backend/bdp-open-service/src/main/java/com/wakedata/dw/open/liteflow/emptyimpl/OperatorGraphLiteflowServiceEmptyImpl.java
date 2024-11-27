package com.wakedata.dw.open.liteflow.emptyimpl;

import com.wakedata.dw.open.liteflow.OperatorGraphLiteflowService;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.OperatorAttribute;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;

import java.util.List;

/**
 * 定义空实现
 * @author luomeng
 * @date 2022/12/15 12:44
 */
public class OperatorGraphLiteflowServiceEmptyImpl implements OperatorGraphLiteflowService {
    @Override
    public List<LiteFlowAllOperatorTemplateDTO> getLiteFlowAllOperatorTemplateDTOList(ApiFlowAttr apiFlowAttr, String operatorType,Boolean isForBreak) {
        return null;
    }

    @Override
    public List<Operator> getOperatorInputEdges(ApiFlowAttr apiFlowAttr, String operatorId) {
        return null;
    }

    @Override
    public OperatorAttribute assembleEdgeAttribute(OperatorAttribute operatorAttribute, Operator operator) {
        return null;
    }
}
