package com.wakedata.dw.open.service.impl.accesstoken;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.accesstoken.AccessToken;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.AppAccessGenerateAuthTokenMapper;
import com.wakedata.dw.open.model.api.AppAccessGenerateAuthTokenPo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author luomeng
 * @date 2022/8/6 17:05
 */
@Component
public class AppAccessTokenHelper {

    @Resource
    private AppAccessGenerateAuthTokenMapper appAccessGenerateAuthTokenMapper;

    /**
     * 查询app生成的auth token信息
     * @param lesseeId
     * @param dataAccessAppId
     * @param refreshToken
     * @return
     */
    public AppAccessGenerateAuthTokenPo getAppAccessGenerateAuthToken(Long lesseeId,Integer dataAccessAppId,String refreshToken){
        AppAccessGenerateAuthTokenPo tokenPo = new AppAccessGenerateAuthTokenPo();
        tokenPo.setLesseeId(lesseeId);
        tokenPo.setDataAccessAppId(dataAccessAppId);
        if(ObjectUtil.isNotEmpty(refreshToken)){
            tokenPo.setRefreshToken(refreshToken);
        }
        return appAccessGenerateAuthTokenMapper.selectOne(tokenPo);

    }

    /**
     * 保存token信息
     * @param accessToken
     * @param lesseeId
     * @param dataAccessAppId
     */
    public void saveAppAccessGenerateAuthToken(AccessToken accessToken,Long lesseeId,Integer dataAccessAppId){

        AppAccessGenerateAuthTokenPo generateAuthTokenPo = this.getAppAccessGenerateAuthToken(lesseeId,dataAccessAppId,null);
        if(ObjectUtil.isNull(generateAuthTokenPo)){
            AppAccessGenerateAuthTokenPo appAccessGenerateAuthTokenPo = createAppAccessAuthToken(accessToken, lesseeId, dataAccessAppId);
            appAccessGenerateAuthTokenMapper.insert(appAccessGenerateAuthTokenPo);
            return;
        }
        //修改
        AppAccessGenerateAuthTokenPo updateAuthTokenPo = getUpdateAuthToken(accessToken);
        updateAuthTokenPo.setId(generateAuthTokenPo.getId());
        appAccessGenerateAuthTokenMapper.updateByPrimaryKeySelective(updateAuthTokenPo);

    }

    /**
     * 校验refreshToken是否有效
     * @param lesseeId
     * @param dataAccessAppId
     * @param refreshToken
     */
    public void checkRefreshTokenAndThrowExc(Long lesseeId, Integer dataAccessAppId, String refreshToken) {
        AppAccessGenerateAuthTokenPo generateAuthTokenPo = this.getAppAccessGenerateAuthToken(lesseeId,dataAccessAppId,refreshToken);
        if(ObjectUtil.isNull(generateAuthTokenPo)){
            throw new OpenException(OpenApiMsgCodeEnum.s_refresh_token_invalid);
        }
        long time = DateUtil.between(new Date(), generateAuthTokenPo.getRefreshTokenExpireTime(), DateUnit.SECOND, false);
        if(time < 0){
            throw new OpenException(OpenApiMsgCodeEnum.s_refresh_token_invalid);
        }
    }

    /**
     * 更新token
     * @param token
     * @param lesseeId
     * @param dataAccessAppId
     */
    public void refreshAuthToken(AccessToken token, Long lesseeId, Integer dataAccessAppId) {
        AppAccessGenerateAuthTokenPo generateAuthTokenPo = this.getAppAccessGenerateAuthToken(lesseeId,dataAccessAppId,null);
        if(ObjectUtil.isNull(generateAuthTokenPo)){
            throw new OpenException(OpenApiMsgCodeEnum.s_refresh_token_invalid);
        }
        //更新
        AppAccessGenerateAuthTokenPo updateAuthTokenPo = new AppAccessGenerateAuthTokenPo();
        Date date = new Date();
        updateAuthTokenPo.setAccessToken(token.getAccessToken());
        updateAuthTokenPo.setRefreshNum(ObjectUtil.defaultIfNull(generateAuthTokenPo.getRefreshNum(),0) + 1);
        updateAuthTokenPo.setTokenExpireTime(DateUtil.offsetSecond(date,token.getExpireIn()));
        updateAuthTokenPo.setUpdateTime(date);
        updateAuthTokenPo.setId(generateAuthTokenPo.getId());
        appAccessGenerateAuthTokenMapper.updateByPrimaryKeySelective(updateAuthTokenPo);

    }


    /**
     * 构造更新token对象
     * @param accessToken
     * @return
     */
    private AppAccessGenerateAuthTokenPo getUpdateAuthToken(AccessToken accessToken) {
        AppAccessGenerateAuthTokenPo updateAuthTokenPo = new AppAccessGenerateAuthTokenPo();
        updateAuthTokenPo.setRefreshNum(0);
        Date date = new Date();
        setAuthToken(accessToken, updateAuthTokenPo, date);
        updateAuthTokenPo.setUpdateTime(date);
        return updateAuthTokenPo;
    }

    /**
     * 构造token对象
     * @param accessToken
     * @param lesseeId
     * @param dataAccessAppId
     * @return
     */
    private AppAccessGenerateAuthTokenPo createAppAccessAuthToken(AccessToken accessToken, Long lesseeId, Integer dataAccessAppId) {
        AppAccessGenerateAuthTokenPo appAccessGenerateAuthTokenPo = new AppAccessGenerateAuthTokenPo();
        appAccessGenerateAuthTokenPo.setLesseeId(lesseeId);
        appAccessGenerateAuthTokenPo.setDataAccessAppId(dataAccessAppId);
        appAccessGenerateAuthTokenPo.setRefreshNum(0);
        Date date = new Date();
        setAuthToken(accessToken, appAccessGenerateAuthTokenPo, date);
        appAccessGenerateAuthTokenPo.setCreateTime(date);
        appAccessGenerateAuthTokenPo.setUpdateTime(date);
        return appAccessGenerateAuthTokenPo;
    }

    /**
     * 设置token及有效期
     * @param accessToken
     * @param appAccessGenerateAuthTokenPo
     * @param date
     */
    private void setAuthToken(AccessToken accessToken, AppAccessGenerateAuthTokenPo appAccessGenerateAuthTokenPo, Date date) {
        appAccessGenerateAuthTokenPo.setAccessToken(accessToken.getAccessToken());
        appAccessGenerateAuthTokenPo.setRefreshToken(accessToken.getRefreshToken());
        appAccessGenerateAuthTokenPo.setTokenExpireTime(DateUtil.offsetSecond(date,accessToken.getExpireIn()));
        appAccessGenerateAuthTokenPo.setRefreshTokenExpireTime(DateUtil.offsetDay(date, DwOpenConstant.REFRESH_TOKEN_EXPIRE));
    }


    /**
     * 清除token
     * @param generateAuthTokenPo
     */
    public void cleanToken(AppAccessGenerateAuthTokenPo generateAuthTokenPo) {
        Date date = new Date();
        generateAuthTokenPo.setAccessToken(null);
        generateAuthTokenPo.setRefreshToken(null);
        generateAuthTokenPo.setRefreshNum(0);
        generateAuthTokenPo.setTokenExpireTime(null);
        generateAuthTokenPo.setRefreshTokenExpireTime(null);
        generateAuthTokenPo.setUpdateTime(date);
        appAccessGenerateAuthTokenMapper.updateByPrimaryKey(generateAuthTokenPo);
    }
}
