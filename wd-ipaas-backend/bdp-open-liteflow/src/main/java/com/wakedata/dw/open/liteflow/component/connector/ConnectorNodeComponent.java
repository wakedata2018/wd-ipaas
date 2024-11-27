package com.wakedata.dw.open.liteflow.component.connector;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorComponent;
import com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorOperator;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorApiRequestParamPo;
import com.wakedata.dw.open.service.impl.api.DataApiAccessServiceImpl;
import com.wakedata.dw.open.service.impl.api.strategy.impl.ConnectorApiInvokeStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连接器算子LiteFlow组件
 *
 * @author wujunqiang
 * @since 2022/11/17 11:03
 */
public class ConnectorNodeComponent extends AbstractNodeComponent<ConnectorOperator> {

    /**
     * 获取请求参数集合
     *
     * @return 请求参数集合
     */
    @Override
    protected List<RequestParamMapping> getRequestParamMappings() {
        ConnectorOperator operator = (ConnectorOperator) threadLocal.get().getOperator();
        return operator.getRequestParamMappings();
    }

    @Override
    protected JSON afterMappingRequestParams(JSON currentRequestParams) {
        ConnectorOperator operator = (ConnectorOperator) threadLocal.get().getOperator();
        List<RequestParamMapping> requestParamMappings = operator.getRequestParamMappings();
        Map<String, RequestParamMapping> fieldMappings = new HashMap<>(requestParamMappings.size());
        for (RequestParamMapping requestParamMapping : requestParamMappings) {
            if (requestParamMapping != null) {
                String key = requestParamMapping.getField() + ":" + requestParamMapping.getHttpParamKind().name();
                fieldMappings.put(key, requestParamMapping);
            }
        }

        ConnectorComponent component = operator.getComponent();
        List<ConnectorApiRequestParamPo> parameters = component.getRequestParams();
        for (ConnectorApiRequestParamPo parameter : parameters) {
            String key = parameter.getAssetColumns() + ":" + parameter.getHttpParamKind().name();
            RequestParamMapping requestParamMapping = fieldMappings.get(key);
            if (requestParamMapping == null) {
                continue;
            }
            requestParamMapping.setHttpParamKind(parameter.getHttpParamKind());
        }

        if (currentRequestParams instanceof JSONObject) {
            JSONObject origin = (JSONObject) currentRequestParams;
            return super.assembleHttpRequestParams(origin, fieldMappings);
        }

        JSONArray origins = (JSONArray) currentRequestParams;
        JSONArray result = new JSONArray();
        for (Object origin : origins) {
            result.add(super.assembleHttpRequestParams((JSONObject) origin, fieldMappings));
        }
        return result;
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        Object results = invokeApi(nodeRunTimeContext, currentRequestParams);
        return AbstractNodeComponent.jsonParse(results);
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_connector_operator_error;
    }

    /**
     * 调用连接器API
     *
     * @param nodeRunTimeContext 流程编排运行上下文
     * @param requestParam       当前节点的请求参数JSON
     * @return 调用结果
     */
    private <T> T invokeApi(NodeRunTimeContext nodeRunTimeContext, JSONObject requestParam) {
        // ApiFlowComponent会有一个转换参数的方法，这里看起来应该不需要，如果参数有问题再参考ApiFlowComponent.transformApiRequestParams()的实现
        ConnectorOperator operator = (ConnectorOperator) nodeRunTimeContext.getOperator();
        ConnectorComponent component = operator.getComponent();
        ConnectorApiPo connectorApi = component.getConnectorApi();
        DataApiAccessServiceImpl dataApiAccessService = ((DataApiAccessServiceImpl) nodeRunTimeContext.getDataApiAccessService());
        //加入鉴权参数
        JSONObject startInputParams = nodeRunTimeContext.getOperatorContainer().getStartInputParams();
        if(ObjectUtil.isNotNull(startInputParams)){
            requestParam.put(HttpParamKind.COMMON.name(),startInputParams.getJSONObject(HttpParamKind.COMMON.name()));
        }
        ConnectorApiInvokeStrategy<T> connectorApiInvokeStrategy = new ConnectorApiInvokeStrategy(
                dataApiAccessService.getExternalApiInvokeService(),
                connectorApi.getApiType(),
                connectorApi,
                requestParam,
                component.getConnectorSecretKey().getSecretKey(),
                component.getConnectorApiAuthType(),
                component.getEnvironmentId());
        return connectorApiInvokeStrategy.invoke();
    }


    @Override
    public void processCall() throws Exception {
        // 1、处理请求参数映射
        beforeProcessInternal();

        // 2、内部处理算子
        processInternal();

        // 3、处理响应参数映射
        afterProcessInternal();
    }
}
