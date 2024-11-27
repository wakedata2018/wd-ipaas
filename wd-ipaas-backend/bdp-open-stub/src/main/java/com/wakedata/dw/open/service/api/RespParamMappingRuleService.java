package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.api.dto.RespParamMappingRuleDTO;
import com.wakedata.dw.open.model.query.RespParamMappingRuleQuery;

import java.util.List;

/**
 * api响应体参数映射规则service
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/3 10:24
 */
public interface RespParamMappingRuleService {


    /**
     * 新增映射规则
     *
     * @param respParamMappingRuleDTO 映射规则实体
     * @return Long 返回新增后的id
     */
    ResultDTO<Integer> addRule(RespParamMappingRuleDTO respParamMappingRuleDTO);

    /**
     * 更新映射规则
     *
     * @param respParamMappingRuleDTO 映射规则实体
     * @return Integer 返回更新成功的条数
     */
    ResultDTO<Integer> updateRule(RespParamMappingRuleDTO respParamMappingRuleDTO);

    /**
     * 删除映射规则
     *
     * @param id 映射规则主键id
     * @return Integer 返回删除成功的条数
     */
    ResultDTO<Integer> deleteRule(Integer id);

    /**
     * 分页查询映射规则
     *
     * @param respParamMappingRuleQuery 映射规则查询条件
     * @return List<RespParamMappingRuleDTO> 返回映射规则实体DTO列表
     */
    PageResultDTO<List<RespParamMappingRuleDTO>> queryRule(RespParamMappingRuleQuery respParamMappingRuleQuery);

    /**
     * 根据id查询映射规则
     *
     * @param id 映射规则id
     * @return RespParamMappingRuleDTO 返回映射规则实体DTO
     */
    ResultDTO<RespParamMappingRuleDTO> queryRuleById(Integer id);

    /**
     * 查询所有映射规则id和名称
     *
     * @return List<RespParamMappingRuleDTO> 返回映射规则实体DTO列表
     */
    ResultDTO<List<RespParamMappingRuleDTO>> queryRuleIdAndName();
}
