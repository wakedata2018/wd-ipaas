package com.wakedata.openapi.sdk;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wakedata.openapi.sdk.common.OpenApiConstant;
import com.wakedata.openapi.sdk.common.OpenApiUrl;
import com.wakedata.openapi.sdk.common.OpenApiRequestUtil;
import com.wakedata.openapi.sdk.common.SignUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * api请求示例
 *
 * @author luomeng
 * @date 2022/8/25 16:09
 */
@Slf4j
public class OpenApiExample {


    /**
     * 示例1
     * @param accessToken
     * @param appSecret
     */
    public void example1(String accessToken, String appSecret) {

        //token
        String timeStamp = String.valueOf(System.currentTimeMillis());

        Map<String, String> param = new HashMap<>();
        param.put("pageNo", "1");
        param.put("pageSize", "10");
        param.put("id","1");
        String api = OpenApiUrl.REQUEST_HOST + "/dw/open/api/org/test3";
        HttpResponse result = OpenApiRequestUtil.get(accessToken, appSecret, timeStamp, api, null, param);
        Map<String, List<String>> respHeaders = result.headers();
        String respBody = result.body();
        log.info("example1 result-> head：{}，body :{}", respHeaders,respBody);

    }

    /**
     * 示例2
     * @param accessToken
     * @param appSecret
     */
    public void example2(String accessToken, String appSecret) {

        //token
        String timeStamp = String.valueOf(System.currentTimeMillis());

        Map<String, String> param = new HashMap<>();
        JSONObject body = new JSONObject();
        body.set("tenantId", "657");
        String bodyStr = JSONUtil.toJsonStr(body);
        param.put(OpenApiConstant.REQ_PARAM_SIGN, SignUtil.sign(param, bodyStr, appSecret));
        String api = OpenApiUrl.REQUEST_HOST + "/dw/open/api/org/wd-organization/rpc/attr.page.query";
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        HttpResponse result = OpenApiRequestUtil.post(accessToken, appSecret, timeStamp, api, headers, param, bodyStr);
        Map<String, List<String>> respHeaders = result.headers();
        String respBody = result.body();
        log.info("example2 result-> head：{}，body :{}", respHeaders,respBody);

    }

}
