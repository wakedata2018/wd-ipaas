package com.wakedata.dw.open.gateway;

import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.service.openapi.OpenApiParams;

import java.util.List;
import java.util.Map;

/**
 * api接口鉴权
 * @author luomeng
 * @date 2022/11/22 10:08
 */
public interface OpenApiAuthService {

    /**
     * 支持的鉴权方式，默认使用accessToken模式
     * @return
     */
    default DataAssetEnums.DataAccessAppAuthType getSupportAuthType(){
        return DataAssetEnums.DataAccessAppAuthType.TOKEN_AUTH;
    }


    /**
     * 身份认证
     * @param openApiParams 请求参数
     * @param appAccessPo 应用信息
     * @return
     */
    AppAccessInfo authenticate(OpenApiParams openApiParams, AppAccessPo appAccessPo);


    /**
     * 获取api测试需要的授权参数
     * @param appAccessPo
     * @return
     */
    Map<String, List<ApiConditionPo>> getApiTestAuthInfo(AppAccessPo appAccessPo);
}
