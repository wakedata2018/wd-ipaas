package com.wakedata.openapi.sdk.common;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 调用api工具类
 *
 * @author luomeng
 * @date 2022/8/26 12:02
 */
@Slf4j
public class OpenApiRequestUtil {


    private static final String STOP_WATCH_PREFIX = "open:api:";

    /**
     * 调用api (get请求)
     *
     * @param accessToken token
     * @param appSecret   密钥
     * @param timestamp   时间戳
     * @param apiUrl      接口地址
     * @param headers     请求头
     * @param params      请求参数
     * @return
     */
    public static HttpResponse get(String accessToken
            , String appSecret
            , String timestamp
            , String apiUrl
            , Map<String, String> headers
            , Map<String, String> params) {
        StopWatch stopWatch = StopWatch.create(STOP_WATCH_PREFIX + IdUtil.fastSimpleUUID());
        stopWatch.start();
        params = addCommonParam(accessToken, appSecret, timestamp, params, null);
        String api = String.join(OpenApiConstant.URL_JOIN_QUESTION, apiUrl, HttpUtil.toParams(params));
        HttpResponse response = HttpRequest.get(api)
                .headerMap(headers, true)
                .timeout(OpenApiConstant.TIMEOUT)
                .execute();
        stopWatch.stop();
        log.info("call the get api to request the result：\nrequest url ->{}\nresponse result ->{}\nelapsed time ->{}", api, response, stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return response;
    }


    /**
     * 调用api (post请求)
     *
     * @param accessToken token
     * @param appSecret   密钥
     * @param timestamp   时间戳
     * @param apiUrl      接口地址
     * @param headers     请求头
     * @param params      请求参数
     * @param body        请求体（json串）
     * @return
     */
    public static HttpResponse post(String accessToken
            , String appSecret
            , String timestamp
            , String apiUrl
            , Map<String, String> headers
            , Map<String, String> params
            , String body) {
        StopWatch stopWatch = StopWatch.create(STOP_WATCH_PREFIX + IdUtil.fastSimpleUUID());
        stopWatch.start();
        params = addCommonParam(accessToken, appSecret, timestamp, params, body);
        String api = String.join(OpenApiConstant.URL_JOIN_QUESTION, apiUrl, HttpUtil.toParams(params));
        //post请求未指定请求头默认使用json
        headers = setDefaultContentType(headers);
        HttpResponse response = HttpRequest.post(api)
                .headerMap(headers, true)
                .body(body)
                .timeout(OpenApiConstant.TIMEOUT)
                .execute();
        stopWatch.stop();
        log.info("call the post api to request the result：\nrequest url ->{}\nresponse result ->{}\nelapsed time ->{}", api, response, stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return response;
    }

    /**
     * 设置默认请求头
     * @param headers
     * @return
     */
    private static Map<String, String> setDefaultContentType(Map<String, String> headers) {
        if(MapUtil.isEmpty(headers)){
            headers = new HashMap<>();
        }
        if(StrUtil.isEmpty(headers.get(Header.CONTENT_TYPE.getValue()))){
            headers.put(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        }
        return headers;
    }


    /**
     * 添加公共参数
     *
     * @param accessToken token
     * @param appSecret   密钥（用于生成sign）
     * @param timestamp   时间戳
     * @param params      query参数
     * @param body        body参数(json串)
     */
    private static Map<String,String> addCommonParam(String accessToken, String appSecret, String timestamp, Map<String, String> params, String body) {
        if (MapUtil.isEmpty(params)) {
            params = new HashMap<>();
        }
        params.put(OpenApiConstant.REQ_PARAM_ACCESS_TOKEN, accessToken);
        params.put(OpenApiConstant.REQ_PARAM_TIMESTAMP, timestamp);
        params.put(OpenApiConstant.REQ_PARAM_SIGN, SignUtil.sign(params, body, appSecret));
        return params;
    }


}
