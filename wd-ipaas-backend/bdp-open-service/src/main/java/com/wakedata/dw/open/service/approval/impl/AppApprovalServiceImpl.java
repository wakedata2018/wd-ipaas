package com.wakedata.dw.open.service.approval.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.AppApprovalMapper;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.mapper.lessee.OpenLesseeMapper;
import com.wakedata.dw.open.model.PlatformUserPo;
import com.wakedata.dw.open.model.api.*;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import com.wakedata.dw.open.model.domain.DataAccessApprovalDo;
import com.wakedata.dw.open.model.lessee.OpenLesseePo;
import com.wakedata.dw.open.model.query.AuthApiListQuery;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.approval.ApprovalBusiness;
import com.wakedata.dw.open.service.approval.impl.adapt.ApprovalBusinessHolder;
import com.wakedata.dw.open.service.approval.status.ApprovalStatusHandlerHolder;
import com.wakedata.dw.open.service.approval.vo.ApprovalApiVO;
import com.wakedata.dw.open.service.approval.vo.ApprovalDetailVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalHistoryVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalRecordVO;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yiyufeng
 * @title AuthorityListReq
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
public class AppApprovalServiceImpl implements AppApprovalService {

    private static final String APPROVAL_FORMAT = "{\"Approvers\":\"{\\\"actApprover1\\\":\\\"%s\\\",\\\"actApprover2\\\":\\\"%s\\\"}\"}";
    private static final String TITLE_FORMAT = "%s (%s) 申请通过开放平台访问ads数据资产";

    @Autowired
    private AppApprovalMapper appApprovalMapper;
    @Autowired
    private DataAssetApiMapper dataAssetApiMapper;
    @Autowired
    private ApiConditionMapper apiConditionMapper;

    @Autowired
    private OpenLesseeMapper openLesseeMapper;

    @Autowired
    private ApprovalBusinessHolder approvalBusinessHolder;
    @Autowired
    private BatchCurdService<AppApprovalPo> batchCurdService;
    @Autowired
    private CurdService<AppApprovalPo> appApprovalCrudService;
    @Autowired
    private AppAccessService appAccessService;
    @Autowired
    private CurdService<DataAssetApiPo> dataAssetApiPoCurdService;
    @Autowired
    private ApprovalStatusHandlerHolder approvalStatusHandlerHolder;

    @Autowired
    private AppAccessRuleService appAccessRuleService;

    @Override
    public List<AppApprovalPo> createApproval(
            String userIdentification,
            ApprovalBusinessTypeEnum approvalBusinessType,
            Object approvalBody,
            String approvalBip) {
        List<AppApprovalPo> approvalList = approvalBusinessHolder
                .getApprovalBusinessImpl(approvalBusinessType)
                .analyzeApprovalBody(userIdentification, approvalBody);
        if (CollectionUtils.isEmpty(approvalList)) {
            log.warn("解析出来的请求单内容为空");
            throw new OpenException(MsgCodeEnum.w_data_approval_create_empty);
        }
        for (AppApprovalPo approval : approvalList) {
            approval.setApprovalBusinessType(approvalBusinessType);
            approval.setApprovalStatus(ApprovalStatusEnum.CREATED);
            approval.setUserIdentification(userIdentification);
            AuthUtils.setAppId(approval);
        }
        batchCurdService.insertList(approvalList, this.appApprovalMapper);
        return approvalList;
    }

