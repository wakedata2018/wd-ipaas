package com.wakedata.dw.open.service.impl.auth;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.mapper.auth.AuthInfoMapper;
import com.wakedata.dw.open.model.api.dto.AuthInfoDTO;
import com.wakedata.dw.open.model.auth.AuthCountDo;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.vo.ApiAuthorizationVo;
import com.wakedata.dw.open.service.vo.AppAccessRuleVo;
import com.wakedata.dw.open.service.vo.AuthInfoVo;
import com.wakedata.dw.open.service.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 认证信息服务实现
 *
 * @author chenshaopeng
 * @date 2021/11/8
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthInfoServiceImpl extends BaseServiceImpl<AuthInfoPo, AuthInfoMapper> implements AuthInfoService {

    @Autowired
    private AppAccessRuleService appAccessRuleService;
    @Autowired
    private AuthInfoService authInfoService;

    @Autowired
    @Override
    protected void init(CurdService<AuthInfoPo> curdService, AuthInfoMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public void addAuthInfo(AuthInfoPo authInfo) {
        authInfo.setCreateTime(new Date());
        authInfo.setUpdateTime(authInfo.getCreateTime());
        authInfo.setLesseeId(AuthUtils.currentAppId());
        super.add(authInfo);
    }

    @Override
    public void updateAuthInfo(AuthInfoPo authInfo) {
        authInfo.setCreateUser(authInfo.getCreateUser());
        authInfo.setCreateTime(authInfo.getCreateTime());
        authInfo.setUpdateTime(new Date());
        super.update(authInfo);
    }

    @Override
    public void deleteAuthInfo(Integer authInfoId) {
        super.delete(authInfoId);
    }

    @Override
    public AuthInfoPo detail(Integer authInfoId) {
        return super.show(authInfoId);
    }

    @Override
    public Page<AuthInfoPo> selectPageLikeOrderBy(PageQuery pageQuery, String keyword, List<String> likeColumns) {
        AuthInfoPo po = new AuthInfoPo();
        po.setLesseeId(AuthUtils.currentAppId());
        return super.selectPageLikeOrderBy(po, pageQuery.getPageNo(), pageQuery.getPageSize()
                , keyword, likeColumns, "CREATE_TIME", false, null, null, null, null, null);
    }

    @Override
    public List<AuthCountDo> queryApiReference(List<Integer> collect) {
        return this.getMapper().countApiByList(collect);
    }

    @Override
    public List<ApiInfoDo> queryThirdPartyApi(Integer id) {
        List<Integer> thirdPartyApiIdList = this.getMapper().selectThirdPartyApiId(id);
        if (thirdPartyApiIdList.size() == 0) {
            return null;
        }
        return this.getMapper().selectApiIdAndName(thirdPartyApiIdList);
    }

    @Override
    public List<AuthInfoPo> queryExternalApplication(Integer id) {
        return this.getMapper().queryExternalApplication(id);
    }

    @Override
    public List<AuthInfoDTO> getAuthApplicationList(Integer apiId) {
        List<AuthInfoDTO> result = new ArrayList<>();
        List<AuthInfoPo> authInfoPos = this.queryExternalApplication(apiId);
        for (AuthInfoPo authInfoPo : authInfoPos) {
            result.add(new AuthInfoDTO(authInfoPo.getId(), authInfoPo.getAppName()));
        }
        return result;
    }

    @Override
    public Date queryAuthorizationTime(Integer apiId, Integer authId) {
        return this.getMapper().queryAuthorizationTime(apiId, authId);
    }

    @Override
    public List<ApiAuthorizationVo> queryApiAndApplication(Integer dataAssetId, Integer dataAccessAppId) {
        AppAccessRuleVo appAccessRuleVo = appAccessRuleService.appAccessRule(dataAccessAppId, dataAssetId);
        List<ApiAuthorizationVo> apiAuthorizationVos = new ArrayList<>();
        Set<Map.Entry<String, AppAccessRuleVo>> entries = appAccessRuleVo.getOperatorAppAccessRules().entrySet();
        for (Map.Entry<String, AppAccessRuleVo> entry : entries) {
            int id = entry.getValue().getApiId();
            List<AuthInfoPo> authInfoPos = authInfoService.queryExternalApplication(entry.getValue().getApiId());
            List<AuthInfoVo> authInfoVoList = new ArrayList<>();
            for (AuthInfoPo authInfoPo : authInfoPos) {
                AuthInfoVo authInfoVo = new AuthInfoVo();
                BeanUtil.copyProperties(authInfoPo, authInfoVo);
                authInfoVo.setAuthorizationTime(authInfoService.queryAuthorizationTime(entry.getValue().getApiId(), authInfoPo.getId()));
                authInfoVoList.add(authInfoVo);
            }
            ApiAuthorizationVo apiAuthorizationVo = new ApiAuthorizationVo();
            apiAuthorizationVo.setApiId(entry.getValue().getApiId());
            apiAuthorizationVo.setStepChineseName(entry.getKey());
            apiAuthorizationVo.setAuthInfoVos(authInfoVoList);
            apiAuthorizationVos.add(apiAuthorizationVo);
        }
        return apiAuthorizationVos;
    }

    @Override
    public boolean deleteAuthorization(Integer apiId, Integer authId) {
        return this.getMapper().deleteAuthorization(apiId, authId) > 0;
    }

    /**
     * 根据ID集合查询第三方应用详情信息
     *
     * @param idList 第三方应用ID集合
     * @return 第三方应用集合
     */
    @Override
    public List<AuthInfoPo> selectByIds(List<Integer> idList) {
        return super.getMapper().selectByIds(idList);
    }

}