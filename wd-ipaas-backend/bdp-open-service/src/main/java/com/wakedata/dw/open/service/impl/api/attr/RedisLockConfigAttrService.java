package com.wakedata.dw.open.service.impl.api.attr;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.attr.RedisLockConfigAttrMapper;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;
import com.wakedata.dw.open.service.api.attr.ApiAttrService;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wujunqiang
 * @since 2023/2/21 09:26
 */
@Slf4j
@Service
public class RedisLockConfigAttrService implements ApiAttrService<RedisLockConfigAttr> {

    @Autowired
    private RedisLockConfigAttrMapper redisLockConfigAttrMapper;

    @Override
    public RedisLockConfigAttr getApiAttr(Integer dataAssetApiId, DataAssetEnums.DataApiType apiKind) {
        RedisLockConfigAttr redisLockConfigAttr = new RedisLockConfigAttr();
        redisLockConfigAttr.setApiId(dataAssetApiId);
        return redisLockConfigAttrMapper.selectOne(redisLockConfigAttr);
    }

    @Override
    public RedisLockConfigAttr saveOrUpdateApiAttr(Integer dataAssetApiId, RedisLockConfigAttr externalApi) {
        if (externalApi == null) {
            return null;
        }
        // 设置公共属性
        Date now = new Date();
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        String operator = userInfo.getUserIdentification();
        externalApi.setLesseeId(userInfo.getLesseeId());
        externalApi.setUpdateTime(now);
        externalApi.setUpdateBy(operator);

        Integer id = externalApi.getId();
        if (id == null) {
            externalApi.setCreateTime(now);
            externalApi.setCreateBy(operator);
            externalApi.setApiId(dataAssetApiId);
            redisLockConfigAttrMapper.insert(externalApi);
            return externalApi;
        }

        RedisLockConfigAttr redisLockConfigAttr = redisLockConfigAttrMapper.selectByPrimaryKey(id);
        if (redisLockConfigAttr == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_api_redis_lock_config_not_find_error);
        }
        redisLockConfigAttrMapper.updateByPrimaryKey(externalApi);
        return externalApi;
    }

    @Override
    public int deleteApiAttrByApiId(Integer dataAssetApiId) {
        if (ObjectUtil.isEmpty(dataAssetApiId)) {
            throw new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        RedisLockConfigAttr redisLockConfigAttr = new RedisLockConfigAttr();
        redisLockConfigAttr.setApiId(dataAssetApiId);
        return redisLockConfigAttrMapper.delete(redisLockConfigAttr);
    }

}
