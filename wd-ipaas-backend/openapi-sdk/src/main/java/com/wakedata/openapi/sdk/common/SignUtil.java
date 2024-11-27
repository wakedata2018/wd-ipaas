package com.wakedata.openapi.sdk.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.util.Arrays;
import java.util.Map;

/**
 * 签名及验证
 * @author luomeng
 * @date 2022/8/23 19:40
 */
public class SignUtil {

    /**
     * 签名
     * @param params http url请求参数
     * @param postData http post的json数据
     * @param secret 应用的appSecret
     * @return
     */
    public static String sign(Map<String, String> params, String postData, String secret){
        // 第一步：检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            if(OpenApiConstant.REQ_PARAM_SIGN.equals(key)){
                continue;
            }
            String value = params.get(key);
            if (StrUtil.isAllNotEmpty(key, value)) {
                query.append(key).append(value);
            }
        }
        if(StrUtil.isNotEmpty(postData)){
            query.append(postData);
        }
        // 第三步：使用MD5加密
        query.append(secret);
        return DigestUtil.md5Hex(query.toString());
    }
}
