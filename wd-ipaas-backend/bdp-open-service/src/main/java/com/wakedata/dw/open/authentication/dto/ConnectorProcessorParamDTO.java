package com.wakedata.dw.open.authentication.dto;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import lombok.Data;
import lombok.ToString;

/**
 * 连接器API处理器封装参数
 *
 * @author wujunqiang
 * @since 2022/11/18 11:56
 */
@Data
@ToString
public class ConnectorProcessorParamDTO {

    /**
     * 连接器平台密钥
     */
    private ConnectorSecretKeyPo connectorSecretKey;

    /**
     * 连接器API信息
     */
    private ConnectorApiPo connectorApi;

    /**
     * API请求参数
     */
    private JSONObject params;

    /**
     * 鉴权枚举类
     */
    private ConnectorApiAuthTypeEnum apiAuthTypeEnum;

    /**
     * 请求环境信息
     */
    private ConnectorEnvironmentAddressPo environmentAddress;

}
