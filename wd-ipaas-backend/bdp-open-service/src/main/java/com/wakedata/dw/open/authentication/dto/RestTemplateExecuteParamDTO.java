package com.wakedata.dw.open.authentication.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * RestTemplate请求参数DTO
 *
 * @author wujunqiang
 * @since 2022/11/18 10:29
 */
@Data
@ToString
public class RestTemplateExecuteParamDTO {

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求类型枚举类
     */
    private HttpMethod method;

    /**
     * HttpEntity
     */
    private HttpEntity<?> requestEntity;

    /**
     * 响应类型
     */
    private ParameterizedTypeReference<?> responseType;

    /**
     * 变量值Map
     */
    private Map<String, ?> uriVariables;

    /**
     * 连接器API处理器封装参数
     */
    private ConnectorProcessorParamDTO processorParamDTO;

}
