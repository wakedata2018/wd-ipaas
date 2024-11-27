package com.wakedata.dw.open.service.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import com.wakedata.dw.open.service.vo.AppAccessRuleVo;

import java.util.List;
import java.util.Set;

/**
 * @author yiyufeng
 * @title DataAccessRuleService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface AppAccessRuleService {

    /**
     * 访问规则列表
     *
     * @param dataAssetApiId
     * @param dataAccessAppId
     * @return
     */
    AppAccessRuleDo getAppAccessRule(Integer dataAssetApiId, Integer dataAccessAppId);

    /**
     * 多数据源版
     * @param dataAccessAppId
     * @param pageNo
     * @param pageSize
     * @param keyword
     * @param dataSourceId
     * @return
     */
    Page<AppAccessRuleVo> pageAppAccessRuleMultiDatasource(Integer dataAccessAppId, Integer pageNo, Integer pageSize, String keyword, Integer dataSourceId);

    /**
     * 某个接入端对某个数据资产的数据访问权限
     *
     * @param dataAccessAppId
     * @param dataAssetId
     * @return
     */
    AppAccessRuleVo appAccessRule(Integer dataAccessAppId, Integer dataAssetId);


    /**
     * 存储数据访问规则
     *
     * @param dataAccessRule
     * @return
     */
    AppAccessRuleDo storeAppAccessRule(AppAccessRuleDo dataAccessRule);

    /**
     * 某个api的数据访问规则
     *
     * @param dataAccessAppId
     * @param dataAccessApiId
     * @return
     */
    Set<String> searchDataAppRuleOfApi(Integer dataAccessAppId, Integer dataAccessApiId);

    /**
     * 验证数据访问规则
     *
     * @param dataAccessRuleReq
     * @param dataAccessRuleResp
     * @param dataAccessRule
     * @return
     */
    Boolean validateAppAccessRule(Set<String> dataAccessRuleReq, Set<String> dataAccessRuleResp, AppAccessRuleDo dataAccessRule);

    /**
     * 删除某个app的删除所有访问规则
     *
     * @param dataAccessAppId
     * @return
     */
    int deleteAppAccessRuleOfAppId(Integer dataAccessAppId);

    /**
     * 删除
     * @param dataAssetApiId
     * @return
     */
    int delete(Integer dataAssetApiId);

    /**
     * 撤消权限
     *
     * @param dataAccessRuleList
     * @return
     */
    boolean revokeAppAccessRule(List<AppAccessRuleDo> dataAccessRuleList);

    /**
     * 删除审批单
     * @param dataAssetApiId
     * @param dataAccessAppId
     * @return
     */
    int delete(Integer dataAssetApiId,Integer dataAccessAppId);

    /**
     * 通过api id删除授权字段
     * @param dataAssetApiId api id
     */
    void deleteAppAccessRuleOfApiId(Integer dataAssetApiId);

    /**
     * 通过api id列表批量删除授权字段
     * @param dataAssetApiIds api id列表
     * @return Boolean
     */
    Boolean batchDeleteAppAccessRuleOfApiId(List<Integer> dataAssetApiIds);
}
