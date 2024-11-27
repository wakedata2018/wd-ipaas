package com.wakedata.dw.open.authentication.processor;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.authentication.BaseConnectorApiProcessor;
import com.wakedata.dw.open.authentication.dto.ConnectorProcessorParamDTO;
import com.wakedata.dw.open.authentication.dto.RestTemplateExecuteParamDTO;
import com.wakedata.dw.open.connector.dto.ew.GetTokenResponse;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 企业微信API处理器
 *
 * @author wujunqiang
 * @since 2022/11/18 10:44
 */
@Slf4j
@Component
@Scope(value = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EnterpriseWechatProcessor extends BaseConnectorApiProcessor {

    /**
     * 错误码，企微API返回参数
     */
    private static final String ERR_CODE = "errcode";

    /**
     * 企业ID，获取token需要用的参数名
     */
    private static final String CORP_ID = "corpid";

    /**
     * 应用的凭证密钥，获取token需要用的参数名
     */
    private static final String CORP_SECRET = "corpsecret";

    /**
     * 调用接口凭证参数
     */
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * access_token已过期错误码
     */
    private static final Integer ACCESS_TOKEN_EXPIRED_ERROR_CODE = 42001;

    /**
     * 企业微信token缓存key模版，填入的值依次是租户id、密钥id
     */
    private static final String CORP_TOKEN_REDIS_KEY_FORMAT = "connector:ew:corp:token:%s:%s";

    @Override
    public ResponseEntity<?> execute(ConnectorProcessorParamDTO processorParamDTO) {
        ConnectorSecretKeyPo connectorSecretKey = processorParamDTO.getConnectorSecretKey();
        ConnectorEnvironmentAddressPo environmentAddress = processorParamDTO.getEnvironmentAddress();
        ConnectorApiPo connectorApi = processorParamDTO.getConnectorApi();
        // 校验鉴权参数是否齐全
        JSONObject authParam = checkAuthParam(connectorSecretKey, getApiAuthType().getDesc());
        // 获取token，并将token设置到QUERY参数中
        String token = getToken(processorParamDTO, authParam);
        JSONObject params = processorParamDTO.getParams();
        params.getJSONObject(HttpParamKind.QUERY.name()).put(ACCESS_TOKEN, token);
        // 构建RestTemplateExecuteParamDTO
        RestTemplateExecuteParamDTO paramDTO = new RestTemplateExecuteParamDTO();
        paramDTO.setUrl(buildRequestUrl(environmentAddress.getEnvironmentAddress(), connectorApi.getApiMethod(), params));
        HttpMethod httpMethod = HttpMethod.resolve(connectorApi.getReqMethod().name());
        paramDTO.setMethod(httpMethod);
        paramDTO.setRequestEntity(createHttpEntity(httpMethod, processorParamDTO.getParams()));
        paramDTO.setUriVariables(null);
        paramDTO.setProcessorParamDTO(processorParamDTO);
        return executeRequest(getRetryCount(), paramDTO);
    }

    @Override
    public ConnectorApiAuthTypeEnum getApiAuthType() {
        return ConnectorApiAuthTypeEnum.ENTERPRISE_WECHAT;
    }

    @Override
    protected String getAuthUrl() {
        return "/gettoken?corpid={corpid}&corpsecret={corpsecret}";
    }

    /**
     * 请求第三方平台URL
     *
     * @param retryCount                  请求重试次数
     * @param restTemplateExecuteParamDTO RestTemplate请求参数DTO
     * @return ResponseEntity
     */
    private ResponseEntity<?> executeRequest(int retryCount, RestTemplateExecuteParamDTO restTemplateExecuteParamDTO) {
        while (retryCount > 0) {
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        restTemplateExecuteParamDTO.getUrl(),
                        restTemplateExecuteParamDTO.getMethod(),
                        restTemplateExecuteParamDTO.getRequestEntity(),
                        new ParameterizedTypeReference<String>() {
                        },
                        restTemplateExecuteParamDTO.getUriVariables() == null ? new HashMap<>(16) : restTemplateExecuteParamDTO.getUriVariables()
                );
                JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
                // 如果返回的错误码是access_token已过期，重新生成Token后重试
                if (jsonObject.containsKey(ERR_CODE) && ACCESS_TOKEN_EXPIRED_ERROR_CODE.equals(jsonObject.getInteger(ERR_CODE))) {
                    resetToken(restTemplateExecuteParamDTO);
                } else {
                    return responseEntity;
                }
                retryCount--;
            } catch (Exception e) {
                resetToken(restTemplateExecuteParamDTO);
                log.error("request connector api fail, request url: {}", restTemplateExecuteParamDTO.getUrl());
                log.error(e.getMessage(), e);
                retryCount--;
            }
        }
        log.error("request connector api fail, request url: {}", restTemplateExecuteParamDTO.getUrl());
        throw new OpenException(OpenApiMsgCodeEnum.w_request_connector_api_fail);
    }

    /**
     * 校验鉴权参数
     *
     * @param connectorSecretKey 连接器平台密钥
     * @param connectorName      平台名称
     * @return 鉴权参数JSONObject
     */
    private JSONObject checkAuthParam(ConnectorSecretKeyPo connectorSecretKey, String connectorName) {
        JSONObject authParam = JSONObject.parseObject(connectorSecretKey.getParamsJson());
        if (!authParam.containsKey(CORP_ID)) {
            throw new OpenException(MISSING_AUTH_PARAM.getCode(), String.format(MISSING_AUTH_PARAM.getDesc(), CORP_ID, connectorName));
        }
        if (!authParam.containsKey(CORP_SECRET)) {
            throw new OpenException(MISSING_AUTH_PARAM.getCode(), String.format(MISSING_AUTH_PARAM.getDesc(), CORP_SECRET, connectorName));
        }
        return authParam;
    }

    /**
     * 重新生成Token
     *
     * @param restTemplateExecuteParamDTO RestTemplate请求参数DTO
     */
    private void resetToken(RestTemplateExecuteParamDTO restTemplateExecuteParamDTO) {
        ConnectorProcessorParamDTO processorParamDTO = restTemplateExecuteParamDTO.getProcessorParamDTO();
        // 清除Redis中的token（避免出现Redis中的token在，实际上企微让token提前失效导致无法请求）
        String redisKey = buildTokenRedisKey(processorParamDTO);
        RedisUtil.getInstance().del(redisKey);
        // 重新获取token保存到Redis中
        ConnectorSecretKeyPo connectorSecretKey = processorParamDTO.getConnectorSecretKey();
        JSONObject authParam = checkAuthParam(connectorSecretKey, getApiAuthType().getDesc());
        String token = getToken(processorParamDTO, authParam);
        JSONObject params = processorParamDTO.getParams();
        params.getJSONObject(HttpParamKind.QUERY.name()).put(ACCESS_TOKEN, token);
        // 重构接口URL，拼接新的token
        ConnectorEnvironmentAddressPo environmentAddress = processorParamDTO.getEnvironmentAddress();
        ConnectorApiPo connectorApi = processorParamDTO.getConnectorApi();
        restTemplateExecuteParamDTO.setUrl(buildRequestUrl(environmentAddress.getEnvironmentAddress(), connectorApi.getApiMethod(), params));
    }

    /**
     * 鉴权，获取token
     *
     * @param processorParamDTO 连接器API处理器封装参数
     * @param authParam         鉴权参数JSONObject
     * @return token
     */
    private String getToken(ConnectorProcessorParamDTO processorParamDTO, JSONObject authParam) {
        ConnectorEnvironmentAddressPo environmentAddress = processorParamDTO.getEnvironmentAddress();
        // 先查看Redis中是否存在token，存在直接返回
        RedisUtil redisUtil = RedisUtil.getInstance();
        String redisKey = buildTokenRedisKey(processorParamDTO);
        String token = redisUtil.get(redisKey);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        // 如果不存在token则调用企业微信获取access_token接口
        String url = environmentAddress.getEnvironmentAddress() + getAuthUrl();
        Map<String, String> uriVariables = new HashMap<>(2);
        uriVariables.put(CORP_ID, authParam.getString(CORP_ID));
        uriVariables.put(CORP_SECRET, authParam.getString(CORP_SECRET));
        GetTokenResponse getTokenResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<GetTokenResponse>() {
                },
                uriVariables
        ).getBody();
        if (getTokenResponse == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_authentication_fail_no_msg);
        }
        if (!getTokenResponse.isFail()) {
            OpenApiMsgCodeEnum codeEnum = OpenApiMsgCodeEnum.w_authentication_fail;
            throw new OpenException(codeEnum.getCode(), String.format(codeEnum.getDesc(), getTokenResponse.getErrMsg(), url));
        }
        // 保存token到redis
        String accessToken = getTokenResponse.getAccessToken();
        redisUtil.set(redisKey, accessToken, getTokenResponse.getExpiresIn());
        return accessToken;
    }

    /**
     * 构建Token保存在Redis中的key
     *
     * @param processorParamDTO 连接器API处理器封装参数
     * @return Redis Key
     */
    private String buildTokenRedisKey(ConnectorProcessorParamDTO processorParamDTO) {
        ConnectorApiPo connectorApi = processorParamDTO.getConnectorApi();
        ConnectorSecretKeyPo connectorSecretKey = processorParamDTO.getConnectorSecretKey();
        return String.format(CORP_TOKEN_REDIS_KEY_FORMAT, connectorApi.getLesseeId(), connectorSecretKey.getId());
    }

}
