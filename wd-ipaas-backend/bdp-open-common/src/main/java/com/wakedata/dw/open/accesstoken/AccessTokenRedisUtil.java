package com.wakedata.dw.open.accesstoken;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.redis.util.RedisUtil;

/**
 * AccessToken缓存工具类
 * @author luomeng
 * @date 2022/8/6 16:34
 */
public class AccessTokenRedisUtil {

    /**
     * 缓存accessTokenByappKey
     */
    private static final String REDIS_ACCESS_TOKEN_BY_APP_KEY = "ipaas:accesstoken:appkey:%s";

    /**
     * 缓存accessTokenBytoken
     */
    private static final String REDIS_ACCESS_TOKEN_BY_TOKEN = "ipaas:accesstoken:token:%s";

    /**
     * 缓存appInfoBytoken
     */
    private static final String REDIS_APP_INFO_BY_TOKEN = "ipaas:appinfo:token:%s";


    /**
     * 根据appKey获取accessToken
     * @param appKey
     * @return
     */
    public static AccessToken getAccessTokenByAppKey(String appKey){
        return getAccessToken(String.format(REDIS_ACCESS_TOKEN_BY_APP_KEY,appKey));
    }

    /**
     * 根据accessToken获取AccessToken
     * @param accessToken
     * @return
     */
    public static AccessToken getAccessTokenByToken(String accessToken){
        return getAccessToken(String.format(REDIS_ACCESS_TOKEN_BY_TOKEN,accessToken));
    }

    /**
     * 根据token获取应用信息
     * @param accessToken
     * @return
     */
    public static AppAccessInfo getAppInfoByToken(String accessToken){
        String result = RedisUtil.getInstance().get(String.format(REDIS_APP_INFO_BY_TOKEN,accessToken));
        if(ObjectUtil.isNotEmpty(result)){
            return JSONUtil.toBean(result,AppAccessInfo.class);
        }
        return null;
    }

    /**
     * 缓存临时accessToken
     *
     * @param accessToken   AccessToken
     * @param appAccessInfo AppAccessInfo
     */
    public static void saveTestAccessToken(AccessToken accessToken, AppAccessInfo appAccessInfo) {
        String token = accessToken.getAccessToken();
        String tokenJson = JSONUtil.toJsonStr(accessToken);
        RedisUtil.getInstance().set(String.format(REDIS_ACCESS_TOKEN_BY_TOKEN,token),tokenJson,accessToken.getExpireIn());
        RedisUtil.getInstance().set(String.format(REDIS_APP_INFO_BY_TOKEN,token),JSONUtil.toJsonStr(appAccessInfo),accessToken.getExpireIn());
    }

    /**
     * 缓存accessToken
     * @param accessToken
     * @param appKey
     */
    public static void saveAccessToken(AccessToken accessToken,String appKey,AppAccessInfo appAccessInfo){
        String token = accessToken.getAccessToken();
        String tokenJson = JSONUtil.toJsonStr(accessToken);
        RedisUtil.getInstance().set(String.format(REDIS_ACCESS_TOKEN_BY_APP_KEY,appKey),tokenJson,accessToken.getExpireIn());
        RedisUtil.getInstance().set(String.format(REDIS_ACCESS_TOKEN_BY_TOKEN,token),tokenJson,accessToken.getExpireIn());
        RedisUtil.getInstance().set(String.format(REDIS_APP_INFO_BY_TOKEN,token),JSONUtil.toJsonStr(appAccessInfo),accessToken.getExpireIn());
    }

    /**
     * 获取accessToken
     * @param key
     * @return
     */
    private static AccessToken getAccessToken(String key){
        String result = RedisUtil.getInstance().get(key);
        if(ObjectUtil.isNotEmpty(result)){
            return JSONUtil.toBean(result,AccessToken.class);
        }
        return null;
    }


    /**
     * 清除accessToken
     * @param accessToken
     * @param dataAccessKey
     */
    public static void cleanAccessToken(String accessToken, String dataAccessKey) {
        RedisUtil.getInstance().del(String.format(REDIS_ACCESS_TOKEN_BY_APP_KEY,dataAccessKey)
                ,String.format(REDIS_ACCESS_TOKEN_BY_TOKEN,accessToken)
                ,String.format(REDIS_APP_INFO_BY_TOKEN,accessToken));
    }
}
