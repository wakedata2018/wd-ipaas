package com.wakedata.dw.open.authentication.processor;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.authentication.BaseConnectorApiProcessor;
import com.wakedata.dw.open.authentication.dto.ConnectorProcessorParamDTO;
import com.wakedata.dw.open.authentication.dto.RestTemplateExecuteParamDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 无需鉴权API处理器
 *
 * @author wujunqiang
 * @since 2022/11/18 12:02
 */
@Slf4j
@Component
@Scope(value = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class NoAuthenticationProcessor extends BaseConnectorApiProcessor {

    @Override
    public ResponseEntity<?> execute(ConnectorProcessorParamDTO processorParamDTO) {
        ConnectorApiPo connectorApi = processorParamDTO.getConnectorApi();
        RestTemplateExecuteParamDTO paramDTO = new RestTemplateExecuteParamDTO();
        paramDTO.setUrl(buildRequestUrl(processorParamDTO.getEnvironmentAddress().getEnvironmentAddress(), connectorApi.getApiMethod(), processorParamDTO.getParams()));
        HttpMethod httpMethod = HttpMethod.resolve(connectorApi.getReqMethod().name());
        paramDTO.setMethod(httpMethod);
        paramDTO.setRequestEntity(createHttpEntity(httpMethod, processorParamDTO.getParams()));
        paramDTO.setUriVariables(null);
        paramDTO.setProcessorParamDTO(processorParamDTO);
        return executeRequest(getRetryCount(), paramDTO);
    }

    /**
     * 请求第三方平台URL
     *
     * @param retryCount                  请求重试次数
     * @param restTemplateExecuteParamDTO RestTemplate请求参数DTO
     * @return ResponseEntity
     */
    private ResponseEntity<?> executeRequest(int retryCount, RestTemplateExecuteParamDTO restTemplateExecuteParamDTO) {
        try {
            if(ObjectUtil.isNull(restTemplateExecuteParamDTO.getUriVariables())){
                restTemplateExecuteParamDTO.setUriVariables(new HashMap<>(2));
            }
            return restTemplate.exchange(
                    restTemplateExecuteParamDTO.getUrl(),
                    restTemplateExecuteParamDTO.getMethod(),
                    restTemplateExecuteParamDTO.getRequestEntity(),
                    new ParameterizedTypeReference<String>() {
                    },
                    restTemplateExecuteParamDTO.getUriVariables()
            );
        } catch (RestClientException e) {
            log.error("request connector api fail, request url: {},retry count :{}", restTemplateExecuteParamDTO.getUrl(),retryCount,e);
            retryCount--;
            if(retryCount <= 0){
                return null;
            }
            return executeRequest(retryCount,restTemplateExecuteParamDTO);
        }
    }

    @Override
    public ConnectorApiAuthTypeEnum getApiAuthType() {
        return ConnectorApiAuthTypeEnum.NO_AUTHENTICATION;
    }

    @Override
    protected String getAuthUrl() {
        return null;
    }

}
