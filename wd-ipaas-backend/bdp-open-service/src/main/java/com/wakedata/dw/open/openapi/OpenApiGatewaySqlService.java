package com.wakedata.dw.open.openapi;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.strategy.impl.CustomSqlApiInvokeStrategy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.wakedata.dw.open.service.utils.RequestParamUtils.ORDER_BY;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 自定义sql
 *
 * @author luomeng
 * @date 2022/10/8 11:37
 */
@Service
@Scope(value = SCOPE_PROTOTYPE,proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OpenApiGatewaySqlService extends OpenApiGatewayAbstractService {


    @Resource
    private DataAssetService dataAssetService;

    @Resource
    private OpenApiDataSourceHelper dataSourceHelper;

    @Override
    public void before(JSONObject params) {
        dataSourceHelper.paramFilter(params, this.dataAssetApiPo.getDataSourceId(), this.apiConditionPoList);
        String orderBy = this.openApiParams.getRequest().getParameter(ORDER_BY);
        JSONObject queryParams = params.getJSONObject(HttpParamKind.QUERY.name());
        this.setPageQueryParam(queryParams, orderBy, this.openApiParams.getPageQuery().getPageNo(), this.openApiParams.getPageQuery().getPageSize());

    }

    @Override
    public <T> ResultDTO<T> after(T result, ResultDTO<T> resultDTO) {
        return resultDTO;
    }

    @Override
    public <T> T process(JSONObject params) {
        DataSourcePo dataSource = dataSourceHelper.getDataSourcePo(dataAssetApiPo.getDataSourceId());
        return (T) new CustomSqlApiInvokeStrategy(
                dataAssetService,
                dataAssetApiPo.getApiType(),
                dataSource,
                dataAssetApiPo,
                appAccessInfo.getDataAccessAppId(),
                params,
                accessRuleFields,this.apiConditionPoList).invoke();
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
        return DataAssetEnums.DataApiType.CUSTOM_SQL;
    }
}