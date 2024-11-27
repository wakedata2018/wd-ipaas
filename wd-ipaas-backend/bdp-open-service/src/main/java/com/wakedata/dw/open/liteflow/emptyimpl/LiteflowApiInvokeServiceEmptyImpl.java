package com.wakedata.dw.open.liteflow.emptyimpl;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.liteflow.LiteflowApiInvokeService;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;

/**
 * 定义空实现，避免引用报错
 * @author luomeng
 * @date 2022/12/15 12:43
 */
public class LiteflowApiInvokeServiceEmptyImpl implements LiteflowApiInvokeService {
    @Override
    public JSONObject invokeApi(ApiFlowAttr apiAttr, Integer dataAssetApiId, Integer appId, JSONObject params) {
        return null;
    }
}
