package com.wakedata.dw.openapi.service;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.openapi.DwOpenAccessAuthPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;

/**
 * 应用下授权绑定的关联信息-服务
 * @author 佟蕊
 */
public interface DwOpenAccessAuthService extends BaseDbService<DwOpenAccessAuthPo> {


    /**
     * 通过应用id更新应用授权绑定的关联信息
     * @param dwOpenAccessAuthPo 应用下授权绑定的关联信息
     * @return
     */
    Integer updateByDataAccessAppId(DwOpenAccessAuthPo dwOpenAccessAuthPo);

    /**
     * 根据应用id获取当前的应用配置信息
     * @param appId 应用id
     * @return ResultDTO<String> 配置信息
     */
    ResultDTO<String>  getAuthConfigByAppId(Integer appId);

    /**
     * 查询应用绑定的授权信息
     * @param lesseeId 租户id
     * @param dataAccessAppId 应用id
     * @param type 授权类型（0：惟客云应用，1：其他）{@link com.wakedata.dw.open.enums.DataAssetEnums.AppAuthType}
     * @return
     */
    AppAccessAuthConfig getAppAuthConfigByAppId(Long lesseeId, Integer dataAccessAppId, Integer type);
}
