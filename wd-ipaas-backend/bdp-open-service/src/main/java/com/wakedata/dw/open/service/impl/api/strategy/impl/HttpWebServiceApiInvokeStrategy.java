package com.wakedata.dw.open.service.impl.api.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.impl.api.strategy.AbstractApiInvokeStrategy;

import java.util.HashSet;

/**
 * http webservice调用策略
 *
 * @author luomeng
 * @date 2022/8/24 16:14
 */
public class HttpWebServiceApiInvokeStrategy<T> extends AbstractApiInvokeStrategy<T> {

    private ExternalApiInvokeService externalApiInvokeService;

    public HttpWebServiceApiInvokeStrategy(
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
        JSONObject query = requestParams.getJSONObject(HttpParamKind.QUERY.name());
        Boolean accessWsdl = query.getBoolean(DwOpenConstant.WEB_SERVICE_ACCESS_WSDL);
        if (accessWsdl != null && accessWsdl) {
            return (T) externalApiInvokeService.queryHttpWebServiceWsdl(dataAssetApi, requestParams);
        }
        return (T) externalApiInvokeService.queryHttpWebServiceApi(dataAssetApi, requestParams);
    }
}
