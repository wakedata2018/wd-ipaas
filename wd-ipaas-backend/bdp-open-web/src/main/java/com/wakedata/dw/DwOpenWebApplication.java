package com.wakedata.dw;

import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.common.mq.annotation.EnableMessageCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author yiyufeng
 * @title AuthorityMineReq
 * @projectName bdp-open
 * @date
 * @description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableCaching
@ComponentScan(basePackages = {"com.wakedata.dw", "com.wakedata.wd.app"})
@tk.mybatis.spring.annotation.MapperScan(basePackages = {"com.wakedata.dw.open.mapper", "com.wakedata.dw.lowcode.mapper"})
@ServletComponentScan
@EnableFeignClients(basePackages = {"com.wakedata.wd.app"})
@EnableAsync
@EnableMessageCenter
public class DwOpenWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(DwOpenWebApplication.class, args);
    }

    @Bean("multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setResolveLazily(true);
        multipartResolver.setMaxInMemorySize(1048576);
        multipartResolver.setMaxUploadSize(60914560);
        multipartResolver.setDefaultEncoding("UTF-8");
        return multipartResolver;
    }

    @Bean
    public GlobalApplicationContext globalApplicationContext() {
        return new GlobalApplicationContext();
    }

}
