package com.wakedata.dw.open.authentication;

import com.wakedata.dw.open.authentication.dto.ConnectorProcessorParamDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import org.springframework.http.ResponseEntity;

/**
 * 连接器API处理器接口
 *
 * @author wujunqiang
 * @since 2022/11/18 10:13
 */
public interface ConnectorApiProcessor {

    /**
     * 根据不同的连接器平台进行鉴权后构建API最终的请求参数
     *
     * @param processorParamDTO 连接器API处理器封装参数
     * @return ResponseEntity
     */
    ResponseEntity<?> execute(ConnectorProcessorParamDTO processorParamDTO);

    /**
     * 获取连接器API鉴权类型枚举类
     *
     * @return ConnectorApiAuthTypeEnum
     */
    ConnectorApiAuthTypeEnum getApiAuthType();

}
