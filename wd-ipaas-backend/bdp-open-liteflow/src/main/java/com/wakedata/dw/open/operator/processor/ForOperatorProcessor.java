package com.wakedata.dw.open.operator.processor;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.foreach.ForNodeComponent;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForComponent;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * for循环
 *
 * @author luomeng
 * @date 2022/12/5 16:43
 */
@Service
public class ForOperatorProcessor extends BaseOperatorProcessor {
    @Override
    protected String getOperatorClassName() {
        return ForOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.foreach;
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return ForNodeComponent.class;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        //将循环中的临时参数如循环次数以及当前循环对象值放入参数模板中供子流程使用
        ApiRespParamDTO respParamDTO = getDefaultRespParamDTO();
        List<ApiRespParamDTO> children = new ArrayList<>();
        children.add(getApiRespParam(1, ForNodeComponent.LoopFieldEnum.COUNTER.getField(), ApiResponseHelper.PARAM_TYPE_INTEGER, ForNodeComponent.LoopFieldEnum.COUNTER.getDesc(), null, null));
        ForComponent component = (ForComponent) operator.getComponent();
        //使用循环条件时才需要提供当前循环参数
        if (!component.checkIsCount()) {
            if (ObjectUtil.isNotNull(component.getLoopCondition())
                    && ParamMappingTypeEnum.REFERENCE_TYPE.getType().equals(component.getLoopCondition().getType())) {
                //此处无法解析出item内部的属性，需要在外层通过其他的返回数据进行转换，因此需要传递表达式和节点到外层
                children.add(getApiRespParam(2, ForNodeComponent.LoopFieldEnum.CURRENT_ITEM.getField(), ApiResponseHelper.PARAM_TYPE_OBJECT, ForNodeComponent.LoopFieldEnum.CURRENT_ITEM.getDesc(), (String) component.getLoopCondition().getExpression(), null));
            } else {
                children.add(getApiRespParam(2, ForNodeComponent.LoopFieldEnum.CURRENT_ITEM.getField(), ApiResponseHelper.PARAM_TYPE_OBJECT, ForNodeComponent.LoopFieldEnum.CURRENT_ITEM.getDesc(), null, null));
            }
        }
        respParamDTO.setChildApiRespParams(children);
        return getLiteFlowAllOperatorTemplateDTO(operateId, operatorName, operator.getDesc(), Collections.singletonList(respParamDTO));
    }

    /**
     * 构造响应数据
     *
     * @param id
     * @param fieldName
     * @param fieldType
     * @param fieldDesc
     * @param expression
     * @param sourceNodeId
     * @return
     */
    private ApiRespParamDTO getApiRespParam(Integer id, String fieldName, String fieldType, String fieldDesc, String expression, String sourceNodeId) {
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setId(id);
        apiRespParamDTO.setParentId(ApiResponseHelper.VIRTUAL_ID);
        apiRespParamDTO.setType(HttpParamKind.BODY);
        apiRespParamDTO.setAssetColumns(fieldName);
        apiRespParamDTO.setAssetDataType(fieldType);
        apiRespParamDTO.setDescription(fieldDesc);
        apiRespParamDTO.setExpression(expression);
        apiRespParamDTO.setNodeId(sourceNodeId);
        return apiRespParamDTO;
    }
}
