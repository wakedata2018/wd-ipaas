package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.flow.ApiFlowRelation;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author tanzhi
 * @title ApiGroupMapper
 * @date 2019/11/27 11:02
 * @projectName bdp-open
 * @descriptor
 */
public interface ApiFlowRelationMapper extends Mapper<ApiFlowRelation> {

    /**
     * 根据Api Id获取关联API对象
     *
     * @param apiId
     * @return
     */
    List<ApiFlowRelation> getApiRelations(Integer apiId);

    /**
     * 根据Api Id列表获取关联API对象
     *
     * @param dataAssetApiIds apiId列表
     * @return List<ApiFlowRelation>
     */
    List<ApiFlowRelation> getApiRelationsByApiIds(@Param("dataAssetApiIds") List<Integer> dataAssetApiIds);
}
