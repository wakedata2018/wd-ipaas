package com.wakedata.dw.open.service.impl.dremio;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * @author ZhangXueJun
 * @title DremioAuthService
 * @date 2021/1/27 14:30
 * @projectName dw-open
 * @description
 */
public class DremioAuthService {

    private static final String login = "/apiv2/login";
    private String dremioToken;

    private RestTemplate restTemplate;
    private DremioRestConfig dremioRestConfig = new DremioRestConfig();

    public static void main(String[] args) {
        DremioAuthService dremioAuthService = new DremioAuthService();
        JSONObject map = dremioAuthService.restTemplate().exchange(
                "http://172.31.3.91:9047/api/v3/user/by-name/dremio",
                HttpMethod.GET,
                new HttpEntity<String>(dremioAuthService.getAuthHeaders()),
                new ParameterizedTypeReference<JSONObject>() {
                }
                ).getBody();

        System.out.println(map.toJSONString());

    }


    public RestTemplate restTemplate() {
        if (restTemplate != null) {
            return restTemplate;
        }
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient()));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 401) {
                    dremioToken = null;
                    return;
                }
                super.handleError(response);
            }
        });
        this.restTemplate = restTemplate;
        return restTemplate;
    }

    private HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(dremioRestConfig.getMaxSize());
        connectionManager.setDefaultMaxPerRoute(dremioRestConfig.getMaxSize());
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(dremioRestConfig.getResponseTimeout())
                .setConnectionRequestTimeout(dremioRestConfig.getRequestTimeout())
                .setConnectTimeout(dremioRestConfig.getConnectionTimeout())
                .build();

        return HttpClientBuilder.create()
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))
                .setConnectionReuseStrategy(new DefaultClientConnectionReuseStrategy())
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setConnectionManagerShared(true)
                .setDefaultRequestConfig(config)
                .setConnectionManager(connectionManager)
                .build();

    }

    private HttpHeaders getAuthHeaders() {
        if (StringUtils.isBlank(dremioToken)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userName", "dremio");
            jsonObject.put("password", "dremio123");
            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toJSONString(), httpHeaders);
            JSONObject results = restTemplate.exchange("http://172.31.3.91:9047/apiv2/login",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<JSONObject>() {
                    }).getBody();

            System.out.println(results);
            if (MapUtils.isNotEmpty(results)) {
                dremioToken = results.getString("token");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("_dremio" + dremioToken));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        return headers;
    }
}