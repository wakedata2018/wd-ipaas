package com.wakedata.dw.open.gateway;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.enums.ApiConditionDefaultColumnEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 无需鉴权
 * @author luomeng
 * @date 2022/11/22 10:36
 */
@Service
@Slf4j
public class OpenApiNoAuthServiceImpl implements OpenApiAuthService{

    @Override
    public AppAccessInfo authenticate(OpenApiParams openApiParams, AppAccessPo appAccessPo) {
        return BeanUtil.copy(appAccessPo, AppAccessInfo.class);
    }

    @Override
    public DataAssetEnums.DataAccessAppAuthType getSupportAuthType() {
        return DataAssetEnums.DataAccessAppAuthType.NO_AUTH;
    }

    @Override
    public Map<String, List<ApiConditionPo>> getApiTestAuthInfo(AppAccessPo appAccessPo) {
        return new HashMap<>(2);
    }
}
