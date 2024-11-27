package com.wakedata.dw.lowcode.config;

import com.wakedata.dw.lowcode.handler.FetchAppIdInterceptor;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wanghu@wakedata.com
 * @title 低代码获取应用id配置
 * @date 2021/12/2
 * @since v1.0.0
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Resource
    private FetchAppIdInterceptor fetchAppIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(fetchAppIdInterceptor).addPathPatterns("/low/code/**");
    }

}
