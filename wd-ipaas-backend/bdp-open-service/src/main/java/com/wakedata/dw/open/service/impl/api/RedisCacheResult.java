package com.wakedata.dw.open.service.impl.api;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.model.api.ApiRulePo;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author ZhangXueJun
 * @title RedisResult
 * @date 2021/3/4 16:07
 * @projectName dw-open
 * @description
 */
public class RedisCacheResult<Result> {
    ApiRulePo apiRule;
    String cacheKey;
    Result results;

    public static final RedisCacheResult DUMMY = RedisCacheResult.of(null, null, null);

    public static <Result> RedisCacheResult<Result> of(ApiRulePo apiRule, String cacheKey, Result results) {
        return new RedisCacheResult(apiRule, cacheKey, results);
    }

    private RedisCacheResult(ApiRulePo apiRule, String cacheKey, Result results) {
        this.apiRule = apiRule;
        this.cacheKey = cacheKey;
        this.results = results;
    }

    private static final String SPLIT = "_";

    public static String assembleRedisKey(
            int apiId,
            int page,
            int size,
            String orderBy,
            JSONObject params) {
        String cacheKey = "dw_open:api_id" + SPLIT + apiId;
        cacheKey += SPLIT + page;
        cacheKey += SPLIT + size;
        cacheKey += SPLIT + orderBy;
        cacheKey += SPLIT + DigestUtils.md5Hex(params.toJSONString());
        return cacheKey;
    }

    public static String assembleRedisKey(
            int apiId,
            JSONObject params) {
        String cacheKey = "dw_open:api_id" + SPLIT + apiId;
        cacheKey += SPLIT + DigestUtils.md5Hex(params.toJSONString());
        return cacheKey;
    }
}
