package com.wakedata.dw.open.service.impl.api;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiRuleMapper;
import com.wakedata.dw.open.model.api.ApiRulePo;
import com.wakedata.dw.open.model.domain.ApiRuleDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.DataAssetApiRuleService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.utils.SentinelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wq
 * @title DataAssetApiRuleServiceImpl
 * @date 2020/10/19 11:26
 * @projectName dw-open
 * @description
 */
@Service
@Slf4j
public class DataAssetApiRuleServiceImpl extends BaseServiceImpl<ApiRulePo, ApiRuleMapper> implements DataAssetApiRuleService, ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    @Override
    protected void init(CurdService<ApiRulePo> curdService, ApiRuleMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Integer addRule(ApiRulePo po) {
        checkRule(po,true);
        int cnt =  this.getCurdService().insert(po,this.getMapper());
        initFlowRules();
        return cnt;
    }

    @Override
    public Integer updateRule(ApiRulePo po) {
        checkRule(po,false);
        po.setUpdateTime(new Date());
        int updateCnt = this.getCurdService().updateByPrimaryKeySelective(po,this.getMapper());
        initFlowRules();
        return updateCnt;
    }

    @Override
    public Integer deleteRule(Integer apiRuleId) {
        int deleteCnt= this.getCurdService().deleteByPrimaryKey(apiRuleId,this.getMapper());
        initFlowRules();
        return deleteCnt;
    }

    @Override
    public Page<ApiRuleDo> queryRule(Integer groupId, String apiKeyWord, PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        Long lesseeId = AuthUtils.currentAppId();
        Page<ApiRuleDo> apiRulePos = (Page<ApiRuleDo>) this.getMapper().queryRuleByCondition(groupId, apiKeyWord,lesseeId);
        return apiRulePos;
    }

    @Override
    public void initFlowRules() {
        List<ApiRulePo> apiRules = getMapper().selectAll();
        if(CollUtil.isEmpty(apiRules)){
            return;
        }
        SentinelUtils.initFlowRules(apiRules.stream().filter(apiRulePo -> apiRulePo.getQps() > CommonConstant.ZERO).collect(Collectors.toList()));
    }

    private void checkRule(ApiRulePo po, boolean isInsert) {
        //校验ap的告警是否已经存在了
        ApiRulePo rulePo = new ApiRulePo();
        rulePo.setDataAssetApiId(po.getDataAssetApiId());
        List<ApiRulePo> selectId = this.getMapper().select(rulePo);
        if (CollectionUtils.isNotEmpty(selectId)) {
            if (!isInsert) {
                List<Integer> collect = selectId.stream().map(ApiRulePo::getId).collect(Collectors.toList());
                collect.remove(po.getId());
                if (CollectionUtils.isNotEmpty(collect)) {
                    throw new OpenException(MsgCodeEnum.w_data_api_rule_exist);
                }
            }else {
                throw new OpenException(MsgCodeEnum.w_data_api_rule_exist);
            }
        }
        //校验告警名称是否重复
        ApiRulePo poName = new ApiRulePo();
        poName.setRuleName(po.getRuleName());
        List<ApiRulePo> selectName = this.getMapper().select(poName);
        if (CollectionUtils.isNotEmpty(selectName)) {
            if (!isInsert) {
                List<Integer> collect = selectName.stream().map(ApiRulePo::getId).collect(Collectors.toList());
                collect.remove(po.getId());
                if (CollectionUtils.isNotEmpty(collect)) {
                    throw new OpenException(MsgCodeEnum.w_data_api_rule_name_duplicate);
                }
            }else {
                throw new OpenException(MsgCodeEnum.w_data_api_rule_name_duplicate);
            }
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        initFlowRules();
    }
}
