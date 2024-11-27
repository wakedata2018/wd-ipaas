package com.wakedata.dw.open.service.api.attr;

import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;

/**
 * TODO 重构现有的SQL TABLE API
 *
 * @author ZhangXueJun
 * @title ApiAttrService
 * @date 2021/3/1 16:06
 * @projectName dw-open
 * @description
 */
public interface ApiAttrService<T extends AbstractApiAttr> {

    /**
     * 获取外部注册API
     *
     * @param dataAssetApiId
     * @param apiKind
     * @return
     */
    T getApiAttr(Integer dataAssetApiId, DataAssetEnums.DataApiType apiKind);

    /**
     * 保存API 属性
     *
     * @param dataAssetApiId
     * @param externalApi
     * @return
     */
    T saveOrUpdateApiAttr(Integer dataAssetApiId, T externalApi);

    /**
     * 根据API 删除流程编排
     *
     * @param dataAssetApiId
     * @return
     */
    int deleteApiAttrByApiId(Integer dataAssetApiId);
}