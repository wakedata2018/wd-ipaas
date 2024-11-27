package com.wakedata.dw.open.service.impl.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.api.DataAssetService;

import java.util.HashSet;

/**
 * @author ZhangXueJun
 * @title DatasourceApiInvokeStrategy
 * @date 2021/5/27 17:58
 * @projectName dw-open
 * @description
 */
public abstract class DatasourceApiInvokeStrategy<T> extends AbstractApiInvokeStrategy<T> {

    protected DataSourcePo dataSource;
    protected DataAssetService dataAssetService;

    public DatasourceApiInvokeStrategy(
            DataAssetService dataAssetService,
            DataAssetEnums.DataApiType apiKind,
            DataSourcePo dataSource,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            HashSet accessRuleFields) {
        super(apiKind, dataAssetApi, appId, params, accessRuleFields);
        this.dataSource = dataSource;
        this.dataAssetService = dataAssetService;
    }
}
