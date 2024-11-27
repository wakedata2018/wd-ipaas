package com.wakedata.dw.open.service.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.ApiWarnDo;
import com.wakedata.dw.open.model.domain.ApiWarnQueryDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.warn.ApiWarnPo;
import com.wakedata.dw.open.service.BaseDbService;

import java.util.List;

/**
 * @author wq
 * @title DataAssetApiWarnService
 * @date 2020/9/21 14:36
 * @projectName dw-open
 * @description
 */
public interface DataAssetApiWarnService extends BaseDbService<ApiWarnPo> {

    /**
     * 新增api告警
     * @param apiWarnPo
     * @return
     */
    Integer addWarn(ApiWarnPo apiWarnPo);

    /**
     * 编辑api告警
     * @param apiWarnPo
     * @return
     */
    Integer updateWarn(ApiWarnPo apiWarnPo);

    /**
     * 删除api告警
     * @param apiWarnId
     * @return
     */
    Integer deleteWarn(Integer apiWarnId);

    /**
     * 改变告警状态
     * @param apiWarnId
     * @param status
     * @return
     */
    Integer status(Integer apiWarnId, Boolean status);


    /**
     * 获取api主题
     * @return
     */
    List<ApiGroupPo> getGroupList();

    /**
     * 查询api告警设置
     * @param apiWarnQueryDo
     * @param pageQuery
     * @return
     */
    Page<ApiWarnDo> queryWarn(ApiWarnQueryDo apiWarnQueryDo, PageQuery pageQuery);

    /**
     * 通过主题查api
     * @param groupId
     * @return
     */
    List<ApiInfoDo> queryApiByGroup(Integer groupId);
}
