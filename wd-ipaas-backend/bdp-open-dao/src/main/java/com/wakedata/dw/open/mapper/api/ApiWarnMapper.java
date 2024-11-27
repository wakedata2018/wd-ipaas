package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.ApiWarnDo;
import com.wakedata.dw.open.model.domain.ApiWarnQueryDo;
import com.wakedata.dw.open.model.warn.ApiWarnPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author wq
 * @title ApiWarnMapper
 * @date 2020/9/21 14:40
 * @projectName dw-open
 * @description
 */
public interface ApiWarnMapper extends Mapper<ApiWarnPo> {


    /**
     * 获取api的告警
     * @param dataAssetApiId
     * @return
     */
    List<ApiWarnPo> findByApiId(Integer dataAssetApiId);

    /**
     * 查询告警列表
     * @param apiWarnQueryDo
     * @return
     */
    List<ApiWarnDo> queryWarnByCondition(@Param("apiWarnQueryDo") ApiWarnQueryDo apiWarnQueryDo);

    /**
     * 通过主题查api
     * @param apiGroupId
     * @return
     */
    List<ApiInfoDo> findApiByGroup(@Param("apiGroupId") Integer apiGroupId);
}
