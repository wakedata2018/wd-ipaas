package com.wakedata.dw.open.authentication;

import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 连接器API处理器工厂
 *
 * @author wujunqiang
 * @since 2022/11/18 10:27
 */
@Component
public class ConnectorApiProcessorFactory {

    @Autowired
    private List<ConnectorApiProcessor> connectorApiProcessors;

    /**
     * 根据枚举类获得对应的处理器
     *
     * @param apiAuthTypeEnum 连接器API鉴权类型枚举类
     * @return ConnectorApiProcessor
     */
    public ConnectorApiProcessor getConnectorApiProcessor(ConnectorApiAuthTypeEnum apiAuthTypeEnum) {
        Optional<ConnectorApiProcessor> processorOptional = connectorApiProcessors.stream().filter(x -> apiAuthTypeEnum == x.getApiAuthType()).findAny();
        return processorOptional.orElseThrow(() -> new IllegalArgumentException("apiAuthTypeEnum error，connector api request fail"));
    }

}
