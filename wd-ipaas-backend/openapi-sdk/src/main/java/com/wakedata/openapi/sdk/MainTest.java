package com.wakedata.openapi.sdk;

import cn.hutool.http.HttpResponse;
import com.wakedata.openapi.sdk.accesstoken.AccessToken;
import com.wakedata.openapi.sdk.accesstoken.AccessTokenUtil;
import com.wakedata.openapi.sdk.generator.apiexample.ApiExec;
import com.wakedata.openapi.sdk.generator.apiexample.RequestBody;
import com.wakedata.openapi.sdk.generator.apiexample.RequestParam;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 方法测试
 * @author luomeng
 * @date 2022/8/23 19:20
 */
@Slf4j
public class MainTest {

    public static void main(String[] args) {
        String appSecret = "635fb3faa9bf4700a1aaa934f1c4e4e0";
        String appKey = "2d937262ec1a4565a98e513570730733";

        //获取token
        AccessToken accessToken = AccessTokenUtil.getAccessToken(appKey, appSecret);
        log.info("get accessToken ：{}",accessToken);
////        //刷新token
////        accessToken = AccessTokenUtil.refreshToken(appKey, appSecret,accessToken.getRefreshToken());
////        log.info("refresh accessToken :{}",accessToken);
////        //校验token
////        Boolean check = AccessTokenUtil.checkToken(appKey, appSecret,accessToken.getAccessToken());
////        log.info("check accessToken result :{}",check);
//        //调用api
//        OpenApiExample openApiExample = new OpenApiExample();
//        openApiExample.example1(accessToken.getAccessToken(), appSecret);
//        openApiExample.example2(accessToken.getAccessToken(),appSecret);

        accessToken.setAccessToken("testAtA2AD75A0CA98DAB7EB32823668D83926");
        RequestParam requestParam = new RequestParam();
        requestParam.setId("123a");
        requestParam.setIt(1);
        requestParam.setRes(Arrays.asList(10,12));
        RequestBody requestBody = null;
        HttpResponse execute = ApiExec.execute(accessToken.getAccessToken(), appSecret, null, requestParam, requestBody);
        log.info("execute:{}",execute);

    }

}
