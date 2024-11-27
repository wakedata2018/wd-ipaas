package com.wakedata.dw.open.service.api.attr;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;

/**
 * 外部API调用
 *
 * @author ZhangXueJun
 * @title ExternalApiInvokeService
 * @date 2021/3/27 12:01
 * @projectName bdp-open
 * @description
 */
public interface ExternalApiInvokeService<T> {

    /**
     * 调用外部 HTTP API
     *
     * @param dataAssetApi
     * @param params
     * @return
     */
    T queryHttpExternalApi(DataAssetApiPo dataAssetApi, JSONObject params);

    /**
     * 调用连接器HTTP API
     *
     * @param secretKey                连接器密钥唯一标识
     * @param connectorApi             第三方api信息表
     * @param params                   请求参数JSON
     * @param connectorApiAuthTypeEnum 连接器API鉴权类型枚举类
     * @param environmentId            连接环境id
     * @return 调用结果
     */
    T queryHttpConnectorApi(String secretKey, ConnectorApiPo connectorApi, JSONObject params, ConnectorApiAuthTypeEnum connectorApiAuthTypeEnum, Long environmentId);

    /**
     * 调用外部 webservice接口
     *
     * @param dataAssetApiPo
     * @param params
     * @return
     */
    T queryHttpWebServiceApi(DataAssetApiPo dataAssetApiPo, JSONObject params);

    /**
     * 调用外部 webservice wsdl
     *
     * @param dataAssetApiPo DataAssetApiPo
     * @param params         请求参数JsonObject
     * @return T
     */
    T queryHttpWebServiceWsdl(DataAssetApiPo dataAssetApiPo, JSONObject params);
}