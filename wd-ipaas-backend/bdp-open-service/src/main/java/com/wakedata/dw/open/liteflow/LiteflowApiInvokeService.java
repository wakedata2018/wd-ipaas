package com.wakedata.dw.open.liteflow;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;

/**
 * liteflow流程执行
 * @author luomeng
 * @date 2022/12/14 14:25
 */
public interface LiteflowApiInvokeService {


    /**
     * 执行api
     * @param apiAttr
     * @param dataAssetApiId
     * @param appId
     * @param params
     * @return
     */
    JSONObject invokeApi(ApiFlowAttr apiAttr,Integer dataAssetApiId,Integer appId,JSONObject params);


}
