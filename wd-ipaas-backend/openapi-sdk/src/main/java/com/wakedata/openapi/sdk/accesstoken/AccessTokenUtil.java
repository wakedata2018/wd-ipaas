package com.wakedata.openapi.sdk.accesstoken;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.openapi.sdk.common.OpenApiConstant;
import com.wakedata.openapi.sdk.common.OpenApiUrl;
import com.wakedata.openapi.sdk.common.SignUtil;
import com.wakedata.openapi.sdk.exception.OpenApiException;
import com.wakedata.openapi.sdk.result.OpenApiResultDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 提供token获取工具类
 * @author luomeng
 * @date 2022/8/24 10:37
 */
@Slf4j
public class AccessTokenUtil {

    private static final String STOP_WATCH_PREFIX = "open:api:accessToken:";
    private static final String APP_KEY = "appKey";

    /**
     * 获取token
     * @param appKey 应用的appKey
     * @param appSecret 应用的appSecret
     * @return
     * @throws OpenApiException
     */
    public static AccessToken getAccessToken(String appKey,String appSecret) throws OpenApiException{
        StopWatch stopWatch = StopWatch.create(STOP_WATCH_PREFIX + IdUtil.fastSimpleUUID());
        stopWatch.start();
        String response = get(appKey,appSecret,stopWatch, null, OpenApiUrl.AccessToken.GET_ACCESS_TOKEN);
        OpenApiResultDTO resultDTO = JSONUtil.toBean(response,OpenApiResultDTO.class);
        if(!resultDTO.getSuccess()){
            log.error("get access token fail：code={},msg={}",resultDTO.getCode(),resultDTO.getMsg());
            throw new OpenApiException(resultDTO.getCode(),resultDTO.getMsg());
        }
        return JSONUtil.toBean(JSONUtil.toJsonStr(resultDTO.getData()),AccessToken.class);
    }


    /**
     * 刷新token
     * @param appKey 应用的appKey
     * @param appSecret 应用的appSecret
     * @param refreshToken 刷新token令牌
     * @return
     * @throws OpenApiException
     */
    public static AccessToken refreshToken(String appKey,String appSecret, String refreshToken)throws OpenApiException{
        StopWatch stopWatch = StopWatch.create(STOP_WATCH_PREFIX + IdUtil.fastSimpleUUID());
        stopWatch.start();
        Map<String,String> param = new HashMap<>();
        param.put("refreshToken", refreshToken);
        String response = get(appKey,appSecret,stopWatch, param, OpenApiUrl.AccessToken.REFRESH_TOKEN);
        OpenApiResultDTO resultDTO = JSONUtil.toBean(response,OpenApiResultDTO.class);
        if(!resultDTO.getSuccess()){
            log.error("refresh access token fail：code={},msg={}",resultDTO.getCode(),resultDTO.getMsg());
            throw new OpenApiException(resultDTO.getCode(),resultDTO.getMsg());
        }
        AccessToken accessToken = JSONUtil.toBean(JSONUtil.toJsonStr(resultDTO.getData()),AccessToken.class);
        accessToken.setRefreshToken(refreshToken);
        return accessToken;
    }

    /**
     * 校验token是否有效
     * @param appKey 应用的appKey
     * @param appSecret 应用的appSecret
     * @param accessToken token令牌
     * @return
     * @throws OpenApiException
     */
    public static Boolean checkToken(String appKey,String appSecret, String accessToken) throws OpenApiException{
        StopWatch stopWatch = StopWatch.create(STOP_WATCH_PREFIX + IdUtil.fastSimpleUUID());
        stopWatch.start();
        Map<String,String> param = new HashMap<>();
        param.put(OpenApiConstant.REQ_PARAM_ACCESS_TOKEN, accessToken);
        String response = get(appKey,appSecret,stopWatch, param, OpenApiUrl.AccessToken.CHECK_TOKEN);
        OpenApiResultDTO resultDTO = JSONUtil.toBean(response,OpenApiResultDTO.class);
        if(!resultDTO.getSuccess()){
            log.error("check access token fail：code={},msg={}",resultDTO.getCode(),resultDTO.getMsg());
            throw new OpenApiException(resultDTO.getCode(),resultDTO.getMsg());
        }
        return (Boolean) resultDTO.getData();
    }

    /**
     * 请求
     * @param appKey 应用的appKey
     * @param appSecret 应用的appSecret
     * @param stopWatch 计时
     * @param param 请求参数
     * @param apiUrl 请求Url
     * @return
     */
    private static String get(String appKey,String appSecret,StopWatch stopWatch, Map<String, String> param, String apiUrl) {
        if(MapUtil.isEmpty(param)){
            param = new HashMap<>();
        }
        param.put(APP_KEY, appKey);
        param.put(OpenApiConstant.REQ_PARAM_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        param.put(OpenApiConstant.REQ_PARAM_SIGN, SignUtil.sign(param,null,appSecret));

        String api = String.join(OpenApiConstant.URL_JOIN_QUESTION, apiUrl, HttpUtil.toParams(param));
        String response = HttpUtil.get(api, OpenApiConstant.TIMEOUT);
        stopWatch.stop();
        log.info("call the get api to request the result：\nrequest url ->{}\nresponse result ->{}，\nelapsed time ->{}", api, response, stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return response;
    }


}
