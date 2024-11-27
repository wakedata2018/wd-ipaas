package com.wakedata.dw.open.config;

import com.wakedata.dw.open.liteflow.DataAssetApiLiteflowService;
import com.wakedata.dw.open.liteflow.LiteflowApiInvokeService;
import com.wakedata.dw.open.liteflow.OperatorGraphLiteflowService;
import com.wakedata.dw.open.service.DataAssetApiLiteflowServiceImpl;
import com.wakedata.dw.open.service.LiteflowApiInvokeServiceImpl;
import com.wakedata.dw.open.service.OperatorGraphLiteflowServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * liteflow接口实现，如去除liteflow模块则需要使用空实现，避免启动及调用报错
 * @author luomeng
 * @date 2022/12/15 12:46
 */
@Configuration
public class LiteflowConfiguration {

    //liteflow实现

    @Bean
    public DataAssetApiLiteflowService dataAssetApiLiteflowService(){
        return new DataAssetApiLiteflowServiceImpl();
    }

    @Bean
    public LiteflowApiInvokeService liteflowApiInvokeService() {
        return new LiteflowApiInvokeServiceImpl();
    }

    @Bean
    public OperatorGraphLiteflowService operatorGraphLiteflowService() {
        return new OperatorGraphLiteflowServiceImpl();
    }



    //空实现
//    @Bean
//    public DataAssetApiLiteflowService dataAssetApiLiteflowService(){
//        return new DataAssetApiLiteflowServiceEmptyImpl();
//    }
//
//    @Bean
//    public LiteflowApiInvokeService liteflowApiInvokeService() {
//        return new LiteflowApiInvokeServiceEmptyImpl();
//    }
//
//    @Bean
//    public OperatorGraphLiteflowService operatorGraphLiteflowService() {
//        return new OperatorGraphLiteflowServiceEmptyImpl();
//    }
}
