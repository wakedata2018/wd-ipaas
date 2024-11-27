package com.wakedata.dw.open.authentication.processor;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.authentication.BaseConnectorApiProcessor;
import com.wakedata.dw.open.authentication.dto.ConnectorProcessorParamDTO;
import com.wakedata.dw.open.authentication.dto.RestTemplateExecuteParamDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.connector.ConnectorApiPo;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 惟客云API处理器
 *
 * @author wujunqiang
 * @since 2022/12/1 15:57
 */
@Slf4j
@Component
@Scope(value = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WakeCloudProcessor extends BaseConnectorApiProcessor {

    /**
     * 惟客云登陆接口用户名参数
     */
    private static final String USERNAME = "userName";

    /**
     * 惟客云登陆接口密码参数
     */
    private static final String PASSWORD = "password";

    /**
     * 惟客云选择租户接口参数
     */
    private static final String TENANT_ID = "tenantId";

    /**
     * 惟客云选择应用接口参数
     */
    private static final String APP_BU_ID = "appBuId";

    /**
     * 鉴权参数集合
     */
    private static final List<String> AUTH_KEYS = Arrays.asList(USERNAME, PASSWORD, TENANT_ID, APP_BU_ID);

    /**
     * 惟客云响应参数code
     */
    private static final String RESPONSE_CODE = "code";

    /**
     * 惟客云响应参数msg
     */
    private static final String RESPONSE_MSG = "msg";

    /**
     * 惟客云接口成功code
     */
    private static final Integer SUCCESS_CODE = 100;

    /**
     * 未登录code
     */
    private static final Integer NO_LOGIN_CODE = 401;

    /**
     * Set-Cookie参数
     */
    private static final String SET_COOKIE = "Set-Cookie";

    /**
     * 选择租户接口URL
     */
    private static final String CHOOSE_TENANT_URL = "/wd/employee/web/login/choose.tenant";

    /**
     * 选择应用接口URL
     */
    private static final String CHOOSE_APP_URL = "/wd/employee/web/login/choose.app";

    /**
     * 惟客云Cookie缓存key模版，填入的值依次是租户id、密钥id
     */
    private static final String WD_COOKIE_REDIS_KEY_FORMAT = "connector:wd:cookie:%s:%s";

    /**
     * Cookie有效时长，30分钟
     */
    private static final Long COOKIE_EXPIRATION_TIME = 30L * 60L;

    @Override
    protected String getAuthUrl() {
        return "/permission/web/wd/login/login";
    }

    @Override
    public ResponseEntity<?> execute(ConnectorProcessorParamDTO processorParamDTO) {
        ConnectorSecretKeyPo connectorSecretKey = processorParamDTO.getConnectorSecretKey();
        ConnectorEnvironmentAddressPo environmentAddress = processorParamDTO.getEnvironmentAddress();
        ConnectorApiPo connectorApi = processorParamDTO.getConnectorApi();
        // 校验鉴权参数是否齐全
        JSONObject authParam = checkAuthParam(connectorSecretKey, getApiAuthType().getDesc(), AUTH_KEYS);
        HttpHeaders cookieHeaders = getCookieHeaders(processorParamDTO, authParam);
        JSONObject params = processorParamDTO.getParams();
        if (!params.containsKey(HttpParamKind.HEAD.name())) {
            params.put(HttpParamKind.HEAD.name(), new JSONObject());
        }
        params.getJSONObject(HttpParamKind.HEAD.name()).put(HttpHeaders.COOKIE, cookieHeaders.get(HttpHeaders.COOKIE));
        // 构建RestTemplateExecuteParamDTO
        RestTemplateExecuteParamDTO paramDTO = new RestTemplateExecuteParamDTO();
        paramDTO.setUrl(buildRequestUrl(environmentAddress.getEnvironmentAddress(), connectorApi.getApiMethod(), params));
        HttpMethod httpMethod = HttpMethod.resolve(connectorApi.getReqMethod().name());
        paramDTO.setMethod(httpMethod);
        paramDTO.setRequestEntity(createHttpEntity(httpMethod, params));
        paramDTO.setUriVariables(new HashMap<>(16));
        paramDTO.setProcessorParamDTO(processorParamDTO);
        // 调用连接器API
        return executeRequest(getRetryCount(), paramDTO);
    }

    @Override
    public ConnectorApiAuthTypeEnum getApiAuthType() {
        return ConnectorApiAuthTypeEnum.WAKE_CLOUD;
    }

    /**
     * 重新生成Cookie
     * @param restTemplateExecuteParamDTO RestTemplate请求参数DTO
     */
    private void resetCookie(RestTemplateExecuteParamDTO restTemplateExecuteParamDTO) {
        // 清除Redis中的cookie
        ConnectorProcessorParamDTO processorParamDTO = restTemplateExecuteParamDTO.getProcessorParamDTO();
        String redisKey = buildWdCookieRedisKey(processorParamDTO);
        RedisUtil.getInstance().del(redisKey);
        // 重新获取cookie保存到Redis中
        JSONObject authParam = checkAuthParam(processorParamDTO.getConnectorSecretKey(), getApiAuthType().getDesc(), AUTH_KEYS);
        HttpHeaders cookieHeaders = getCookieHeaders(restTemplateExecuteParamDTO.getProcessorParamDTO(), authParam);
        // 覆盖Cookie值
        JSONObject params = processorParamDTO.getParams();
        params.getJSONObject(HttpParamKind.HEAD.name()).put(HttpHeaders.COOKIE, cookieHeaders.get(HttpHeaders.COOKIE));
        restTemplateExecuteParamDTO.setRequestEntity(createHttpEntity(restTemplateExecuteParamDTO.getMethod(), params));
    }

    /**
     * 获取Cookie请求头
     *
     * @param processorParamDTO 连接器API处理器封装参数
     * @param authParam         鉴权参数JSONObject
     * @return HttpHeaders
     */
    private HttpHeaders getCookieHeaders(ConnectorProcessorParamDTO processorParamDTO, JSONObject authParam) {
        HttpHeaders headers = new HttpHeaders();
        // 先查看Redis中是否存在Cookie，存在直接返回
        RedisUtil redisUtil = RedisUtil.getInstance();
        String redisKey = buildWdCookieRedisKey(processorParamDTO);
        List<String> cookies = redisUtil.getList(redisKey, String.class);
        if (CollectionUtil.isNotEmpty(cookies)) {
            headers.put(HttpHeaders.COOKIE, cookies);
            return headers;
        }
        // 调用惟客云登陆接口
        ConnectorEnvironmentAddressPo environmentAddress = processorParamDTO.getEnvironmentAddress();
        String loginUrl = environmentAddress.getEnvironmentAddress() + getAuthUrl();
        ResponseEntity<?> loginRespEntity = login(loginUrl, authParam);
        cookies = loginRespEntity.getHeaders().get(SET_COOKIE);
        if (CollectionUtil.isEmpty(cookies)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_param_set_cookie_not_found);
        }
        headers.put(HttpHeaders.COOKIE, cookies);
        // 调用惟客云选择租户接口
        String chooseTenantUrl = environmentAddress.getEnvironmentAddress() + CHOOSE_TENANT_URL;
        chooseTenant(chooseTenantUrl, authParam, headers);
        // 调用惟客云选择应用接口
        String chooseAppUrl = environmentAddress.getEnvironmentAddress() + CHOOSE_APP_URL;
        chooseApp(chooseAppUrl, authParam, headers);
        // 保存cookie到redis后返回
        redisUtil.set(redisKey, JSON.toJSONString(cookies), COOKIE_EXPIRATION_TIME);
        return headers;
    }

    /**
     * 登陆接口
     *
     * @param url       请求url
     * @param authParam 鉴权参数JSONObject
     * @return ResponseEntity
     */
    private ResponseEntity<?> login(String url, JSONObject authParam) {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put(USERNAME, authParam.getString(USERNAME));
        paramMap.put(PASSWORD, authParam.getString(PASSWORD));
        return postByFrom(url, null, paramMap);
    }

    /**
     * 选择租户
     *
     * @param url         请求url
     * @param authParam   鉴权参数JSONObject
     * @param httpHeaders HttpHeaders
     */
    private void chooseTenant(String url, JSONObject authParam, HttpHeaders httpHeaders) {
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put(TENANT_ID, authParam.get(TENANT_ID));
        postByFrom(url, httpHeaders, paramMap);
    }

    /**
     * 选择应用
     *
     * @param url         请求url
     * @param authParam   鉴权参数JSONObject
     * @param httpHeaders HttpHeaders
     */
    private void chooseApp(String url, JSONObject authParam, HttpHeaders httpHeaders) {
        Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put(APP_BU_ID, authParam.get(APP_BU_ID));
        postByFrom(url, httpHeaders, paramMap);
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
                if (jsonObject.containsKey(RESPONSE_CODE) && NO_LOGIN_CODE.equals(jsonObject.getInteger(RESPONSE_CODE))) {
                    resetCookie(restTemplateExecuteParamDTO);
                } else {
                    return responseEntity;
                }
                retryCount--;
            } catch (Exception e) {
                resetCookie(restTemplateExecuteParamDTO);
                log.error("request connector api fail, request url: {}", restTemplateExecuteParamDTO.getUrl());
                log.error(e.getMessage(), e);
                retryCount--;
            }
        }
        log.error("request connector api fail, request url: {}", restTemplateExecuteParamDTO.getUrl());
        throw new OpenException(OpenApiMsgCodeEnum.w_request_connector_api_fail);
    }

    /**
     * post请求，form表单提交
     *
     * @param url         请求URL
     * @param httpHeaders HttpHeaders
     * @param paramMap    请求头参数Map
     * @return ResponseEntity
     */
    private ResponseEntity<?> postByFrom(String url, HttpHeaders httpHeaders, Map<String, Object> paramMap) {
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                createHttpEntity(httpHeaders, paramMap),
                new ParameterizedTypeReference<String>() {
                },
                new HashMap<>(16)
        );
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        if (!SUCCESS_CODE.equals(jsonObject.getInteger(RESPONSE_CODE))) {
            OpenApiMsgCodeEnum codeEnum = OpenApiMsgCodeEnum.w_authentication_fail;
            throw new OpenException(codeEnum.getCode(), String.format(codeEnum.getDesc(), jsonObject.getString(RESPONSE_MSG), url));
        }
        return responseEntity;
    }

    /**
     * 构建HttpEntity
     *
     * @param httpHeaders HttpHeaders
     * @param paramMap    请求参数Map
     * @return HttpEntity
     */
    private HttpEntity<?> createHttpEntity(HttpHeaders httpHeaders, Map<String, Object> paramMap) {
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 构建表单参数MultiValueMap
        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        if (paramMap != null && !paramMap.isEmpty()) {
            paramMap.forEach(valueMap::add);
        }
        return new HttpEntity<>(valueMap, httpHeaders);
    }

    /**
     * 构建惟客云Cookie保存在Redis中的key
     *
     * @param processorParamDTO 连接器API处理器封装参数
     * @return Redis Key
     */
    private String buildWdCookieRedisKey(ConnectorProcessorParamDTO processorParamDTO) {
        ConnectorApiPo connectorApi = processorParamDTO.getConnectorApi();
        ConnectorSecretKeyPo connectorSecretKey = processorParamDTO.getConnectorSecretKey();
        return String.format(WD_COOKIE_REDIS_KEY_FORMAT, connectorApi.getLesseeId(), connectorSecretKey.getId());
    }

}
