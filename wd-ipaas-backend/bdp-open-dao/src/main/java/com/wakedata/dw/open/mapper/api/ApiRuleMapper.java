package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.ApiRulePo;
import com.wakedata.dw.open.model.domain.ApiRuleDo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author wq
 * @title ApiRuleMapper
 * @date 2020/10/19 11:11
 * @projectName dw-open
 * @description
 */
public interface ApiRuleMapper extends Mapper<ApiRulePo> {
    /**
     * 分页查询限流规则
     * @param groupId 主题id
     * @param apiKeyWord 搜索key
     * @param lesseeId 租户id
     * @return api限流信息
     */
    List<ApiRuleDo> queryRuleByCondition(@Param("groupId") Integer groupId, @Param("apiKeyWord") String apiKeyWord,@Param("lesseeId") Long lesseeId);
}
