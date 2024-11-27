package com.wakedata.dw.open.model.api.flow.operator.connector;

import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorApiRequestParamPo;
import com.wakedata.dw.open.model.connector.ConnectorApiResponseParamPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 连接器算子组件
 *
 * @author wujunqiang
 * @since 2022/11/17 10:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectorComponent extends AbstractComponent {

    /**
     * 连接器API信息
     */
    private ConnectorApiPo connectorApi;

    /**
     * API请求参数集合
     */
    private List<ConnectorApiRequestParamPo> requestParams;

    /**
     * API响应参数集合
     */
    private List<ConnectorApiResponseParamPo> responseParams;

    /**
     * 连接器对应密钥
     */
    private ConnectorSecretKeyPo connectorSecretKey;

    /**
     * 连接器API鉴权类型枚举类
     */
    private ConnectorApiAuthTypeEnum connectorApiAuthType;

    /**
     * 环境id（有些连接器不需要密钥，但还是需要有对应的连接环境）
     */
    private Long environmentId;

}
