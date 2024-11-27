package com.wakedata.dw.open.service.impl.api;

import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.api.GlobalParamService;
import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月21日 20:32:31
 */
@Service
@Slf4j
public class GlobalParamServiceImpl implements GlobalParamService {

    @Resource
    private AppAccessMapper appAccessMapper;

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Override
    public AppAccessAuthConfig queryAppInfoByAppId(Integer appId) {
        AppAccessPo appAccessPo = appAccessMapper.selectByPrimaryKey(appId);
        return openApiDataCache.getWdAuthAppInfo(appAccessPo);
    }
}