    @Override
    public AppApprovalPo createApprovalApi(Integer dataAssetApiId, IpaasUserInfo userInfo) {
        // 前置验证：API不存在不允许发起申请、API未发布不允许、最后一次申请记录是否为通过状态，如果是则不允许申请
        DataAssetApiPo dataAssetApiPo = dataAssetApiPoCurdService.selectByPrimaryKey(dataAssetApiId, dataAssetApiMapper);
        if (dataAssetApiPo == null) {
            throw new OpenException(MsgCodeEnum.w_cannot_apply_not_exists_api);
        }
        if (DataAssetPublishStatusEnum.UN_PUBLISH.equals(dataAssetApiPo.getDataAssetPublishStatus())) {
            throw new OpenException(MsgCodeEnum.w_cannot_apply_not_published_api);
        }
        if (DataAssetEnums.PublicEnums.PRIVATE.equals(dataAssetApiPo.getSecret())) {
            throw new OpenException(OpenApiMsgCodeEnum.w_data_api_is_private_not_request_access);
        }
        this.checkApprovalApiRepeat(userInfo.getLesseeId(), dataAssetApiId);

        AppApprovalPo appApprovalPo = new AppApprovalPo();
        appApprovalPo.setUserIdentification(userInfo.getUserIdentification());
        appApprovalPo.setLesseeId(userInfo.getLesseeId());
        appApprovalPo.setApprovalBusinessType(ApprovalBusinessTypeEnum.API);
        appApprovalPo.setApprovalStatus(ApprovalStatusEnum.IN_APPROVAL);
        appApprovalPo.setDataAssetId(dataAssetApiId);
        appApprovalPo.setApprovalSubject(String.valueOf(dataAssetApiId));
        AppAccessRuleDo appAccessRuleDo = new AppAccessRuleDo();
        appAccessRuleDo.setDataAssetApiId(dataAssetApiId);
        appApprovalPo.setApprovalBody(JSON.toJSONString(appAccessRuleDo));
        Date date = new Date();
        appApprovalPo.setCreateTime(date);
        appApprovalPo.setUpdateTime(date);
        appApprovalPo.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE);
        appApprovalMapper.insert(appApprovalPo);
        return appApprovalPo;
    }

    @Override
    public PageResultDTO<List<ApprovalRecordVO>> approvalApiRecord(AppApprovalPo appApprovalPo, Integer pageNo, Integer pageSize, String keyword, IpaasUserInfo userInfo, String lesseeName) {
        PageResultDTO<List<ApprovalRecordVO>> resultDTO = new PageResultDTO<>();
        // 判断当前用户是否为管理员，如果不是管理员需要带上租户id查询
        if (!userInfo.getIsPlatformAdmin()) {
            appApprovalPo.setLesseeId(userInfo.getLesseeId());
        }
        appApprovalPo.setApprovalBusinessType(ApprovalBusinessTypeEnum.API);
        // 分页查询审批数据
        PageHelper.startPage(pageNo, pageSize);
        List<AppApprovalPo> approvalList = appApprovalMapper.selectPageApproveInfo(appApprovalPo, keyword, lesseeName);
        if (approvalList.isEmpty()) {
            return resultDTO;
        }
        PageInfo<AppApprovalPo> pageInfo = new PageInfo<>(approvalList);
        List<AppApprovalPo> list = pageInfo.getList();
        // 根据返回的数据查询api数据
        List<Integer> apiIds = list.stream().map(AppApprovalPo::getDataAssetId).collect(Collectors.toList());
        Map<Integer, DataAssetApiPo> apiMap = dataAssetApiMapper.findByIds(apiIds).stream().collect(Collectors.toMap(DataAssetApiPo::getDataAssetApiId, x -> x));
        // 根据租户id集合查询租户名称
        List<Long> tenantIdList = list.stream().map(AppApprovalPo::getLesseeId).collect(Collectors.toList());
        Example example = new Example(OpenLesseePo.class);
        example.createCriteria().andIn("id", tenantIdList).andEqualTo("isDeleted", 0);
        List<OpenLesseePo> lesseePoList = openLesseeMapper.selectByExample(example);
        Map<Long, String> lesseeMap;
        if (CollectionUtils.isEmpty(lesseePoList)) {
            lesseeMap = new HashMap<>(16);
        } else {
            lesseeMap = lesseePoList.stream().collect(Collectors.toMap(OpenLesseePo::getId, OpenLesseePo::getName));
        }
        // 组装返回数据列表
        List<ApprovalRecordVO> dataList = new ArrayList<>();
        for (AppApprovalPo approvalPo : list) {
            Integer apiId = approvalPo.getDataAssetId();
            Long lesseeId = approvalPo.getLesseeId();
            DataAssetApiPo dataAssetApiPo = apiMap.get(apiId);

            ApprovalRecordVO approvalRecordVO = new ApprovalRecordVO();
            approvalRecordVO.setApprovalId(approvalPo.getApprovalId());
            approvalRecordVO.setApiName(dataAssetApiPo != null ? dataAssetApiPo.getApiName() : null);
            approvalRecordVO.setApiPath(dataAssetApiPo != null ? dataAssetApiPo.getDataAssetApiMethod() : null);
            approvalRecordVO.setLesseeId(lesseeId);
            approvalRecordVO.setLesseeName(lesseeMap.getOrDefault(lesseeId, null));
            approvalRecordVO.setSubmitTime(approvalPo.getCreateTime());
            approvalRecordVO.setSubmitter(approvalPo.getUserIdentification());
            approvalRecordVO.setApprovalMessage(approvalPo.getApprovalMessage());
            approvalRecordVO.setApprovalStatus(approvalPo.getApprovalStatus().getValue());
            dataList.add(approvalRecordVO);
        }

        resultDTO.setData(dataList);
        resultDTO.setPageNo(pageNo);
        resultDTO.setPageSize(pageSize);
        resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
        return resultDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approvalApi(ApprovalApiVO approvalApiVO) {
        AppApprovalPo appApprovalPo = appApprovalMapper.findById(approvalApiVO.getApprovalId());
        if (appApprovalPo == null) {
            throw new OpenException(MsgCodeEnum.w_approval_not_exist);
        }
        ApprovalStatusEnum approvalStatusEnum = approvalApiVO.getAuditResults() ? ApprovalStatusEnum.APPROVAL : ApprovalStatusEnum.FAILURE_APPROVAL;
        appApprovalPo.setApprovalStatus(approvalStatusEnum);
        appApprovalPo.setApprovalMessage(approvalApiVO.getApprovalComments());

        // 将旧审核记录置为失效状态
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        if (Objects.isNull(userInfo)){
            throw new OpenException(MsgCodeEnum.w_approval_not_exist);
        }
        if (Objects.isNull(userInfo.getLesseeId())){
            throw new OpenException(MsgCodeEnum.w_user_not_tenantId);
        }
        if (Objects.isNull(appApprovalPo.getDataAssetId())){
            throw new OpenException(MsgCodeEnum.w_not_exists_api);
        }
        appApprovalMapper.disableApprovalRecord(userInfo.getLesseeId(),null,appApprovalPo.getDataAssetId());
        appApprovalPo.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE);

        appApprovalMapper.updateByPrimaryKey(appApprovalPo);
        return Boolean.TRUE;
    }

    @Override
    public AppApprovalPo createApproval(String userIdentification, ApprovalBusinessTypeEnum approvalBusinessType, Integer dataAssetId) {
        AppApprovalPo appApprovalPo = new AppApprovalPo();
        AuthUtils.setAppId(appApprovalPo);
        DataAssetApiPo dataAssetApiPo = dataAssetApiPoCurdService.selectByPrimaryKey(dataAssetId, dataAssetApiMapper);
        if (dataAssetApiPo == null) {
            throw new OpenException(MsgCodeEnum.w_cannot_apply_not_exists_api);
        }
        if (dataAssetApiPo.getDataAssetPublishStatus().equals(DataAssetPublishStatusEnum.UN_PUBLISH)) {
            throw new OpenException(MsgCodeEnum.w_cannot_apply_not_published_api);
        }
        appApprovalPo.setDataAssetId(dataAssetId);

        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setInCharge(userIdentification);
        List<AppAccessPo> accessAppPos = appAccessService.find(appAccessPo);
        if (CollectionUtils.isEmpty(accessAppPos)) {
            throw new OpenException(MsgCodeEnum.w_user_has_not_access_app);
        }
        Integer dataAccessAppId = accessAppPos.get(0).getDataAccessAppId();
        appApprovalPo.setAccessAppId(dataAccessAppId);
        appApprovalPo.setApprovalSubject(String.valueOf(dataAssetId));

        appApprovalPo.setApprovalBusinessType(approvalBusinessType);
        appApprovalPo.setApprovalStatus(ApprovalStatusEnum.CREATED);
        appApprovalPo.setUserIdentification(userIdentification);
        Date updateTime = new Date();
        appApprovalPo.setUpdateTime(updateTime);
        appApprovalPo.setCreateTime(updateTime);


        AppAccessRuleDo appAccessRuleDo = new AppAccessRuleDo();
        appAccessRuleDo.setDataAccessAppId(dataAccessAppId);
        appAccessRuleDo.setDataAssetFieldName(dataAssetApiPo.getDataAssetName());
        appAccessRuleDo.setDataAssetApiId(dataAssetId);
        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.RESULT);
        apiConditionPo.setDataAssetId(dataAssetId);

        List<ApiConditionPo> select = apiConditionMapper.select(apiConditionPo);
        if (!CollectionUtils.isEmpty(select)) {
            List<String> columns = new ArrayList<>(select.size());
            for (ApiConditionPo conditionPo : select) {
                columns.add(conditionPo.getAssetColumns());
            }
            appAccessRuleDo.setDataAccessRuleFieldList(columns);
        }
        appApprovalPo.setApprovalBody(JSON.toJSONString(appAccessRuleDo));
        appApprovalMapper.insert(appApprovalPo);
        return appApprovalPo;
    }

    private PlatformUserPo getEmpCode(String bip) {
        PlatformUserPo platformUserPo = new PlatformUserPo();
        platformUserPo.setZusrid(bip);
        List<PlatformUserPo> userPoList = null;
//        List<PlatformUserPo> userPoList = platformUserMapper.select(platformUserPo);

        if (CollectionUtils.isEmpty(userPoList)) {
            throw new OpenException(MsgCodeEnum.w_wrong_apply_user_info);
        }
        platformUserPo = userPoList.get(0);
        String zpernr = platformUserPo.getZpernr();
        if (StringUtils.isEmpty(zpernr)) {
            throw new OpenException(MsgCodeEnum.w_wrong_apply_user_info);
        }
        return platformUserPo;
    }

    @Deprecated
    @Override
    public Page<ApprovalHistoryVo> approvalHistory(AppApprovalPo appApprovalPo, Integer pageNo, Integer pageSize, String keyword) {
        ApprovalBusiness approvalBusiness = approvalBusinessHolder.getApprovalBusinessImpl(appApprovalPo.getApprovalBusinessType());
        Page<AppApprovalPo> approvalList = approvalBusiness.listVisibleApproval(appApprovalPo, pageNo, pageSize, keyword);
        // 处理申请记录
        Page<ApprovalHistoryVo> page = new Page();
        for (AppApprovalPo approval : approvalList) {
            ApprovalHistoryVo approvalHistoryVo = approvalBusiness.transformApprovalHistory(approval.getUserIdentification(), approval);
            page.add(approvalHistoryVo);
        }
        page.setTotal(approvalList.getTotal());
        return page;
    }


    @Override
    public ApprovalDetailVo approvalDetail(String userIdentification, Integer approvalId) {
        AppApprovalPo approval = this.appApprovalCrudService.selectByPrimaryKey(approvalId, this.appApprovalMapper);
        return approvalBusinessHolder.getApprovalBusinessImpl(approval.getApprovalBusinessType()).transformApprovalDetail(userIdentification, approval);
    }

    @Override
    public boolean approval(String userIdentification, Integer approvalId, String message, ApprovalStatusEnum statusEnum, String formUrl) {
        AppApprovalPo approval = this.appApprovalCrudService.selectByPrimaryKey(approvalId, this.appApprovalMapper);
        approval.setApprovalMessage(message);
        if (null == approval) {
            throw new OpenException(MsgCodeEnum.w_approval_not_exist);
        }
        if (ApprovalStatusEnum.APPROVAL == approval.getApprovalStatus()) {
            return true;
        }
        this.approvalStatusHandlerHolder.getApprovalStatusHandler(approval.getApprovalStatus()).approvalSuccess(approval);
        if (StringUtils.isNotBlank(formUrl)) {
            AppApprovalPo updateApproval = new AppApprovalPo();
            updateApproval.setApprovalId(approvalId);
            updateApproval.setFormUrl(formUrl);
            appApprovalMapper.updateByPrimaryKeySelective(updateApproval);
        }
        if (statusEnum == ApprovalStatusEnum.APPROVAL) {
            try {
                approvalBusinessHolder.getApprovalBusinessImpl(approval.getApprovalBusinessType()).handleApprovalSuccess(userIdentification, approval);
            } catch (OpenException e) {
                log.error("处理权限审批通过回调失败", e);
                this.appApprovalCrudService.deleteByPrimaryKey(approvalId, this.appApprovalMapper);
                return false;
            }
        } else if (ApprovalStatusEnum.FAILURE_APPROVAL == statusEnum) {
            this.approvalBusinessHolder.getApprovalBusinessImpl(approval.getApprovalBusinessType()).handleApprovalFail(userIdentification, approval);
        } else if (ApprovalStatusEnum.IN_APPROVAL == statusEnum) {
            AppApprovalPo appApprovalPo = new AppApprovalPo();
            appApprovalPo.setApprovalId(approvalId);
            appApprovalPo.setUpdateTime(new Date());
            appApprovalPo.setApprovalStatus(ApprovalStatusEnum.IN_APPROVAL);
            appApprovalMapper.updateByPrimaryKeySelective(appApprovalPo);
        }
        return true;
    }

    @Override
    public boolean approvalCallback(Integer approvalId, Integer status, Date approvalDate, String formUrl) {
        switch (status) {
            case 2:
                approval(null, approvalId, "k2已审批，请进入k2查看", ApprovalStatusEnum.APPROVAL, formUrl);
                break;
            default:
                approval(null, approvalId, "k2已审批，请进入k2查看", ApprovalStatusEnum.IN_APPROVAL, formUrl);
        }
        return true;
    }

    @Override
    public boolean deleteApproval(Integer approvalId) {
        AppApprovalPo appApprovalPo = appApprovalMapper.selectByPrimaryKey(approvalId);
        if (appApprovalPo == null) {
            throw new OpenException(MsgCodeEnum.w_approval_not_exist);
        }
        appAccessRuleService.delete(appApprovalPo.getDataAssetId(), appApprovalPo.getAccessAppId());
        appApprovalMapper.deleteByPrimaryKey(approvalId);
        return true;
    }

    @Override
    public Page<DataAccessApprovalDo> getApprovalPoByAssetId(Integer dataAssetId, PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        Page<DataAccessApprovalDo> dataAccessApprovalDos = (Page<DataAccessApprovalDo>) appApprovalMapper.selectApprovalInfo(dataAssetId);
        return dataAccessApprovalDos;
    }

    @Override
    public void deleteApproveByAppId(Integer appId) {
        AppApprovalPo po = new AppApprovalPo();
        po.setAccessAppId(appId);
        this.appApprovalCrudService.delete(po, this.appApprovalMapper);
    }

    @Override
    public List<Map> queryCollectInfo(List<Integer> collect, String userIdentification) {
        return Optional.ofNullable(this.appApprovalMapper.queryCollect(collect, userIdentification)).orElse(new ArrayList<>());
    }

    @Override
    public Page<AuthApiVO> selectAuthApiListByPage(AuthApiListQuery authApiListQuery, PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        Page<AuthApiVO> authApiVOPage = (Page<AuthApiVO>) appApprovalMapper.selectAuthApiVOList(authApiListQuery);
        return authApiVOPage;
    }

    @Override
    public Page<AuthApiVO> selectAuthApiListWithAppAuthStatus(Long lesseeId, Integer appId, PageQuery pageQuery) {

        if (Objects.isNull(lesseeId)){
            throw new OpenException(MsgCodeEnum.w_user_not_tenantId);
        }
        if (Objects.isNull(appId)){
            throw new OpenException(MsgCodeEnum.w_data_app_id_not_found);
        }
        // 构建查询条件
        AuthApiListQuery authApiListQuery = getAuthApiListQuery(lesseeId);

        Page<AuthApiVO> authApiVOListPage = this.selectAuthApiListByPage(authApiListQuery,pageQuery);
        if (CollectionUtils.isEmpty(authApiVOListPage)){
            return authApiVOListPage;
        }

        List<AuthApiVO> authorizedByLessessList = authApiVOListPage.getResult();
        if (CollectionUtils.isEmpty(authorizedByLessessList)){
            return authApiVOListPage;
        }

        // 查询该应用下最新的审批记录 如果相同的api并且已授权，则设置授权状态
        AppApprovalPo appApprovalPo = new AppApprovalPo();
        appApprovalPo.setLesseeId(lesseeId);
        appApprovalPo.setAccessAppId(appId);
        appApprovalPo.setApprovalBusinessType(ApprovalBusinessTypeEnum.APP);
        appApprovalPo.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE);
        List<AppApprovalPo> allAppApprovalPoList = appApprovalMapper.select(appApprovalPo);

        if (CollectionUtils.isEmpty(allAppApprovalPoList)){
            return authApiVOListPage;
        }

        Map<Integer, List<AppApprovalPo>> apiIdAndAppApproval = allAppApprovalPoList.stream().collect(Collectors.groupingBy(AppApprovalPo::getDataAssetId));
        if (Objects.isNull(apiIdAndAppApproval)){
            return authApiVOListPage;
        }

        authorizedByLessessList.forEach(authorizedByLessess->{
            String dataAssetApiId = authorizedByLessess.getDataAssetApiId();
            if (StringUtils.isBlank(dataAssetApiId)){
                return;
            }
            List<AppApprovalPo> appApprovalPoList = apiIdAndAppApproval.get(Integer.valueOf(dataAssetApiId));
            if (CollectionUtils.isEmpty(appApprovalPoList)){
                return;
            }

            AppApprovalPo latestAppApproval = appApprovalPoList.stream().sorted(Comparator.comparing(AppApprovalPo::getApprovalId).reversed()).findFirst().get();
            if (latestAppApproval.getAppAuthStatus().equals(DataAssetEnums.AppAuthStatus.AUTHORIZED)){
                authorizedByLessess.setAppAuthStatus(DataAssetEnums.AppAuthStatus.AUTHORIZED.getValue());
            }
        });

        return authApiVOListPage;
    }

    /**
     * 查询该租户下，api类型，已经审批通过，api已经发布的，有效的 记录
     * @param lesseeId 租户id
     * @return
     */
    private AuthApiListQuery getAuthApiListQuery(Long lesseeId) {
        AuthApiListQuery authApiListQuery = new AuthApiListQuery();
        authApiListQuery.setLesseeId(lesseeId);
        authApiListQuery.setAppId(null);
        authApiListQuery.setApprovalBusinessType(ApprovalBusinessTypeEnum.API.getValue());
        authApiListQuery.setApprovalStatus(ApprovalStatusEnum.APPROVAL.getValue());
        authApiListQuery.setAppAuthStatus(null);
        authApiListQuery.setApiId(null);
        authApiListQuery.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE.getValue());
        authApiListQuery.setDataAssetPublishStatus(DataAssetPublishStatusEnum.PUBLISH.getValue());
        authApiListQuery.setSecret(DataAssetEnums.PublicEnums.PUBLIC.getValue());
        return authApiListQuery;
    }

    private AuthApiListQuery getAuthAppCountQuery(Long lesseeId,Integer appId, Integer apiId) {
        AuthApiListQuery query = new AuthApiListQuery();
        query.setLesseeId(lesseeId);
        query.setAppId(appId);
        query.setApprovalBusinessType(ApprovalBusinessTypeEnum.APP.getValue());
        query.setApprovalStatus(ApprovalStatusEnum.APPROVAL.getValue());
        query.setAppAuthStatus(DataAssetEnums.AppAuthStatus.AUTHORIZED.getValue());
        query.setApiId(apiId);
        query.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE.getValue());
        query.setDataAssetPublishStatus(DataAssetPublishStatusEnum.PUBLISH.getValue());
        return query;
    }

    private AuthApiListQuery getAuthApiCountQuery(Integer appId, Long lesseeId) {
        AuthApiListQuery query = new AuthApiListQuery();
        query.setLesseeId(lesseeId);
        query.setAppId(appId);
        query.setApprovalBusinessType(ApprovalBusinessTypeEnum.APP.getValue());
        query.setApprovalStatus(ApprovalStatusEnum.APPROVAL.getValue());
        query.setAppAuthStatus(DataAssetEnums.AppAuthStatus.AUTHORIZED.getValue());
        query.setApiId(null);
        query.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE.getValue());
        query.setDataAssetPublishStatus(DataAssetPublishStatusEnum.PUBLISH.getValue());
        return query;
    }

    private AuthApiListQuery getAppAndAuthApiCountMapQuery(Long lesseeId) {
        AuthApiListQuery query = new AuthApiListQuery();
        query.setLesseeId(lesseeId);
        query.setAppId(null);
        query.setApprovalBusinessType(ApprovalBusinessTypeEnum.APP.getValue());
        query.setApprovalStatus(ApprovalStatusEnum.APPROVAL.getValue());
        query.setAppAuthStatus(DataAssetEnums.AppAuthStatus.AUTHORIZED.getValue());
        query.setApiId(null);
        query.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE.getValue());
        query.setDataAssetPublishStatus(DataAssetPublishStatusEnum.PUBLISH.getValue());
        return query;
    }

    @Override
    public AppAuthApiCountVo selectAuthApiCount(Long lesseeId, Integer appId) {

        AuthApiListQuery authApiListQuery = getAuthApiListQuery(lesseeId);
        Long totalCount = appApprovalMapper.selectAuthCount(authApiListQuery).longValue();
        Long lesseeApiCount = this.selectLesseeApiCount(lesseeId,null,null,null);

        AuthApiListQuery authApiCountQuery = getAuthApiCountQuery(appId, lesseeId);
        Long authedApiCount = appApprovalMapper.selectAuthCount(authApiCountQuery).longValue();

        totalCount = totalCount + lesseeApiCount;

        Long unAuthApiCount = totalCount - authedApiCount;
        return new AppAuthApiCountVo(totalCount,authedApiCount,unAuthApiCount);
    }

    /**
     * 获取租户自建授权api数量
     * @param lesseeId 租户id
     * @param appId 应用id
     * @param apiId apiId
     * @param appAuthStatus 授权状态
     * @return long
     */
    private Long selectLesseeApiCount(Long lesseeId,Integer appId,Integer apiId,Integer appAuthStatus){
        AuthApiListQuery authApiListQuery = new AuthApiListQuery();
        authApiListQuery.setLesseeId(lesseeId);
        authApiListQuery.setAppId(appId);
        authApiListQuery.setApprovalBusinessType(ApprovalBusinessTypeEnum.APP.getValue());
        authApiListQuery.setAppAuthStatus(appAuthStatus);
        authApiListQuery.setApiId(apiId);
        authApiListQuery.setDataAssetPublishStatus(DataAssetPublishStatusEnum.PUBLISH.getValue());
        authApiListQuery.setSecret(DataAssetEnums.PublicEnums.PUBLIC.getValue());
        return ObjectUtil.defaultIfNull(appApprovalMapper.selectLesseeApiCount(authApiListQuery), 0L);
    }

    @Override
    public Integer selectAuthAppCount(Long lesseeId, Integer apiId) {
        AuthApiListQuery authAppCountQuery = getAuthAppCountQuery(lesseeId,null, apiId);
        return appApprovalMapper.selectAuthCount(authAppCountQuery);
    }

    @Override
    public Integer selectAuthAppCountByAppId(Long lesseeId, Integer appId, Integer apiId) {
        AuthApiListQuery authAppCountQuery = getAuthAppCountQuery(lesseeId,appId, apiId);
        return appApprovalMapper.selectAuthCount(authAppCountQuery);
    }

    @Override
    public ImmutableMap<Integer, AppIdAndAuthApiCountVo> selectAppIdAndAuthApiCountMap(Long lesseeId) {
        AuthApiListQuery query = getAppAndAuthApiCountMapQuery(lesseeId);
        List<AppIdAndAuthApiCountVo> appIdAndAuthApiCountVos = appApprovalMapper.selectAuthCountGroupByAppId(query);
        ImmutableMap<Integer, AppIdAndAuthApiCountVo> integerAppIdAndAuthApiCountVoImmutableMap = Maps.uniqueIndex(appIdAndAuthApiCountVos, AppIdAndAuthApiCountVo::getAppId);
        return integerAppIdAndAuthApiCountVoImmutableMap;
    }

    @Override
    public Boolean appAuthApi(Long lessessId, Integer appId, Integer apiId) {
        // 根据apiId查询出api信息，判断api是否加密状态
        DataAssetApiPo dataAssetApiPo = dataAssetApiMapper.selectByPrimaryKey(apiId);
        if (ObjectUtil.isEmpty(dataAssetApiPo)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_data_api_not_found);
        }
        if (DataAssetEnums.PublicEnums.PRIVATE.equals(dataAssetApiPo.getSecret())) {
            throw new OpenException(OpenApiMsgCodeEnum.w_data_api_is_private_not_authorization_app);
        }
        saveAppAuthApi(lessessId, appId, apiId, DataAssetEnums.AppAuthStatus.AUTHORIZED, "授权");
        return true;
    }

    @Override
    public Boolean appDeauthApi(Long lessessId, Integer appId, Integer apiId) {
        saveAppAuthApi(lessessId, appId, apiId, DataAssetEnums.AppAuthStatus.UNAUTHORIZED, "解除授权");
        return true;
    }

    @Override
    public Boolean updateActiveStatusById(Integer dataAssetApiId) {
        AppApprovalPo appApprovalPo = new AppApprovalPo();
        appApprovalPo.setDataAssetId(dataAssetApiId);
        appApprovalPo.setActiveStatus(DataAssetEnums.ActiveStatus.DISABLE);
        return appApprovalMapper.updateActiveStatusById(appApprovalPo);
    }

    @Override
    public Boolean updateActiveStatusByIds(List<Integer> dataAssetApiIds, DataAssetEnums.ActiveStatus activeStatus) {
        return appApprovalMapper.batchUpdateActiveStatusById(dataAssetApiIds, activeStatus.getValue());
    }


    @Transactional(rollbackFor = Exception.class)
    void saveAppAuthApi(Long lessessId, Integer appId, Integer apiId, DataAssetEnums.AppAuthStatus appAuthStatus, String describe) {

        if (Objects.isNull(lessessId)){
            throw new OpenException(MsgCodeEnum.w_user_not_tenantId);
        }
        if (Objects.isNull(appId)){
            throw new OpenException(MsgCodeEnum.w_data_approval_app_not_exist);
        }
        if (Objects.isNull(apiId)){
            throw new OpenException(MsgCodeEnum.w_data_approval_api_not_exist);
        }

        // 对于同一个租户id，同一个app id和api id ,只能有一个有效状态的记录
        appApprovalMapper.disableApprovalRecord(lessessId,appId,apiId);
        AppApprovalPo appApprovalPo = new AppApprovalPo();
        appApprovalPo.setLesseeId(lessessId);
        appApprovalPo.setAccessAppId(appId);
        appApprovalPo.setDataAssetId(apiId);
        appApprovalPo.setApprovalSubject(apiId.toString());
        appApprovalPo.setApprovalBusinessType(ApprovalBusinessTypeEnum.APP);
        appApprovalPo.setApprovalStatus(ApprovalStatusEnum.APPROVAL);
        appApprovalPo.setAppAuthStatus(appAuthStatus);
        appApprovalPo.setCreateTime(new Date());
        appApprovalPo.setUpdateTime(new Date());
        appApprovalPo.setUserIdentification(IpaasUserContext.getUserInfo().getUserIdentification());
        appApprovalPo.setApprovalBody(describe);
        appApprovalPo.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE);
        appApprovalMapper.insert(appApprovalPo);
    }

    /**
     * 查询当前租户最后一次API审核记录是否为通过状态，如果为审批中或通过状态则抛异常不允许重复申请
     *
     * @param lesseeId       租户id
     * @param dataAssetApiId API ID
     */
    private void checkApprovalApiRepeat(Long lesseeId, Integer dataAssetApiId) {
        AppApprovalPo appApprovalPo = new AppApprovalPo();
        appApprovalPo.setApprovalBusinessType(ApprovalBusinessTypeEnum.API);
        appApprovalPo.setDataAssetId(dataAssetApiId);
        appApprovalPo.setLesseeId(lesseeId);
        appApprovalPo.setActiveStatus(DataAssetEnums.ActiveStatus.ACTIVE);
        AppApprovalPo lastApiApprove = appApprovalMapper.findLastApiApproveByLesseeId(appApprovalPo);
        if (lastApiApprove == null) {
            return;
        }
        ApprovalStatusEnum approvalStatus = lastApiApprove.getApprovalStatus();
        if (ApprovalStatusEnum.IN_APPROVAL == approvalStatus || ApprovalStatusEnum.APPROVAL == approvalStatus) {
            throw new OpenException(MsgCodeEnum.w_api_create_approval_repeat);
        }
    }

}
