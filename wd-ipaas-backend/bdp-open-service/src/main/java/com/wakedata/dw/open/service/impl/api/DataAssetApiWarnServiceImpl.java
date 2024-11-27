package com.wakedata.dw.open.service.impl.api;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiWarnMapper;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.ApiWarnDo;
import com.wakedata.dw.open.model.domain.ApiWarnQueryDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.warn.ApiWarnPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataAssetApiWarnService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wq
 * @title DataAssetApiWarnServiceImpl
 * @date 2020/9/21 14:39
 * @projectName dw-open
 * @description
 */
@Service
@Slf4j
public class DataAssetApiWarnServiceImpl extends BaseServiceImpl<ApiWarnPo, ApiWarnMapper> implements DataAssetApiWarnService {
    @Autowired
    @Override
    protected void init(CurdService<ApiWarnPo> curdService, ApiWarnMapper mapper) {
        super.set(curdService, mapper);
    }

    @Autowired
    private ApiGroupService apiGroupService;

    @Override
    public Integer addWarn(ApiWarnPo apiWarnPo) {
        checkWarn(apiWarnPo,true);
        return this.getCurdService().insert(apiWarnPo,this.getMapper());
    }

    @Override
    public Integer updateWarn(ApiWarnPo apiWarnPo) {
        checkWarn(apiWarnPo,false);
        apiWarnPo.setUpdateTime(new Date());
        return this.getCurdService().updateByPrimaryKeySelective(apiWarnPo,this.getMapper());
    }

    @Override
    public Integer deleteWarn(Integer apiWarnId) {
        return this.getCurdService().deleteByPrimaryKey(apiWarnId,this.getMapper());
    }

    @Override
    public Integer status(Integer apiWarnId, Boolean status) {
        ApiWarnPo po = new ApiWarnPo();
        po.setId(apiWarnId);
        po.setStatus(status);
        return this.getMapper().updateByPrimaryKeySelective(po);
    }

    @Override
    public List<ApiGroupPo> getGroupList() {
        return apiGroupService.listAllApi();
    }

    @Override
    public Page<ApiWarnDo> queryWarn(ApiWarnQueryDo apiWarnQueryDo, PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        Page<ApiWarnDo> dataAccessApprovalDos = (Page<ApiWarnDo>)this.getMapper().queryWarnByCondition(apiWarnQueryDo);
        return dataAccessApprovalDos;
    }

    @Override
    public List<ApiInfoDo> queryApiByGroup(Integer groupId) {
        return Optional.ofNullable(this.getMapper().findApiByGroup(groupId)).orElse(new ArrayList<>());
    }

    public void checkWarn(ApiWarnPo apiWarnPo,Boolean isInsert) {
        //校验ap的告警是否已经存在了
        ApiWarnPo poApiId = new ApiWarnPo();
        poApiId.setApiId(apiWarnPo.getApiId());
        List<ApiWarnPo> selectId = this.getMapper().select(poApiId);
        if (CollectionUtils.isNotEmpty(selectId)) {
            if (!isInsert) {
                List<Integer> collect = selectId.stream().map(ApiWarnPo::getId).collect(Collectors.toList());
                collect.remove(apiWarnPo.getId());
                if (CollectionUtils.isNotEmpty(collect)) {
                    throw new OpenException(MsgCodeEnum.w_data_api_warn_exist);
                }
            }else {
                throw new OpenException(MsgCodeEnum.w_data_api_warn_exist);
            }
        }
        //校验告警名称是否重复
        ApiWarnPo poName = new ApiWarnPo();
        poName.setName(apiWarnPo.getName());
        List<ApiWarnPo> selectName = this.getMapper().select(poName);
        if (CollectionUtils.isNotEmpty(selectName)) {
            if (!isInsert) {
                List<Integer> collect = selectName.stream().map(ApiWarnPo::getId).collect(Collectors.toList());
                collect.remove(apiWarnPo.getId());
                if (CollectionUtils.isNotEmpty(collect)) {
                    throw new OpenException(MsgCodeEnum.w_data_api_name_duplicate);
                }
            }else {
                throw new OpenException(MsgCodeEnum.w_data_api_name_duplicate);
            }
        }
    }
}
