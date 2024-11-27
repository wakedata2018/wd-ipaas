package com.wakedata.dw.open.service.thirdparty.auth;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.model.api.DataAssetApiPo;

/**
 * 第三方认证服务
 * @author 佟蕊
 */
public interface ThirdPartyAuthService {

    /**
     * 第三方认证执行
     * @param dataAssetApi api封装信息
     * @param params 原执行参数
     * @return 认证后的执行参数
     */
    JSONObject invokeAuth(DataAssetApiPo dataAssetApi, JSONObject params);
}
