package com.wakedata.dw.open.service.api;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;

import java.util.List;

/**
 * @author yiyufeng
 * @title DataApiAccessService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface DataApiAccessService {

    /**
     * 根据请求条件动态分页访问数据库数据，支持分页
     *
     * @param dataAssetApi
     * @param appId
     * @param params
     * @param page
     * @param size
     * @param orderBy
     * @return
     */
    <T> T readDataApiData(
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            int page,
            int size,
            String orderBy);

    /**
     * 根据请求条件实现数据删除
     * @param dataAssetApi
     * @param appId
     * @param params
     * @return
     */
    <T> T dmlDataApiData(
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            List<ApiConditionPo> apiConditions);

    /**
     * 获取数据量总量
     *
     * @param dataAssetApi
     * @param params
     * @return
     */
    int getDataCount(DataAssetApiPo dataAssetApi, JSONObject params);
}
