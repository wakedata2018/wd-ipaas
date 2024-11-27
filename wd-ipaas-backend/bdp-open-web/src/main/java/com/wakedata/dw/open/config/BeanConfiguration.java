package com.wakedata.dw.open.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wakedata.dw.open.interceptor.IpaasLoginCheckInterceptor;
import com.wakedata.dw.open.interceptor.PermissionLoginCheckInterceptor;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;


/**
 * @ClassName BeanConfiguration
 * @Description
 * @Author yyf
 * @Date 2019/3/12 16:05
 * @Version 1.0
 * @title BeanConfiguration
 **/
@Configuration
public class BeanConfiguration implements WebMvcConfigurer {


    /**
     * 后台接口前缀
     */
    @Value("${spring.mvc.backend.prefix}")
    private String backendUrlPrefix;

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(0, stringHttpMessageConverter());
//        converters.add(1, fastJsonHttpMessageConverter());
//    }

//    @PostConstruct
//    public void registerModule(){
////        //自定义算子反序列化实现
//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(Operator.class,new OperatorJsonDeserializer());
//        JsonUtil.registerModules(module);
//    }

    private StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter();
    }

    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_PLAIN));
        return fastJsonHttpMessageConverter;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
    /**
     * 添加登录拦截过滤器，license拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //ipaas登录校验
        InterceptorRegistration registration = registry.addInterceptor(new IpaasLoginCheckInterceptor());
        registration.addPathPatterns(backendUrlPrefix + "/business/**", backendUrlPrefix + "/action/**", backendUrlPrefix + "/test/**", backendUrlPrefix + "/api/test/**", backendUrlPrefix + "/document/**");
        registration.excludePathPatterns(backendUrlPrefix + "/manage/**");

        //权限后台使用接口校验
        InterceptorRegistration permiRegistration = registry.addInterceptor(new PermissionLoginCheckInterceptor());
        permiRegistration.addPathPatterns(backendUrlPrefix + "/manage/account/**");
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "false");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        //配置postgresql数据库的方言支持Oracle,Mysql,MariaDB,SQLite,Hsqldb,PostgreSQL六种数据库
        properties.setProperty("dialect", "postgresql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @LoadBalanced
    @Bean(name = "lbRestTemplate")
    public RestTemplate lbRestTemplate() {
        return new RestTemplate();
    }


    @Bean
    public SessionLocaleResolver sessionLocaleResolver() {
        Locale.setDefault(new Locale("zh", "cn"));
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.getDefault());
        return sessionLocaleResolver;
    }


    @Bean
    public Validator validator() {
        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ResourceBundleMessageInterpolator(
                        new PlatformResourceBundleLocator("i18n/ValidationMessages")))
                .buildValidatorFactory()
                .getValidator();
        return validator;
    }
}
