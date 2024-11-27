package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.AppAccessRulePo;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.Set;

/**
 * @author tanzhi
 * @title DataAccessRuleMapper
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
public interface AppAccessRuleMapper extends Mapper<AppAccessRulePo>, InsertListMapper<AppAccessRulePo> {

    /**
     * 查询api的访问规则
     *
     * @param dataAccessAppId
     * @param dataAssetApiId
     * @return
     */
    Set<String> searchDataAppRuleSetOfApi(@Param(value = "dataAccessAppId") Integer dataAccessAppId, @Param(value = "dataAssetApiId") Integer dataAssetApiId);

    /**
     * 通过api id和app id 删除关联关系
     * @param dataAccessAppId
     * @param dataAssetApiId
     */
    void deleteByApiAppId(@Param("appId") Integer dataAccessAppId, @Param("apiId") Integer dataAssetApiId);

    /**
     * 通过api 根据API ID查询相关的APP ID
     * @param idList
     */
    List<Integer> searchAccessAppIdWithAuth(List<Integer> idList);

    /**
     * 通过api 根据API ID查询相关的APP ID
     * @param dataAssetApiIds apiId列表
     * @return Boolean
     */
    Boolean batchDeleteByApiIds(@Param("dataAssetApiIds") List<Integer> dataAssetApiIds);
}
