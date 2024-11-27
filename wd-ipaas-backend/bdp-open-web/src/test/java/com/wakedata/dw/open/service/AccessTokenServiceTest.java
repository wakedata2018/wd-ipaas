package com.wakedata.dw.open.service;

import com.wakedata.dw.DwOpenWebApplication;
import com.wakedata.dw.open.accesstoken.AccessToken;
import com.wakedata.dw.open.accesstoken.SignUtil;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luomeng
 * @Description token测试
 * @createTime 2022-08-07 09:57:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DwOpenWebApplication.class)
@Slf4j
public class AccessTokenServiceTest {


    @Resource
    private AccessTokenService accessTokenService;


    @Test
    public void generate(){

        String appKey = "81b0b0a8d99f4e198136680c59e0c5c7";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String appSecret = "4efc6059c03544f186770571182b9a79";
        Map<String,String> param = new HashMap<>();
        param.put("timestamp",timestamp);
        param.put("appKey",appKey);
        String sign = SignUtil.signApiRequest(param,null,appSecret);
        AccessToken accessToken = accessTokenService.generate(appKey, timestamp, sign);
        log.info("accessToken = {}",accessToken);
        // accessToken = AccessToken(accessToken=at6BE79722D5D41B94EE823CE6B19E29C8, refreshToken=rt6BE79722D5D41B94EE823CE6B19E29C8, expireIn=7200)
    }

    @Test
    public void refresh(){

        String appKey = "81b0b0a8d99f4e198136680c59e0c5c7";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String refreshToken = "rt6BE79722D5D41B94EE823CE6B19E29C8";
        String appSecret = "4efc6059c03544f186770571182b9a79";
        Map<String,String> param = new HashMap<>();
        param.put("timestamp",timestamp);
        param.put("appKey",appKey);
        param.put("refreshToken",refreshToken);
        String sign = SignUtil.signApiRequest(param,null,appSecret);

        AccessToken accessToken = accessTokenService.refresh(appKey,refreshToken,timestamp,sign);
        log.info("refresh token result = {}",accessToken);


    }

    @Test
    public void check(){
        String appKey = "81b0b0a8d99f4e198136680c59e0c5c7";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String accessToken = "at6BE79722D5D41B94EE823CE6B19E29C8";
        String appSecret = "4efc6059c03544f186770571182b9a79";
        Map<String,String> param = new HashMap<>();
        param.put("timestamp",timestamp);
        param.put("appKey",appKey);
        param.put("accessToken",accessToken);
        String sign = SignUtil.signApiRequest(param,null,appSecret);

        Boolean result = accessTokenService.check(appKey,accessToken,timestamp,sign);
        log.info("check access token result = {}",result);
    }

}
