package com.wakedata.dw.open.service.impl.accesstoken;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.accesstoken.*;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.model.api.AppAccessGenerateAuthTokenPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * accessToken实现
 * @author luomeng
 * @date 2022/8/6 15:13
 */
@Service
@Slf4j
public class AccessTokenServiceImpl implements AccessTokenService {

    @Resource
    private AppAccessMapper appAccessMapper;

    @Resource
    private AppAccessTokenHelper appAccessTokenHelper;

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;

    @Override
    public AccessToken generate(String timestamp) {
        //时间戳校验
        TimestampUtil.checkTimestampAndThrowExc(timestamp, dwOpenCommonConfig.getReplayTime());
        //查询平台管理员的App Key
        AppAccessPo appAccess = getPublishAppAccessPoAndThrowExc(dwOpenCommonConfig.getPlatformAdminAppKey(),null);
        //生成临时token
        AccessToken token = new AccessToken();
        token.generateTestToken();
        //保存token
        AccessTokenRedisUtil.saveTestAccessToken(token, BeanUtil.copy(appAccess, AppAccessInfo.class));
        return token;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccessToken generate(String appKey, String timestamp, String sign) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put(DwOpenConstant.SIGN_APP_KEY, appKey);
        paramMap.put(DwOpenConstant.SIGN_TIMESTAMP, timestamp);

        //参数校验
        AppAccessPo appAccess = checkAndThrowExc(appKey,paramMap,sign);
        //判断token是否有效
        AccessToken accessToken = AccessTokenRedisUtil.getAccessTokenByAppKey(appKey);
        if(ObjectUtil.isNotNull(accessToken)){
            return accessToken;
        }
        //生成token
        AccessToken token = new AccessToken();
        token.generate();
        //保存token
        appAccessTokenHelper.saveAppAccessGenerateAuthToken(token,appAccess.getLesseeId(),appAccess.getDataAccessAppId());
        AccessTokenRedisUtil.saveAccessToken(token,appKey,BeanUtil.copy(appAccess, AppAccessInfo.class));
        return token;
    }

    @Override
    public AccessToken refresh(String appKey, String refreshToken, String timestamp, String sign) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put(DwOpenConstant.SIGN_APP_KEY, appKey);
        paramMap.put(DwOpenConstant.SIGN_TIMESTAMP, timestamp);
        paramMap.put("refreshToken", refreshToken);

        AppAccessPo appAccessPo = checkAndThrowExc(appKey, paramMap, sign);

        //校验refreshtoken是否失效
        appAccessTokenHelper.checkRefreshTokenAndThrowExc(appAccessPo.getLesseeId(),appAccessPo.getDataAccessAppId(),refreshToken);

        //如果accessToken未失效则只更新accessToken的有效期
        AccessToken token = AccessTokenRedisUtil.getAccessTokenByAppKey(appKey);
        if(ObjectUtil.isNotNull(token)){
            token.setExpireIn(DwOpenConstant.ACCESS_TOKEN_EXPIRE);
        }else{
            token = new AccessToken();
            token.setRefreshToken(refreshToken);
            token.refresh();
        }

        //更新token
        appAccessTokenHelper.refreshAuthToken(token,appAccessPo.getLesseeId(),appAccessPo.getDataAccessAppId());
        AccessTokenRedisUtil.saveAccessToken(token,appKey,BeanUtil.copy(appAccessPo, AppAccessInfo.class));
        return token;
    }

    @Override
    public Boolean check(String appKey, String accessToken, String timestamp, String sign) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put(DwOpenConstant.SIGN_APP_KEY, appKey);
        paramMap.put(DwOpenConstant.SIGN_TIMESTAMP, timestamp);
        paramMap.put(DwOpenConstant.SIGN_ACCESS_TOKEN, accessToken);

        checkAndThrowExc(appKey,paramMap,sign);

        AccessToken token = AccessTokenRedisUtil.getAccessTokenByToken(accessToken);
        return ObjectUtil.isNotNull(token);
    }

    @Override
    public Boolean cleanToken(Long lesseeId,Integer dataAccessAppId) {
        AppAccessGenerateAuthTokenPo generateAuthTokenPo = appAccessTokenHelper.getAppAccessGenerateAuthToken(lesseeId,dataAccessAppId,null);
        if(ObjectUtil.isEmpty(generateAuthTokenPo)){
            return Boolean.TRUE;
        }
        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setLesseeId(lesseeId);
        appAccessPo.setDataAccessAppId(dataAccessAppId);
        appAccessPo = appAccessMapper.selectOne(appAccessPo);
        if(ObjectUtil.isEmpty(appAccessPo)){
            return Boolean.FALSE;
        }
        AccessTokenRedisUtil.cleanAccessToken(generateAuthTokenPo.getAccessToken(),appAccessPo.getDataAccessKey());
        appAccessTokenHelper.cleanToken(generateAuthTokenPo);
        return Boolean.TRUE;
    }

    @Override
    public AccessToken getAppTokenByAppKey(String appKey, String timestamp) {
        //时间戳校验
        TimestampUtil.checkTimestampAndThrowExc(timestamp, dwOpenCommonConfig.getReplayTime());
        //查询平台管理员的App Key
        AppAccessPo appAccess = getPublishAppAccessPoAndThrowExc(appKey,null);
        //生成临时token
        AccessToken token = new AccessToken();
        token.generateTestToken();
        //保存token
        AccessTokenRedisUtil.saveTestAccessToken(token, BeanUtil.copy(appAccess, AppAccessInfo.class));
        return token;
    }

    /**
     * 校验并抛异常
     * @param appKey
     * @param paramMap
     * @param sign
     * @return
     */
    private AppAccessPo checkAndThrowExc(String appKey,Map<String,String> paramMap,String sign){
        AppAccessPo appAccess = getPublishAppAccessPoAndThrowExc(appKey,DataAssetEnums.DataAccessPublishStatus.ON_LINE);
        //时间戳校验
        TimestampUtil.checkTimestampAndThrowExc(paramMap.get(DwOpenConstant.SIGN_TIMESTAMP),dwOpenCommonConfig.getReplayTime());
        //签名校验
        SignUtil.checkSignAndThrowExc(paramMap,null,appAccess.getDataAccessSecret(),sign);
        return appAccess;
    }

    /**
     * 根据appKey获取已发布的应用信息
     * @param appKey
     * @return
     */
    private AppAccessPo getPublishAppAccessPoAndThrowExc(String appKey,DataAssetEnums.DataAccessPublishStatus dataAccessStatus) {
        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setDataAccessKey(appKey);
        appAccessPo.setPublishStatus(dataAccessStatus);
        AppAccessPo accessPo = appAccessMapper.selectOne(appAccessPo);
        if(ObjectUtil.isNull(accessPo)){
            throw new OpenException(OpenApiMsgCodeEnum.w_data_gateway_app_id_no_found);
        }
        return accessPo;
    }
}
