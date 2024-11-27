package com.wakedata.dw.open.service.impl.api.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.impl.api.strategy.AbstractApiInvokeStrategy;

/**
 * 连接器API执行策略
 *
 * @author wujunqiang
 * @since 2022/11/17 16:23
 */
public class ConnectorApiInvokeStrategy<T> extends AbstractApiInvokeStrategy<T> {

    /**
     * 连接器API访问环境id
     */
    private final Long environmentId;

    /**
     * 连接器密钥唯一标识
     */
    private final String secretKey;

    /**
     * 连接器API信息
     */
    private final ConnectorApiPo connectorApi;

    /**
     * 连接器API鉴权类型枚举类
     */
    private final ConnectorApiAuthTypeEnum connectorApiAuthTypeEnum;

    /**
     * 外部API调用接口
     */
    private final ExternalApiInvokeService<T> externalApiInvokeService;

    public ConnectorApiInvokeStrategy(
            ExternalApiInvokeService<T> externalApiInvokeService,
            DataAssetEnums.DataApiType apiKind,
            ConnectorApiPo connectorApi,
            JSONObject params,
            String secretKey,
            ConnectorApiAuthTypeEnum connectorApiAuthTypeEnum,
            Long environmentId) {
        super(apiKind, null, null, params, null);
        this.externalApiInvokeService = externalApiInvokeService;
        this.connectorApi = connectorApi;
        this.secretKey = secretKey;
        this.connectorApiAuthTypeEnum = connectorApiAuthTypeEnum;
        this.environmentId = environmentId;
    }

    @Override
    public T invoke() {
        super.setAuthContextParams();
        return (T) externalApiInvokeService.queryHttpConnectorApi(secretKey, connectorApi, requestParams, connectorApiAuthTypeEnum, environmentId);
    }

}
