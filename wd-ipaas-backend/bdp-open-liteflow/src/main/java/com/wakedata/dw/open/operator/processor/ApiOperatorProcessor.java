package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.api.ApiFlowNodeComponent;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiComponent;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * API算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 10:59
 */
@Service
public class ApiOperatorProcessor extends BaseOperatorProcessor {

    @Resource
    private ApiResponseParamService apiResponseParamService;

    @Override
    protected String getOperatorClassName() {
        return ApiOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.api;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildApiOperatorTemplate((ApiComponent) operator.getComponent(), operateId, operatorName, operator.getDesc());
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return ApiFlowNodeComponent.class;
    }

    /**
     * 构建API类型的响应参数模版
     *
     * @param apiComponent API算子组件
     * @param operateId    算子id
     * @param operatorName 算子名称
     * @return 参数模版
     */
    private LiteFlowAllOperatorTemplateDTO buildApiOperatorTemplate(ApiComponent apiComponent, String operateId, String operatorName, String operatorDesc) {
        LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO;
        DataAssetApiPo dataAssetApiPo = apiComponent.getDataAssetApi();
        List<ApiRespParamDTO> apiRespParams = apiResponseParamService.listByApiId(dataAssetApiPo.getDataAssetApiId());
        // API算子类型不同，构建参数模版的方式也不同
        liteFlowAllOperatorTemplateDTO = getTemplateFromApiRespParamDtoList(apiRespParams, operateId, operatorName, operatorDesc);
        fillApiInfo(liteFlowAllOperatorTemplateDTO, dataAssetApiPo);
        return liteFlowAllOperatorTemplateDTO;
    }
}
