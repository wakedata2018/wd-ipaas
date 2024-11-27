package com.wakedata.dw.open.service.impl.api;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.lowcode.model.AppPo;
import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.lowcode.service.LowCodeAccountService;
import com.wakedata.dw.lowcode.service.event.AppCreatedEvent;
import com.wakedata.dw.lowcode.service.event.AppUpdatedEvent;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.gateway.OpenApiAuthHandler;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.mapper.api.AppAccessRuleMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorAuthConfigMapper;
import com.wakedata.dw.open.model.api.*;
import com.wakedata.dw.open.model.connector.ConnectorAuthConfigPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.AppCountDo;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.FavoriteService;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.approval.vo.AppAccessVo;
import com.wakedata.dw.open.service.connector.ConnectorAuthConfigService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.utils.OpenJsonWebTokenContext;
import com.wakedata.dw.open.service.vo.AppAccessRuleVo;
import com.wakedata.dw.open.utils.UUIDUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.openapi.service.DwOpenAccessAuthService;
import com.wakedata.dw.platform.tools.jwt.JsonWebTokenUtils;
import com.wakedata.wd.app.client.app.api.AppRpcService;
import com.wakedata.wd.app.client.app.dto.AppBaseInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
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
public class AppAccessServiceImpl extends BaseServiceImpl<AppAccessPo, AppAccessMapper> implements AppAccessService {
    @Autowired
    private BatchCurdService<AppAccessPo> batchCurdService;
    @Autowired
    private AppApprovalService appApprovalService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Autowired
    private AppAccessRuleMapper appAccessRuleMapper;
    @Autowired
    private LowCodeAccountService lowCodeAccountService;
    @Resource
    private AppRpcService appRpcService;
    @Resource
    private DwOpenAccessAuthService dwOpenAccessAuthService;
    @Resource
    private AccessTokenService accessTokenService;
    @Resource
    private OpenApiAuthHandler openApiAuthHandler;
    @Resource
    private ConnectorAuthConfigMapper connectorAuthConfigMapper;

    @Autowired
    @Override
    protected void init(CurdService<AppAccessPo> curdService, AppAccessMapper mapper) {
        super.set(curdService, mapper);

    }

    @Override
    public List<AppAccessPo> selectByIdList(List<Integer> list) {
        return this.batchCurdService.selectByIdList(list, this.getMapper());
    }
//

    @Override
    public AppAccessPo createAccessApp(AppAccessPo appAccess) {
        verifyAppAccess(appAccess, false);

        if (DataAssetEnums.DataAccessAppEnums.PASS.equals(appAccess.getStatus())) {
            appAccess.setDataAccessKey(this.generateKey());
            appAccess.setDataAccessSecret(this.generateSecret());
        } else {
            appAccess.setStatus(DataAssetEnums.DataAccessAppEnums.CREATED);
        }
        this.getCurdService().insertSelective(appAccess, this.getMapper());

        //自动生成应用调用前缀
        appAccess.setDataAccessPrefix(UUIDUtils.generateAppPrefix(appAccess.getDataAccessAppId(),appAccess.getLesseeId()));

        updateAppPrefix(appAccess);

        //发布app创建事件
        eventPublisher.publishEvent(new AppCreatedEvent(this, appAccess));
        return appAccess;
    }

    /**
     * 更新应用前缀
     * @param appAccess
     */
    private void updateAppPrefix(AppAccessPo appAccess) {
        AppAccessPo po = new AppAccessPo();
        po.setDataAccessAppId(appAccess.getDataAccessAppId());
        po.setDataAccessPrefix(appAccess.getDataAccessPrefix());
        this.getCurdService().updateByPrimaryKeySelective(po,this.getMapper());
    }

    @Override
    public List<AppAccessPo> searchAccessApp(AppAccessPo appAccess) {
        try {
            log.info("根据条件获取数据库数据 {}", appAccess.toString());
            return this.getMapper().selectWithOutSecret(appAccess);
        } catch (Exception e) {
            log.error("根据条件获取数据库数据发生错误 param {}", appAccess, e);
            throw new OpenException("获取数据库数据发生错误", e);
        }
    }

