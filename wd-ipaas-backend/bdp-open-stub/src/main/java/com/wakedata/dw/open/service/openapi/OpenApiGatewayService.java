package com.wakedata.dw.open.service.openapi;

import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.DataAssetApiPo;

/**
 * 开放平台执行api
 * @author luomeng
 * @date 2022/9/30 16:34
 */
public interface OpenApiGatewayService {

    /**
     * 执行api
     * @param openApiParams api参数
     * @param dataAssetApiPo api信息
     * @param appAccessInfo 应用信息
     * @return
     * @param <T>
     */
    <T> ResultDTO<T> invokeOpenApi(OpenApiParams openApiParams, DataAssetApiPo dataAssetApiPo, AppAccessInfo appAccessInfo);

    /**
     * api类型
     * @return
     */
    DataAssetEnums.DataApiType apiType();

}
