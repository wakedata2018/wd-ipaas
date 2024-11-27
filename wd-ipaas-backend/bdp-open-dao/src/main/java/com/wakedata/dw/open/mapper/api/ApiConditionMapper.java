package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.ApiConditionPo;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.Set;

/**
 * @author tanzhi
 * @title ApiConditionMapper
 * @projectName bdp-open
 * @date 2019/8/19 15:32
 * @description
 */
public interface ApiConditionMapper extends Mapper<ApiConditionPo>, InsertListMapper<ApiConditionPo> {

    /**
     * 资产列
     *
     * @param dataAssetId
     * @return
     */
    Set<String> getAssetColumns(@Param("dataAssetId") Integer dataAssetId);


    /**
     * 获取没有授权的列
     *
     * @param dataAssetIds    apiId
     * @param dataAccessAppId 应用id
     * @param hasAuth         是否已经认证
     * @return 数据列
     */
    List<ApiConditionPo> listByDataAssetId(@Param("dataAssetIds") Set<Integer> dataAssetIds,
                                           @Param("dataAccessAppId") Integer dataAccessAppId,
                                           @Param("hasAuth") Boolean hasAuth);
}
