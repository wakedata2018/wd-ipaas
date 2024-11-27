package com.wakedata.dw.open.gateway;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wakedata.dw.open.accesstoken.*;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.ApiConditionDefaultColumnEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import com.wakedata.dw.open.utils.HttpParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * accessToken鉴权
 * @author luomeng
 * @date 2022/11/22 10:32
 */
@Service
@Slf4j
public class OpenApiAccessTokenAuthServiceImpl implements OpenApiAuthService{

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;

    @Resource
    private AccessTokenService accessTokenService;

    @Override
    public AppAccessInfo authenticate(OpenApiParams openApiParams, AppAccessPo appAccessPo) {
        Map<String,String> paramMap = HttpParamUtil.getRequestParams(openApiParams.getRequest());
        log.info("openapi request api:{},param:{},postData:{}",openApiParams.getDataAssetApiMethod(),paramMap,openApiParams.getPostData());
        if(ObjectUtil.isEmpty(openApiParams.getAccessToken())){
            throw new OpenException(OpenApiMsgCodeEnum.s_access_token_invalid);
        }
        //token验证
        AccessToken accessToken = AccessTokenRedisUtil.getAccessTokenByToken(openApiParams.getAccessToken());
        AppAccessInfo appAccessInfo = AccessTokenRedisUtil.getAppInfoByToken(openApiParams.getAccessToken());
        if(ObjectUtil.isNull(accessToken) || ObjectUtil.isNull(appAccessInfo)){
            throw new OpenException(OpenApiMsgCodeEnum.s_access_token_invalid);
        }
        if(isApiTest(openApiParams)){
            appAccessInfo.setIsApiTest(Boolean.TRUE);
            return appAccessInfo;
        }
        //时间戳校验
        TimestampUtil.checkTimestampAndThrowExc(paramMap.get(DwOpenConstant.SIGN_TIMESTAMP),dwOpenCommonConfig.getReplayTime());
        //签名校验
        SignUtil.checkSignAndThrowExc(paramMap
                ,(ObjectUtil.isEmpty(openApiParams.getPostData())?null:openApiParams.getPostData())
                ,appAccessInfo.getDataAccessSecret()
                ,openApiParams.getSign());
        appAccessInfo.setIsApiTest(Boolean.FALSE);
        return appAccessInfo;
    }

    /**
     * 是否是api测试
     * @param openApiParams
     * @return
     */
    private boolean isApiTest(OpenApiParams openApiParams) {
        if(StrUtil.isBlank(openApiParams.getAccessToken())){
            return false;
        }
        return openApiParams.getAccessToken().contains(DwOpenConstant.TEST_ACCESS_TOKEN_PREFIX);
    }

    @Override
    public DataAssetEnums.DataAccessAppAuthType getSupportAuthType() {
        return DataAssetEnums.DataAccessAppAuthType.TOKEN_AUTH;
    }

    @Override
    public Map<String, List<ApiConditionPo>> getApiTestAuthInfo(AppAccessPo appAccessPo) {
        Map<String,List<ApiConditionPo>> param = new HashMap<>(2);
        List<ApiConditionPo> conditionPoList = new ArrayList<>();
        conditionPoList.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN_ACCESS_TOKEN,accessTokenService.getAppTokenByAppKey(appAccessPo.getDataAccessKey(),String.valueOf(System.currentTimeMillis())).getAccessToken(),0));
        conditionPoList.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN, IdUtil.fastSimpleUUID(),0));
        param.put(HttpParamKind.QUERY.name(),conditionPoList);
        return param;
    }
}
