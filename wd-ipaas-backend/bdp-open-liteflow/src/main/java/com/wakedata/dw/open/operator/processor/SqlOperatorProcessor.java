package com.wakedata.dw.open.operator.processor;

import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.sql.SqlNodeComponent;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlComponent;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * sql算子执行器
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/2/14 19:02
 */
@Service
public class SqlOperatorProcessor extends BaseOperatorProcessor {

    /**
     * sql算子响应参数模板不存库，所以id为空，默认给id赋值1，用于生成响应参数模板树
     */
    private static final int API_RESP_PARAM_FIRST_ID = 1;

    @Override
    protected String getOperatorClassName() {
        return SqlOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.sql_execute;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildSqlOperatorTemplate((SqlComponent) operator.getComponent(), operateId, operatorName, operator.getDesc());
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return SqlNodeComponent.class;
    }

    /**
     * 构建SQL算子类型的响应参数模版
     *
     * @param sqlComponent SQL算子组件
     * @param operateId    算子id
     * @param operatorName 算子名称
     * @return 参数模版
     */
    private LiteFlowAllOperatorTemplateDTO buildSqlOperatorTemplate(SqlComponent sqlComponent, String operateId, String operatorName, String operatorDesc) {
        LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO;
        List<ApiRespParamDTO> apiRespParams = sqlComponent.getResponseParams();
        // sql算子响应参数模板不存库，所以id为空，默认给第一层的id赋值1，用于生成响应参数模板树，第二层及以下的自动赋值
        if (apiRespParams.size() > 0) {
            apiRespParams.get(0).setId(API_RESP_PARAM_FIRST_ID);
        }
        // API算子类型不同，构建参数模版的方式也不同
        liteFlowAllOperatorTemplateDTO = getTemplateFromApiRespParamDtoList(apiRespParams, operateId, operatorName, operatorDesc);
        return liteFlowAllOperatorTemplateDTO;
    }
}
