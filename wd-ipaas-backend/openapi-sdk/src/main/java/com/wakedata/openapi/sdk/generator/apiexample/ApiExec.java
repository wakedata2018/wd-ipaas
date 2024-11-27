package com.wakedata.openapi.sdk.generator.apiexample;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.wakedata.openapi.sdk.common.OpenApiRequestUtil;
import com.wakedata.openapi.sdk.common.OpenApiUrl;
import com.wakedata.openapi.sdk.common.TypeConvertUtil;

import java.util.Map;

/**
 *  api执行
 * @author luomeng
 * @date 2022/10/26 15:18
 */
public class ApiExec {

    /**
     * 执行api
     * @param accessToken token
     * @param appSecret 应用密钥
     * @param requestHead api请求head参数
     * @param requestParam api请求query参数
     * @param requestBody api请求body参数
     * @return
     */
    public static HttpResponse execute(String accessToken,String appSecret,RequestHead requestHead,RequestParam requestParam,RequestBody requestBody){
        //设置请求头
        Map<String,String> head = TypeConvertUtil.convert(requestHead);
        //设置请求参数
        Map<String,String> param = TypeConvertUtil.convert(requestParam);
        //设置请求体
        String body = ObjectUtil.isNull(requestBody) ? null : JSONUtil.toJsonStr(requestBody);
        String api = OpenApiUrl.REQUEST_HOST + "/dw/open/api/org/wd-organization/rpc/attr.page.query";
        return OpenApiRequestUtil.post(accessToken, appSecret, String.valueOf(System.currentTimeMillis()), api, head, param,body);
    }


}
