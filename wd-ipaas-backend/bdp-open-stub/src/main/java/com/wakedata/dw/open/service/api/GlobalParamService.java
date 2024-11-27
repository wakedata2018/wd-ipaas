package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月21日 20:33:50
 */
public interface GlobalParamService {

    /**
     * 根据appId查询惟客云信息
     *
     * @param appId 应用id
     * @return 惟客云鉴权信息
     */
    AppAccessAuthConfig queryAppInfoByAppId(Integer appId);
}
