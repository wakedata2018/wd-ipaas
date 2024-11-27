package com.wakedata.dw.open.liteflow;

import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.OperatorAttribute;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;

import java.util.List;

/**
 * 算子定义及公共参数获取
 * @author luomeng
 * @date 2022/12/14 14:17
 */
public interface OperatorGraphLiteflowService {


    /**
     * 获取算子参数模板
     * @param apiFlowAttr
     * @param operatorType
     * @return
     */
    List<LiteFlowAllOperatorTemplateDTO> getLiteFlowAllOperatorTemplateDTOList(ApiFlowAttr apiFlowAttr, String operatorType,Boolean isForBreak);

    /**
     * 获取当前算子的入边信息
     * @param apiFlowAttr
     * @param operatorId
     * @return
     */
    List<Operator> getOperatorInputEdges(ApiFlowAttr apiFlowAttr, String operatorId);

    /**
     * 组装算子边类型信息
     * @param operatorAttribute
     * @param operator
     * @return
     */
    OperatorAttribute assembleEdgeAttribute(OperatorAttribute operatorAttribute, Operator operator) ;
}
