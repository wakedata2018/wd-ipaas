package com.wakedata.dw.helper;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.redis.lock.module.LockInfo;
import com.wakedata.dw.open.enums.ActiveStateEnum;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;

import java.util.List;
import java.util.StringJoiner;

/**
 * Redis锁工具类
 *
 * @author wujunqiang
 * @since 2023/2/22 10:11
 */
public class RedisLockHelper {

    /**
     * 数据表/SQL接口redis锁key前缀，填充的内容为api id、生成key的请求参数值
     */
    private static final String API_REDIS_LOCK_KEY_PREFIX = "ipaas:lock:api:%s-%s";

    /**
     * 服务编排SQL算子redis锁key前缀，填充的内容为api id、算子步骤名、生成key的请求参数值
     */
    private static final String LITE_FLOW_REDIS_LOCK_KEY_PREFIX = "ipaas:lock:flow:%s-%s-%s";

    /**
     * 根据Redis锁配置，构建LockInfo对象，如果未开启Redis锁返回null
     *
     * @param param 请求参数
     * @param attr  Redis锁配置
     * @return LockInfo
     */
    public static LockInfo buildLockInfoFromApi(JSONObject param, RedisLockConfigAttr attr) {
        if (attr == null || ActiveStateEnum.INVALID.getValue().equals(attr.getEnableRedisLock())) {
            return null;
        }
        String lockKey = String.format(API_REDIS_LOCK_KEY_PREFIX, attr.getApiId(), joinerParams(param, attr.getKeyParams(), new StringJoiner("-")));
        return new LockInfo(attr.getLockType(), lockKey, attr.getWaitTime(), attr.getLeaseTime());
    }

    /**
     * 根据Redis锁配置，构建LockInfo对象，如果未开启Redis锁返回null
     *
     * @param params 请求参数集合
     * @param attr   Redis锁配置
     * @return LockInfo
     */
    public static LockInfo buildLockInfoFromApi(List<JSONObject> params, RedisLockConfigAttr attr) {
        if (attr == null || ActiveStateEnum.INVALID.getValue().equals(attr.getEnableRedisLock())) {
            return null;
        }
        StringJoiner paramValue = new StringJoiner("-");
        for (JSONObject param : params) {
            joinerParams(param, attr.getKeyParams(), paramValue);
        }
        String lockKey = String.format(API_REDIS_LOCK_KEY_PREFIX, attr.getApiId(), paramValue);
        return new LockInfo(attr.getLockType(), lockKey, attr.getWaitTime(), attr.getLeaseTime());
    }

    /**
     * 根据Redis锁配置，构建LockInfo对象，如果未开启Redis锁返回null
     *
     * @param param        请求参数
     * @param attr         Redis锁配置
     * @param operatorName 算子步骤名
     * @return LockInfo
     */
    public static LockInfo buildLockInfoFromLiteFlow(JSONObject param, RedisLockConfigAttr attr, String operatorName) {
        if (attr == null || ActiveStateEnum.INVALID.getValue().equals(attr.getEnableRedisLock())) {
            return null;
        }
        String lockKey = String.format(LITE_FLOW_REDIS_LOCK_KEY_PREFIX, attr.getApiId(), operatorName, joinerParams(param, attr.getKeyParams(), new StringJoiner("-")));
        return new LockInfo(attr.getLockType(), lockKey, attr.getWaitTime(), attr.getLeaseTime());
    }

    /**
     * 使用-将生成redis锁的参数值拼接起来
     *
     * @param param      请求参数
     * @param keyParams  组成锁的请求参数名集合
     * @param paramValue StringJoiner
     * @return 拼接的字符串
     */
    private static String joinerParams(JSONObject param, List<String> keyParams, StringJoiner paramValue) {
        if (CollectionUtil.isEmpty(keyParams)) {
            return "";
        }
        for (String keyParam : keyParams) {
            if (param.containsKey(keyParam)) {
                paramValue.add(param.getString(keyParam));
            }
        }
        return paramValue.toString();
    }

}
