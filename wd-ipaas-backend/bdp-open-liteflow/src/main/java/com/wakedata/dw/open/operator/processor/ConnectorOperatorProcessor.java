package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.connector.ConnectorNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorComponent;
import com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorOperator;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 连接器算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 11:50
 */
@Service
public class ConnectorOperatorProcessor extends BaseOperatorProcessor {

    @Resource
    private ApiResponseParamService apiResponseParamService;

    @Override
    protected String getOperatorClassName() {
        return ConnectorOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.connector;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildConnectorOperatorTemplate((ConnectorComponent) operator.getComponent(), operateId, operatorName, operator.getDesc());
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return ConnectorNodeComponent.class;
    }

    private LiteFlowAllOperatorTemplateDTO buildConnectorOperatorTemplate(ConnectorComponent component, String operateId, String operatorName, String operatorDesc) {
        ConnectorApiPo connectorApi = component.getConnectorApi();
        List<ApiRespParamDTO> connectorApiRespParams = apiResponseParamService.getApiRespParamByConnectorId(connectorApi.getId());
        LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO = getTemplateFromApiRespParamDtoList(connectorApiRespParams, operateId, operatorName, operatorDesc);
        fillConnectorApiInfo(liteFlowAllOperatorTemplateDTO, connectorApi);
        return liteFlowAllOperatorTemplateDTO;
    }

    /**
     * 填充api信息到LiteFlowAllOperatorTemplateDTO中
     *
     * @param dto          流程编排所有算子的参数模板
     * @param connectorApi 连接器API
     */
    protected void fillConnectorApiInfo(LiteFlowAllOperatorTemplateDTO dto, ConnectorApiPo connectorApi) {
        if (connectorApi == null) {
            return;
        }
        dto.setApiName(connectorApi.getApiName());
        dto.setApiDescription(connectorApi.getApiDescription());
    }

}
