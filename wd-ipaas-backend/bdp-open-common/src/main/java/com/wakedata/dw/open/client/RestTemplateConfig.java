package com.wakedata.dw.open.client;

import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;

/**
 * restTemplate 配置
 *
 * @author luomeng
 * @date 2022/10/10 19:15
 */
@Configuration
public class RestTemplateConfig {

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;


    @Bean(name = DwOpenConstant.REST_TEMPLATE_CUSTOMER)
    RestTemplate template(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template = restTemplateBuilder
                //单位ms, 设置连接时间，避免线程过多被阻塞挂起
                .setConnectTimeout(Duration.ofSeconds(dwOpenCommonConfig.getConnectTimeout()))
                .setReadTimeout(Duration.ofSeconds(dwOpenCommonConfig.getReadTimeout()))
                .requestFactory(() -> {
                    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new CustomHttpClientRequestFactory();
                    // 连接池
                    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
                    //设置HTTP连接管理器
                    PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
                    poolingConnectionManager.setMaxTotal(dwOpenCommonConfig.getMaxTotal()); // 连接池最大连接数
                    poolingConnectionManager.setDefaultMaxPerRoute(dwOpenCommonConfig.getMaxPerRoute()); // 每个主机的并发
                    httpClientBuilder.setConnectionManager(poolingConnectionManager);
                    // 设置连接池
                    clientHttpRequestFactory.setHttpClient(httpClientBuilder.build());
                    return clientHttpRequestFactory;
                })
                .build();
        //设置编码
        List<HttpMessageConverter<?>> converterList = template.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = converterList.iterator();
        while (iterator.hasNext()) {
            if (StringHttpMessageConverter.class == iterator.next().getClass()) {
                iterator.remove();
            }
        }
        converterList.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        template.setMessageConverters(converterList);
        return template;
    }

}
