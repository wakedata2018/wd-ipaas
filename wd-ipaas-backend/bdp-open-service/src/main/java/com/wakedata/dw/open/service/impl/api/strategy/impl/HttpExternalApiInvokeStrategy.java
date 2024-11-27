package com.wakedata.dw.open.service.impl.api.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.impl.api.strategy.AbstractApiInvokeStrategy;

import java.util.HashSet;

/**
 * @author ZhangXueJun
 * @title HttpExternalApiInvokeStrategy
 * @date 2021/5/28 10:23
 * @projectName dw-open
 * @description
 */
public class HttpExternalApiInvokeStrategy<T> extends AbstractApiInvokeStrategy<T> {

    private ExternalApiInvokeService externalApiInvokeService;

    public HttpExternalApiInvokeStrategy(
            ExternalApiInvokeService externalApiInvokeService,
            DataAssetEnums.DataApiType apiKind,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            HashSet<String> accessRuleFields) {
        super(apiKind, dataAssetApi, appId, params, accessRuleFields);
        this.externalApiInvokeService = externalApiInvokeService;
    }

    @Override
    public T invoke() {
        super.setWdCloudTenantParam();
        super.setAuthContextParams();
        return (T) externalApiInvokeService.queryHttpExternalApi(dataAssetApi, requestParams);
    }
}