    @Override
    public AppAccessListVo searchAccessAppWithDefault(AppAccessPo appAccess, Integer apiId) {
        try {
            log.info("根据条件获取数据库数据 {}", appAccess.toString());
            List<AppAccessPo> appAccessPoList = this.getMapper().selectWithOutSecret(appAccess);

            DataAssetApiPo dataAssetApi = dataAssetApiService.detail(apiId);
            AppAccessRuleVo appAccessRule = new AppAccessRuleVo();
            appAccessRule.setApiId(apiId);

            if (DataAssetPublishStatusEnum.UN_PUBLISH.equals(dataAssetApi.getDataAssetPublishStatus())) {
                return null;
            }

            List<Integer> idList = new ArrayList<>();
            idList.add(apiId);

            //接口未使用，先注释
//            if (dataAssetApi.getApiType() == DataAssetEnums.DataApiType.LITE_FLOW) {
//                ApiFlowAttr apiFlowAttr = (ApiFlowAttr) dataAssetApi.getApiAttr();
//                DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);
//                List<ApiOperator> apiOperators = operatorContainer.filterApiOperator();
//                for (ApiOperator apiOperator : apiOperators) {
//                    ApiComponent component = apiOperator.getComponent();
//                    idList.add(component.getDataAssetApi().getDataAssetApiId());
//                }
//            }
//
            List<Integer> defaultAppIdList = appAccessRuleMapper.searchAccessAppIdWithAuth(idList);
            log.info("API：{}，相关的授权APP id：{}", apiId, defaultAppIdList);

            AppAccessListVo appAccessListVo = new AppAccessListVo();
            appAccessListVo.setAppAccessVoList(appAccessPoList);
            appAccessListVo.setDefaultAppIdList(defaultAppIdList);

            return appAccessListVo;
        } catch (Exception e) {
            log.error("根据条件获取数据库数据发生错误 param {}", appAccess, e);
            throw new OpenException("获取数据库数据发生错误", e);
        }
    }

    @Override
    public AppAccessPo getAppAccess(String appId) {
        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setDataAccessAppId(Integer.valueOf(appId));
        List<AppAccessPo> select = this.getCurdService().select(appAccessPo, getMapper());
        if (CollectionUtils.isEmpty(select)) {
            throw new OpenException("应用不存在!");
        }
        return select.iterator().next();
    }

    private void verifyAppAccess(AppAccessPo appAccess, boolean isEdit) {
        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setDataAccessAppName(appAccess.getDataAccessAppName());
        AuthUtils.setAppId(appAccessPo);

        // 同名判断
        List<AppAccessPo> exitsAppAccess = this.getCurdService().select(appAccessPo, getMapper());
        if (CollectionUtils.isNotEmpty(exitsAppAccess)) {

            Map<Integer, AppAccessPo> map = exitsAppAccess
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    AppAccessPo::getDataAccessAppId,
                                    internalAppAccess -> internalAppAccess
                            )
                    );

            if (isEdit) {
                map.remove(appAccess.getDataAccessAppId());
            }

