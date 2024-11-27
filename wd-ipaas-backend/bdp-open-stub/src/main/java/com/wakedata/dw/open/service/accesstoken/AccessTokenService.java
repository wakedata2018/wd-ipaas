package com.wakedata.dw.open.service.accesstoken;

import com.wakedata.dw.open.accesstoken.AccessToken;

/**
 * token获取接口
 * @author luomeng
 * @date 2022/8/6 14:30
 */
public interface AccessTokenService {

    /**
     * 生成临时accessToken
     * @param timestamp 时间戳
     * @return AccessToken
     */
    AccessToken generate(String timestamp);

    /**
     * 生成accessToken
     * @param appKey 应用appKey
     * @param timestamp 时间戳
     * @param sign 签名
     * @return
     */
    AccessToken generate(String appKey, String timestamp, String sign);

    /**
     * 刷新token
     * @param appKey 应用appKey
     * @param refreshToken 刷新token
     * @param timestamp shi
     * @param sign
     * @return
     */
    AccessToken refresh(String appKey, String refreshToken, String timestamp, String sign);

    /**
     * 校验token是否有效
     * @param appKey 应用appKey
     * @param accessToken 生成的token
     * @param timestamp 时间戳
     * @param sign 签名
     * @return
     */
    Boolean check(String appKey, String accessToken, String timestamp, String sign);

    /**
     * 清除token
     * @param lesseeId 租户id
     * @param dataAccessAppId 应用id
     * @return
     */
    Boolean cleanToken(Long lesseeId,Integer dataAccessAppId);

    /**
     * 根据应用key获取临时测试accessToken
     *
     * @param appKey 应用key
     * @param timeStamp 时间戳
     * @return AccessToken
     */
    AccessToken getAppTokenByAppKey(String appKey,String timeStamp);
}
