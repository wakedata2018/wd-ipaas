package com.wakedata.dw.open.openapi;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.strategy.impl.HttpWebServiceApiInvokeStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * webservice
 *
 * @author luomeng
 * @date 2022/10/8 11:37
 */
@Service
@Scope(value = SCOPE_PROTOTYPE,proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OpenApiGatewayWsService extends OpenApiGatewayAbstractService {

    @Resource
    private ExternalApiInvokeService externalApiInvokeService;

    @Override
    public void before(JSONObject params) {

    }

    @Override
    public <T> ResultDTO<T> after(T result, ResultDTO<T> resultDTO) {
        return resultDTO;
    }

    @Override
    public <T> T process(JSONObject params) {
        return (T) new HttpWebServiceApiInvokeStrategy<>(
                externalApiInvokeService,
                dataAssetApiPo.getApiType(),
                dataAssetApiPo,
                appAccessInfo.getDataAccessAppId(),
                params,
                accessRuleFields
        ).invoke();
    }

    @Override
    public <T> T responseBodyHandle(T result) {
        return result;
    }

    @Override
    public List<ApiRespParamDTO> responseHeadHandle(Integer apiId) {
        return null;
    }

    @Override
    public DataAssetEnums.DataApiType apiType() {
        return DataAssetEnums.DataApiType.WEB_SERVICE;
    }
}
