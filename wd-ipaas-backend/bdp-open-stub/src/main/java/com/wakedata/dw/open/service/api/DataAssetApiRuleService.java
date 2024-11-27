package com.wakedata.dw.open.service.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.api.ApiRulePo;
import com.wakedata.dw.open.model.domain.ApiRuleDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.BaseDbService;

/**
 * @author wq
 * @title DataAssetApiRuleService
 * @date 2020/10/19 10:45
 * @projectName dw-open
 * @description
 */
public interface DataAssetApiRuleService extends BaseDbService<ApiRulePo> {

    /**
     * 新增api限流规则
     * @param po
     * @return
     */
    Integer addRule(ApiRulePo po);

    /**
     * 编辑api限流规则
     * @param po
     * @return
     */
    Integer updateRule(ApiRulePo po);

    /**
     * 删除限流规则
     * @param apiRuleId
     * @return
     */
    Integer deleteRule(Integer apiRuleId);

    /**
     * 分页查询限流规则
     * @param groupId
     * @param apiKeyWord
     * @param pageQuery
     * @return
     */
    Page<ApiRuleDo> queryRule(Integer groupId, String apiKeyWord, PageQuery pageQuery);

    void initFlowRules();
}
