package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.RespParamMappingRulePo;
import com.wakedata.dw.open.model.query.RespParamMappingRuleQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/3 10:28
 */
@Repository
public interface RespParamMappingRuleMapper extends Mapper<RespParamMappingRulePo> {

    /**
     * 根据查询条件查询参数映射规则列表（不查默认规则）
     *
     * @param respParamMappingRuleQuery 查询条件
     * @param lesseeId 租户Id
     * @return List<ConnectorApiPo> 连接器api列表
     */
    List<RespParamMappingRulePo> query(@Param("query") RespParamMappingRuleQuery respParamMappingRuleQuery, @Param("lesseeId") Integer lesseeId);
}
