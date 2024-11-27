package com.wakedata.dw.open.service.impl.api.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.helper.RedisLockHelper;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.impl.api.strategy.DatasourceApiInvokeStrategy;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;

/**
 * @author ZhangXueJun
 * @title SqlApiInvokeStrategy
 * @date 2021/5/28 9:43
 * @projectName dw-open
 * @description
 */
public class CustomSqlApiInvokeStrategy<T> extends DatasourceApiInvokeStrategy<T> {

    /**
     * 允许为空参数列表
     */
    private List<ApiConditionPo> apiConditionPoList;

    public CustomSqlApiInvokeStrategy(
            DataAssetService dataAssetService,
            DataAssetEnums.DataApiType apiKind,
            DataSourcePo dataSource,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            HashSet<String> accessRuleFields,
            List<ApiConditionPo> apiConditionPoList) {
        super(dataAssetService, apiKind, dataSource, dataAssetApi, appId, params, accessRuleFields);
        this.apiConditionPoList = apiConditionPoList;
    }

    @Override
    public T invoke() {
        super.setWdCloudTenantParam();
        JSONObject param = DataAssetEnums.ReqMethod.POST == dataAssetApi.getReqMethod() ?
                requestParams.getJSONObject(HttpParamKind.BODY.name()) : requestParams.getJSONObject(HttpParamKind.QUERY.name());
        return (T) dataAssetService.readDataAssetDataBySql(
                dataSource,
                dataAssetApi.getApiSql(),
                accessRuleFields,
                this.apiConditionPoList,
                param,
                JsonUtil.getPageParam(param, RequestParamUtils.PAGE_NO, PageQuery.DEFAULT_PAGE_NO),
                JsonUtil.getPageParam(param, RequestParamUtils.PAGE_SIZE, PageQuery.DEFAULT_PAGE_SIZE),
                JsonUtil.getParam(param, RequestParamUtils.ORDER_BY, StringUtils.EMPTY),
                RedisLockHelper.buildLockInfoFromApi(param, ((RedisLockConfigAttr) dataAssetApi.getApiAttr()))
        );
    }

}
