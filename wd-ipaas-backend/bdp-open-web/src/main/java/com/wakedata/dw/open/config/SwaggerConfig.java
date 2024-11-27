package com.wakedata.dw.open.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author yiyufeng
 * @title SwaggerConfig
 * @projectName bdp-open
 * @date 2019/8/16 10:22
 * @descprition
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${sys.env}")
    private String env;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).enable(ifTestEnv()).apiInfo(apiInfo()).pathMapping("/").select()
                .apis(RequestHandlerSelectors.basePackage("com.wakedata.dw")).paths(PathSelectors.any()).build();
    }

    private boolean ifTestEnv() {
        if (StringUtils.isNotBlank(env) && (env.toUpperCase().contains("IDC"))) {
            return false;
        }
        return true;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Ipaas平台接口文档").description("Ipaas平台接口文档")
                .termsOfServiceUrl("http://open.wakedata.com/").version("1.0").build();
    }
}