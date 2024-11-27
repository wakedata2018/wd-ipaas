package com.wakedata.dw.open.authentication;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 连接器API处理器抽象类
 *
 * @author wujunqiang
 * @since 2022/11/18 14:28
 */
public abstract class BaseConnectorApiProcessor implements ConnectorApiProcessor {

    /**
     * 默认调用失败重试次数
     */
    private static final int DEFAULT_RETRY_COUNT = 3;

    @Resource(name = DwOpenConstant.REST_TEMPLATE_CUSTOMER)
    protected RestTemplate restTemplate;

    /**
     * 缺失鉴权参数错误枚举
     */
    protected static final OpenApiMsgCodeEnum MISSING_AUTH_PARAM = OpenApiMsgCodeEnum.w_missing_authentication_parameters;

    /**
     * 获取鉴权URL
     *
     * @return 鉴权URL
     */
    protected abstract String getAuthUrl();

    /**
     * 构建请求URL
     *
     * @param environmentAddress 平台环境URL
     * @param apiUrl             接口URL
     * @param params             API请求参数
     * @return 请求URL
     */
    protected String buildRequestUrl(String environmentAddress, String apiUrl, JSONObject params) {
        StringBuilder sb = new StringBuilder();
        sb.append(environmentAddress).append(apiUrl);
        JSONObject queryParams = params.getJSONObject(HttpParamKind.QUERY.name());
        if (queryParams == null || queryParams.size() == 0) {
            return sb.toString();
        }
        sb.append("?");
        boolean isFirstParam = true;
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            if (isFirstParam) {
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                isFirstParam = false;
            } else {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return sb.toString();
    }

    /**
     * 构建HttpEntity
     *
     * @param method 请求类型枚举类
     * @param params API请求参数
     * @return HttpEntity
     */
    protected HttpEntity<?> createHttpEntity(HttpMethod method, JSONObject params) {
        HttpHeaders httpHeaders = RequestParamUtils.getHeaders(params.getJSONObject(HttpParamKind.HEAD.name()));
        if (HttpMethod.POST == method) {
            return new HttpEntity<>(params.getJSONObject(HttpParamKind.BODY.name()) != null ? params.getJSONObject(HttpParamKind.BODY.name()) : "", httpHeaders);
        }
        return new HttpEntity<>(httpHeaders);
    }

    /**
     * 校验鉴权参数
     *
     * @param connectorSecretKey 连接器平台密钥
     * @param connectorName      平台名称
     * @param authKeys           鉴权参数集合
     * @return 鉴权参数JSONObject
     */
    protected JSONObject checkAuthParam(ConnectorSecretKeyPo connectorSecretKey, String connectorName, List<String> authKeys) {
        JSONObject authParam = JSONObject.parseObject(connectorSecretKey.getParamsJson());
        for (String authKey : authKeys) {
            if (!authParam.containsKey(authKey)) {
                throw new OpenException(MISSING_AUTH_PARAM.getCode(), String.format(MISSING_AUTH_PARAM.getDesc(), authKey, connectorName));
            }
        }
        return authParam;
    }

    /**
     * 请求第三方API的失败重试次数，默认3次
     *
     * @return 失败重试次数
     */
    protected int getRetryCount() {
        return DEFAULT_RETRY_COUNT;
    }

}