            if (MapUtils.isNotEmpty(map)) {
                throw new OpenException(MsgCodeEnum.w_app_name_exists);
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean editAccessApp(AppAccessPo appAccess) {
        verifyAppAccess(appAccess, true);
        int effectRow = this.getCurdService().updateByPrimaryKeySelective(appAccess, this.getMapper());
        if (0 == effectRow) {
            log.error("更新数据接入失败，找不到更新的数据");
            throw new OpenException(MsgCodeEnum.w_data_app_id_not_found);
        }
        AppAccessPo appAccessPo = this.getCurdService().selectByPrimaryKey(appAccess.getDataAccessAppId(), this.getMapper());
        if(StringUtils.isBlank(appAccessPo.getDataAccessPrefix())){
            appAccessPo.setDataAccessPrefix(UUIDUtils.generateAppPrefix(appAccessPo.getDataAccessAppId(), appAccessPo.getLesseeId()));
            updateAppPrefix(appAccessPo);
        }
        //发布app创建事件
        eventPublisher.publishEvent(new AppUpdatedEvent(this, appAccess));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAccessApp(Integer dataAccessAppId) {
        int effectRow = this.getCurdService().deleteByPrimaryKey(dataAccessAppId, this.getMapper());
        if (0 == effectRow) {
            log.error("删除数据接入APP失败，找不到删除的记录");
            throw new OpenException(MsgCodeEnum.w_data_app_id_not_found);
        }
        return true;
    }

    @Override
    public AppAccessPo resetAccessAppSecret(Integer dataAccessAppId) {
        AppAccessPo dataAccessApp = new AppAccessPo();
        dataAccessApp.setDataAccessAppId(dataAccessAppId);
        dataAccessApp.setDataAccessSecret(this.generateSecret());
        dataAccessApp.setUpdateTime(new Date());

        int effectRow = this.getCurdService().updateByPrimaryKeySelective(dataAccessApp, this.getMapper());
        if (0 == effectRow) {
            log.error("重置数据接入APP密钥失败，找不到主键对应的记录");
            throw new OpenException(MsgCodeEnum.w_data_app_id_not_found);
        }
        AppAccessPo appAccessPo = this.getCurdService().selectByPrimaryKey(dataAccessAppId, this.getMapper());
        //清除token
        accessTokenService.cleanToken(appAccessPo.getLesseeId(),appAccessPo.getDataAccessAppId());
        return appAccessPo;
    }


    @Override
    public AppAccessPo approvalAccessApp(AppAccessPo appAccess) {
        String approvalMessage = appAccess.getApprovalMessage();
        if (approvalMessage == null || approvalMessage.length() > 255) {
            throw new OpenException(MsgCodeEnum.w_approval_message_neither_empty_nor_beyond_max_length);
        }
        if (DataAssetEnums.DataAccessAppEnums.PASS.equals(appAccess.getStatus())) {
            appAccess.setDataAccessSecret(this.generateSecret());
            appAccess.setDataAccessKey(generateKey());
        }
        getCurdService().updateByPrimaryKeySelective(appAccess, getMapper());
        return appAccess;
    }

    @Override
    public List<AppCountDo> queryApiReference(List<Integer> collect) {
        return Optional.ofNullable(this.getMapper().countApiByList(collect)).orElse(new ArrayList<>());
    }

    @Override
    public List<ApiInfoDo> getAppReferenceApi(Integer appId) {
        return Optional.ofNullable(this.getMapper().getAppReferenceApi(appId)).orElse(new ArrayList<>());
    }

    @Override
    public Date getAppAuthDate(Integer appId, Integer apiId) {
        return this.getMapper().getAppAuthDate(appId, apiId);
    }

    @Override
    public List<AppAccessPo> listPermissionApp(Integer apiId) {
        return this.getMapper().listPermissionApp(apiId);
    }

    @Override
    public String getToken(String appId, String appSecret, String apiPath, Date createTime) {
        return createTokenInternal(appId, appSecret, normalization(apiPath), createTime);
    }

    @Override
    public String normalization(String apiPath) {
        if (!apiPath.startsWith("/")) {
            apiPath = "/" + apiPath;
        }
        return apiPath;
    }

    @Override
    public boolean verifyToken(String token, AppAccessPo appAccess, String apiPath) {
        try {
            OpenJsonWebTokenContext context = JsonWebTokenUtils.verifyToken(appAccess.getDataAccessSecret(), token, OpenJsonWebTokenContext.class);
            if (!context.getAppId().equals(appAccess.getDataAccessKey())) {
                log.error("{} {} token 与 appId不一致", appAccess.getDataAccessKey(), token);
                return false;
            }

            if (System.currentTimeMillis() > context.getExpireTime()) {
                log.error("{} {} token已过期!", appAccess.getDataAccessKey(), DateFormatUtils.format(context.getExpireTime(), "yyyy-MM-dd HH:mm:ss"));
                return false;
            }

            if (!normalization(apiPath).equals(context.getApiPath())) {
                log.error("{} {} {} api path不匹配!", appAccess.getDataAccessKey(), context.getApiPath(), apiPath);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    private String createTokenInternal(String appId, String appSecret, String apiPath, Date issuedAt) {
        OpenJsonWebTokenContext context = new OpenJsonWebTokenContext();
        context.setAppId(appId);
        context.setExpireTime(issuedAt.getTime());
        context.setApiPath(apiPath);
        String token = JsonWebTokenUtils.createToken(appSecret, context);
        return token;
    }

    private String generateKey() {
        return UUIDUtils.generateSimpleUUID();
    }

    private String generateSecret() {
        return UUIDUtils.generateSimpleUUID();
    }

    @Override
    public AppAccessVO appDetail(Integer dataAccessAppId) {
        AppAccessPo appAccessPo = this.getCurdService().selectByPrimaryKey(dataAccessAppId,getMapper());
        if (Objects.isNull(appAccessPo)){
            return new AppAccessVO();
        }

        AppAccessVO appAccessVO = BeanUtil.copy(appAccessPo, AppAccessVO.class);
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        if(ObjectUtil.isNotNull(appAccessVO.getConnectorAuthId())){
            ConnectorAuthConfigPo authConfigPo = connectorAuthConfigMapper.selectByPrimaryKey(appAccessVO.getConnectorAuthId());
            if(authConfigPo != null){
                appAccessVO.setConnectorAuthName(authConfigPo.getAuthName());
            }
        }
        // 设置已授权和未授权的api数量
        AppAuthApiCountVo appAuthApiCountVo = appApprovalService.selectAuthApiCount(lesseeId,dataAccessAppId);
        appAccessVO.setAuthedApiCount(appAuthApiCountVo.getAuthedApiCountl());
        appAccessVO.setUnAuthApiCount(appAuthApiCountVo.getUnAuthApiCount());

        ResultDTO<String> resultDTO = dwOpenAccessAuthService.getAuthConfigByAppId(dataAccessAppId);
        String authConfigJson = resultDTO.getData();
        if (StringUtils.isNotBlank(authConfigJson)){
            AppBaseInfoDTO appBaseInfoDTO = new Gson().fromJson(authConfigJson, AppBaseInfoDTO.class);
            if (Objects.nonNull(appBaseInfoDTO)){
                appAccessVO.setAuthorizedAppName(appBaseInfoDTO.getAppName());
            }
        }
        appAccessVO.setPrefixLesseeId(UUIDUtils.generateLesseeIdEncode(appAccessVO.getLesseeId()));
        //拆分应用访问前缀
        if(StringUtils.isNotBlank(appAccessVO.getDataAccessPrefix())) {
            appAccessVO.setPrefixCustom(appAccessVO.getDataAccessPrefix().replace(appAccessVO.getPrefixLesseeId(),""));
        }
        appAccessVO.setApiAccessPrefix(openApiAuthHandler.getAppApiPrefix(appAccessVO.getDataAccessPrefix()));
        return appAccessVO;
    }

    @Override
    public Boolean updateLine(Integer dataAccessAppId, Integer status) {

        AppAccessPo appAccessPo = this.getCurdService().selectByPrimaryKey(dataAccessAppId, getMapper());
        if (Objects.isNull(appAccessPo) || Objects.isNull(appAccessPo.getPublishStatus())){
            throw new OpenException("该应用不存在");
        }

        AppAccessPo updatePo = new AppAccessPo();
        updatePo.setDataAccessAppId(dataAccessAppId);

        if (Objects.equals(status,DataAssetEnums.DataAccessPublishStatus.ON_LINE.getValue())){
            updatePo.setPublishStatus(DataAssetEnums.DataAccessPublishStatus.ON_LINE);
        }
        if (Objects.equals(status,DataAssetEnums.DataAccessPublishStatus.OFF_LINE.getValue())){
            updatePo.setPublishStatus(DataAssetEnums.DataAccessPublishStatus.OFF_LINE);
        }

        int update = this.getCurdService().updateByPrimaryKeySelective(updatePo,getMapper());
        if (update < 0){
            throw new OpenException("应用更新在线状态失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 应用前缀是否重复
     * @param prefix
     * @param id
     * @return
     */
    public boolean checkAppPrefixIsRepeat(String prefix,Integer id) {
        Example example = new Example(AppAccessPo.class);
        example.createCriteria().andEqualTo("dataAccessPrefix",prefix).andNotEqualTo("dataAccessAppId",id);
        int count = this.getMapper().selectCountByExample(example);
        return count > 0;
    }

    @Override
    public PageResultDTO<List<AppAccessVo>> pageList(Long lesseeId, AppAccessPo appAccessPo, int pageNo, int pageSize) {
        PageResultDTO<List<AppAccessVo>> resultDTO = new PageResultDTO<>();
        PageHelper.startPage(pageNo, pageSize);
        List<AppAccessPo> appAccessPoList = this.getMapper().pageList(appAccessPo);
        if (appAccessPoList.isEmpty()){
            return resultDTO;
        }
        PageInfo<AppAccessPo> pageInfo = new PageInfo<>(appAccessPoList);
        List<AppAccessPo> data = pageInfo.getList();
        if (CollectionUtils.isNotEmpty(data)) {
            ImmutableMap<Integer, AppIdAndAuthApiCountVo> integerAppIdAndAuthApiCountVoImmutableMap = appApprovalService.selectAppIdAndAuthApiCountMap(lesseeId);
            if (null != integerAppIdAndAuthApiCountVoImmutableMap){
                //求接入在多少个api里面授权了
                data.forEach(po -> {
                    po.setPrefixLesseeId(UUIDUtils.generateLesseeIdEncode(po.getLesseeId()));
                    //拆分应用访问前缀
                    if(StringUtils.isNotBlank(po.getDataAccessPrefix())) {
                        po.setPrefixCustom(po.getDataAccessPrefix().replace(po.getPrefixLesseeId(),""));
                    }
                    po.setApiAccessPrefix(openApiAuthHandler.getAppApiPrefix(po.getDataAccessPrefix()));

                    po.setApiNum(0);
                    if (null == po.getDataAccessAppId()){
                        return;
                    }
                    AppIdAndAuthApiCountVo appIdAndAuthApiCountVo = integerAppIdAndAuthApiCountVoImmutableMap.get(po.getDataAccessAppId());
                    if (null == appIdAndAuthApiCountVo){
                        return;
                    }
                    Integer authApiCount = appIdAndAuthApiCountVo.getAuthApiCount();
                    if (null == authApiCount){
                        return;
                    }
                    po.setApiNum(authApiCount);
                });
            }
            //设置低代码绑定低账号信息
            setLowCodeAccountPo(data);
        }

        List<AppAccessVo> dataList = BeanUtil.copyList(data, AppAccessVo.class);
        resultDTO.setData(dataList);
        resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
        for (AppAccessVo datum : dataList) {
            datum.setDataAccessSecret(null);
        }
        return resultDTO;
    }

    @Override
    public ResultDTO<List<AppBaseInfoDTO>> queryAppInfo(Long tenantId) {
        List<AppBaseInfoDTO> appBaseInfoDTOList = new ArrayList<>();
        if(ObjectUtil.isNull(tenantId) || tenantId == 0L){
            return ResultDTO.success(appBaseInfoDTOList);
        }
        try {
            com.wakedata.common.core.dto.ResultDTO<List<AppBaseInfoDTO>> result = appRpcService.queryAppBaseInfoByTenantId(tenantId);
            if (null == result){
                log.error(this.getClass().getName()+":queryAppInfo, result is null");
                return ResultDTO.success(appBaseInfoDTOList);
            }
            if (!result.isSuccess()){
                log.error(this.getClass().getName()+":queryAppInfo failed result is :%s",new Gson().toJson(result));
                return ResultDTO.success(appBaseInfoDTOList);
            }
            appBaseInfoDTOList = result.getData();
        }catch (Exception e){
            log.error(String.format(this.getClass().getName()+":queryAppInfo exception , error info：%s",e));
        }
        return ResultDTO.success(appBaseInfoDTOList);
    }

    private void setLowCodeAccountPo(Collection<AppAccessPo> appAccessPoPage) {
        if (CollectionUtils.isEmpty((appAccessPoPage))) {
            return;
        }

        Set<Integer> appIds = appAccessPoPage.stream().map(AppAccessPo::getDataAccessAppId).collect(Collectors.toSet());
        List<LowCodeAccountPo> accountPos = lowCodeAccountService.listByAppIds(appIds);
        if (CollectionUtils.isEmpty(accountPos)) {
            return;
        }
        //appId->LowCodeAccountPo
        Map<Integer, LowCodeAccountPo> appIdToAccount = accountPos.stream().collect(
                Collectors.toMap(AppPo::getAppId, Function.identity(), (pre, next) -> pre));
        for (AppAccessPo appAccessPo : appAccessPoPage) {
            appAccessPo.setLowCodeAccountPo(appIdToAccount.get(appAccessPo.getDataAccessAppId()));
        }
    }


    @Override
    public AppAccessPo queryAppInfoById(Integer dataAssetAppId) {
        return this.getMapper().selectByPrimaryKey(dataAssetAppId);
    }

    @Override
    public AppAccessPo queryAppInfoByKey(String appKey) {
        Example example = new Example(AppAccessPo.class);
        example.createCriteria().andEqualTo("dataAccessKey",appKey);
        return this.getMapper().selectOneByExample(example);
    }

    @Override
    public AppAccessPo getAppAccessInfoByPrefix(String apiPrefix) {
        Example example = new Example(AppAccessPo.class);
        example.createCriteria().andEqualTo("dataAccessPrefix",apiPrefix);
        return this.getMapper().selectOneByExample(example);
    }


}
