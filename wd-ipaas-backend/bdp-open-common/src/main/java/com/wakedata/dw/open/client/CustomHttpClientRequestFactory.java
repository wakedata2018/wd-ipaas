package com.wakedata.dw.open.client;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

/**
 * @author luomeng
 * @date 2022/10/10 19:31
 */
public class CustomHttpClientRequestFactory extends HttpComponentsClientHttpRequestFactory {

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        RequestConfig config =HttpContextHolder.get();
        if(config!=null){
            HttpContext context = HttpClientContext.create();
            context.setAttribute(HttpClientContext.REQUEST_CONFIG,config);
            return context;
        }
        return super.createHttpContext(httpMethod,uri);
    }
}
