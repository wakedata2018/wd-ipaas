package com.wakedata.dw.open.service.impl.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.impl.api.DataApiAccessServiceImpl;
import com.wakedata.dw.open.service.impl.api.strategy.impl.*;

import java.util.HashSet;
import java.util.List;

/**
 * @author ZhangXueJun
 * @title ApiInvokeStrategyFactory
 * @date 2021/5/27 17:41
 * @projectName dw-open
 * @description
 */
public class ApiInvokeStrategyFactory {

    public static AbstractApiInvokeStrategy getApiInvokeStrategy(
            DataApiAccessServiceImpl dataApiAccessService,
            DataAssetEnums.DataApiType apiKind,
            DataAssetEnums.DataApiOperationType operationType,
            DataSourcePo dataSource,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            HashSet<String> accessRuleFields,
            List<ApiConditionPo> apiConditions) {
        switch (dataAssetApi.getApiType()) {
            case NORMAL_TABLE:
                return new TableApiInvokeStrategy(
                        dataApiAccessService.getDataAssetService(),
                        apiKind,
                        operationType,
                        dataSource,
                        dataAssetApi,
                        appId,
                        params,
                        accessRuleFields,
                        apiConditions);
            case CUSTOM_SQL:
                return new CustomSqlApiInvokeStrategy(
                        dataApiAccessService.getDataAssetService(),
                        apiKind,
                        dataSource,
                        dataAssetApi,
                        appId,
                        params,
                        accessRuleFields,
                        apiConditions);
            case EXTERNAL_HTTP:
                return new HttpExternalApiInvokeStrategy(
                        dataApiAccessService.getExternalApiInvokeService(),
                        apiKind,
                        dataAssetApi,
                        appId,
                        params,
                        accessRuleFields
                );
            case WEB_SERVICE:
                return new HttpWebServiceApiInvokeStrategy<>(
                        dataApiAccessService.getExternalApiInvokeService(),
                        apiKind,
                        dataAssetApi,
                        appId,
                        params,
                        accessRuleFields
                );
            default:
                return new LiteFlowApiInvokeStrategy(
                        dataApiAccessService,
                        apiKind,
                        dataAssetApi,
                        appId,
                        params,
                        accessRuleFields
                );
        }
    }
}
