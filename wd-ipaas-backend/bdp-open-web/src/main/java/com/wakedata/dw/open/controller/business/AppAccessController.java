package com.wakedata.dw.open.controller.business;

import cdjd.com.google.common.collect.Maps;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.wakedata.dw.lowcode.model.AppPo;
import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.lowcode.service.LowCodeAccountService;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetEnums.AppType;
import com.wakedata.dw.open.enums.DataAssetEnums.DataAccessAppAuthType;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.*;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.AppCountDo;
import com.wakedata.dw.open.model.openapi.DwOpenAccessAuthPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.api.DataApiGatewayService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.approval.vo.AppAccessVo;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.utils.OpenJsonWebTokenContext;
import com.wakedata.dw.open.util.WebUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.open.vo.DataAccessAppReq;
import com.wakedata.dw.openapi.service.DwOpenAccessAuthService;
import com.wakedata.dw.platform.tools.jwt.JsonWebTokenUtils;
import com.wakedata.wd.app.client.app.dto.AppBaseInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author yiyufeng
 * @title DataAccessAppController
 * @projectName bdp-open
 * @date 2019/8/16 10:22
 * @descprition 接入端app管理
 */
@RestController
@Slf4j
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/data_access_app")
@Api(value = "应用管理", tags = "应用管理（接入端管理）")
@Validated
public class AppAccessController extends BaseController {

    @Autowired
    private AppAccessService appAccessService;
    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Autowired
    private LowCodeAccountService lowCodeAccountService;
    @Autowired
    private DwOpenAccessAuthService dwOpenAccessAuthService;
    @Autowired
    private AppApprovalService appApprovalService;
    @Resource
    private DataApiGatewayService dataApiGatewayService;

    @ApiOperation("接入端列表")
    @GetMapping(value = "/list")
    @AuditLog
    public ResultDTO<List<AppAccessPo>> listDataAccessApp(AppAccessPo dataAccessApp) {
        ResultDTO<List<AppAccessPo>> resultDTO = new ResultDTO<>();
        AuthUtils.setAppId(dataAccessApp);
        List<AppAccessPo> data = this.appAccessService.searchAccessApp(dataAccessApp);

        //设置低代码绑定低账号信息
        setLowCodeAccountPo(data);
        resultDTO.setData(data);
        return resultDTO;
    }

    @ApiOperation("测试选择应用")
    @GetMapping(value = "chooseAppTest")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appKey",value = "应用key",required = true,dataType = "String",paramType = "query")
    })
    public ResultDTO<Map<String,List<ApiConditionPo>>> chooseAppTest(@RequestParam String appKey){
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        return ResultDTO.success(dataApiGatewayService.chooseAppTest(lesseeId,appKey));
    }

    @ApiOperation("APP列表(包含默认选项)")
    @GetMapping(value = "/listWithDefaultApp")
    @AuditLog
    public ResultDTO<AppAccessListVo> listWithDefaultApp(AppAccessPo dataAccessApp, Integer apiId) {
        ResultDTO<AppAccessListVo> resultDTO = new ResultDTO<>();
        AuthUtils.setAppId(dataAccessApp);
        if(null != apiId){
            resultDTO.setData(this.appAccessService.searchAccessAppWithDefault(dataAccessApp,apiId));
        }
        return resultDTO;
    }

    @ApiOperation("权限回收时，展示授权的接入")
    @GetMapping("/list/permission/app")
    public ResultDTO<List<AppAccessPo>> listPermissionApp(Integer apiId) {
        ResultDTO<List<AppAccessPo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(this.appAccessService.listPermissionApp(apiId));
        return resultDTO;
    }

    @ApiOperation("接入端详情")
    @AuditLog
    @GetMapping(value = "/appDetail")
    public ResultDTO<AppAccessVO> appDetail(@RequestParam("dataAccessAppId") Integer dataAccessAppId){
        return ResultDTO.success(appAccessService.appDetail(dataAccessAppId));
    }

    @ApiOperation("接入端列表分页模糊查询")
    @AuditLog
    @GetMapping(value = "/list/page/like")
    public PageResultDTO<List<AppAccessVo>> listDataAccessApp(AppAccessPo appAccessPo, PageQuery pageQuery){
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        appAccessPo.setLesseeId(lesseeId);
        return appAccessService.pageList(lesseeId,appAccessPo,pageQuery.getPageNo(),pageQuery.getPageSize());
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


    /**
     * 新增接入端
     *
     * @param dataAccessAppReq
     * @return
     */
    @ApiOperation("新增接入端")
    @PostMapping(value = "/create")
    @AuditLog
    public ResultDTO<AppAccessPo> createDataAccessApp(@Valid DataAccessAppReq dataAccessAppReq) {
        AppAccessPo appAccessPo = new AppAccessPo();
        BeanUtils.copyProperties(dataAccessAppReq, appAccessPo);
        ResultDTO<AppAccessPo> resultDTO = new ResultDTO<>();

        // 初始下线
        appAccessPo.setPublishStatus(DataAssetEnums.DataAccessPublishStatus.OFF_LINE);
        appAccessPo.setStatus(DataAssetEnums.DataAccessAppEnums.PASS);
        appAccessPo.setApprovalMessage("管理员添加，审批通过");

        //创建低代码绑定的账号
        createWakeAccount(appAccessPo, dataAccessAppReq);
        appAccessPo.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        AppAccessPo accessApp = this.appAccessService.createAccessApp(appAccessPo);
        resultDTO.setData(accessApp);
        //创建应用绑定的授权信息，如惟客云应用授权给开放平台后产生的映射数据
        DwOpenAccessAuthPo dwOpenAccessAuthPo = new DwOpenAccessAuthPo();
        dwOpenAccessAuthPo.setDataAccessAppId(accessApp.getDataAccessAppId());
        dwOpenAccessAuthPo.setType(dataAccessAppReq.getAppAuthType().getValue());
        //授权配置，json串形式
        dwOpenAccessAuthPo.setApiAuthConfig(dataAccessAppReq.getApiAuthConfig());
        this.setEditUserAndEpId(dwOpenAccessAuthPo);
        dwOpenAccessAuthService.add(dwOpenAccessAuthPo);
        return resultDTO;
    }

    @ApiOperation("查询授权应用列表")
    @PostMapping(value = "/queryAppInfo")
    @AuditLog
    public ResultDTO<List<AppBaseInfoDTO>> queryAppInfo() {
        Long tenantId = IpaasUserContext.getUserInfo().getTenantId();
        return appAccessService.queryAppInfo(tenantId);
    }


    private void createWakeAccount(AppAccessPo appAccessPo, DataAccessAppReq req) {
        //低代码应用
        if (Objects.nonNull(req.getAppType()) && AppType.LOW_CODE_APP == req.getAppType()) {
            appAccessPo.setInCharge(WebUtils.getCurrentUserInfo().getAccount());
            appAccessPo.setAuthType(DataAccessAppAuthType.NO_AUTH);
        }
        LowCodeAccountPo lowCodeAccountPo = new LowCodeAccountPo();
        lowCodeAccountPo.setPwd(req.getWakePassword());
        lowCodeAccountPo.setUserName(req.getWakeUserName());

        String loginName = WebUtils.getCurrentUserInfo().getAccount();
        lowCodeAccountPo.setUpdateBy(loginName);

        if (Objects.isNull(appAccessPo.getDataAccessAppId())) {
            lowCodeAccountPo.setCreateBy(loginName);
        }

        appAccessPo.setLowCodeAccountPo(lowCodeAccountPo);
    }

    /**
     * 应用上线/下线
     * @param dataAccessAppId 应用id
     * @param dataAccessAppId status
     * @return Boolean
     */
    @ApiOperation("应用上线")
    @GetMapping(value = "/updateLine")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataAssetApiId", value = "应用id", required = true),
            @ApiImplicitParam(name = "status", value = "0:上线，1：下线", required = true)
    })
    @AuditLog
    public ResultDTO<Boolean> updateLine(Integer dataAccessAppId, Integer status) {
        return ResultDTO.success(appAccessService.updateLine(dataAccessAppId, status));
    }


    @ApiOperation("修改接入端")
    @PostMapping(value = "/edit")
    @AuditLog
    public ResultDTO editDataAccessApp(@Valid DataAccessAppReq dataAccessAppReq) {
        AppAccessPo dataAccessApp = new AppAccessPo();
        BeanUtils.copyProperties(dataAccessAppReq, dataAccessApp);

        //创建低代码绑定的账号
        createWakeAccount(dataAccessApp, dataAccessAppReq);
        //非连接器鉴权模式清空授权id
        if(DataAccessAppAuthType.CONNECTOR_AUTH != dataAccessAppReq.getAuthType()){
            dataAccessApp.setConnectorAuthId(null);
        }
        boolean success = this.appAccessService.editAccessApp(dataAccessApp);
        if (!success) {
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setFailed(MsgCodeEnum.w_data_app_id_edit_fail);
            return resultDTO;
        }
        Integer dataAccessAppId = dataAccessApp.getDataAccessAppId();

        //更新应用下授权绑定的关联信息
        DwOpenAccessAuthPo dwOpenAccessAuthPo = new DwOpenAccessAuthPo();
        dwOpenAccessAuthPo.setDataAccessAppId(dataAccessAppId);
        dwOpenAccessAuthPo.setApiAuthConfig(dataAccessAppReq.getApiAuthConfig());
        this.setEditUserAndEpId(dwOpenAccessAuthPo);
        dwOpenAccessAuthService.updateByDataAccessAppId(dwOpenAccessAuthPo);
        return new ResultDTO<>();
    }

    @ApiOperation(("审批接入端"))
    @PostMapping(value = "/approval")
    @AuditLog
    public ResultDTO approval(AppAccessPo appAccessPo) {
        appAccessPo = this.appAccessService.approvalAccessApp(appAccessPo);
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(appAccessPo);
        return resultDTO;
    }

    @Transactional
    @ApiOperation("删除接入端")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataAccessAppId", value = "应用id", required = true),
            @ApiImplicitParam(name = "forceDelete", value = "是否强制删除,否则需要解绑关联对api", required = false)
    })
    @GetMapping(value = "/delete")
    @AuditLog
    public ResultDTO<Boolean> delete(Integer dataAccessAppId, @RequestParam(required = false) Boolean forceDelete) {
        if (forceDelete == null || !forceDelete) {
            AppAccessPo appAccess = appAccessService.getAppAccess(dataAccessAppId.toString());
            DataAssetEnums.DataAccessPublishStatus publishStatus = appAccess.getPublishStatus();
            if (publishStatus.equals(DataAssetEnums.DataAccessPublishStatus.ON_LINE)){
                throw new OpenException(MsgCodeEnum.w_app_auth_list_delete_error);
            }
        }
        boolean success = this.appAccessService.deleteAccessApp(dataAccessAppId);
        if (!success) {
            ResultDTO<Boolean> resultDTO = new ResultDTO<>();
            resultDTO.setFailed(MsgCodeEnum.w_data_app_id_delete_fail);
            return resultDTO;
        }
        return new ResultDTO<>();
    }

    @ApiOperation("我的接入端")
    @GetMapping(value = "/my")
    public ResultDTO<List<AppAccessPo>> myApp() {
        String loginName = WebUtils.getCurrentUserInfo().getAccount();

        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setInCharge(loginName);
        appAccessPo.setLesseeId(AuthUtils.currentAppId());
        Page<AppAccessPo> collect = this.appAccessService.selectPageLikeOrderBy(
                appAccessPo, 1, 9999, "", Lists.newArrayList(), "CREATE_TIME", false,
                "", null, null, null, null);

        if (CollectionUtils.isNotEmpty(collect)) {
            //求接入在多少个api里面授权了
            List<AppCountDo> appCountDos = appAccessService.queryApiReference(collect.stream()
                                                                                      .map(AppAccessPo::getDataAccessAppId).collect(Collectors.toList()));
            Map<Integer, Integer> map = appCountDos.stream().collect(Collectors.toMap(AppCountDo::getAppId, AppCountDo::getApiNum));
            collect.forEach(po -> {
                po.setApiNum(map.containsKey(po.getDataAccessAppId()) ? map.get(po.getDataAccessAppId()) : 0);
            });

            //设置低代码绑定低账号信息
            setLowCodeAccountPo(collect);
        }
        ResultDTO<List<AppAccessPo>> resultDTO = new ResultDTO();
        resultDTO.setData(collect);
        return resultDTO;
    }

    @ApiOperation("重置接入端密钥")
    @PostMapping(value = "/reset_secret")
    @AuditLog
    public ResultDTO<AppAccessPo> resetDataAccessApp(Integer dataAccessAppId) {
        ResultDTO<AppAccessPo> resultDTO = new ResultDTO<>();
        AppAccessPo dataAccessApp = this.appAccessService.resetAccessAppSecret(dataAccessAppId);
        if (null == dataAccessApp) {
            resultDTO.setFailed(MsgCodeEnum.w_data_app_id_reset_secret_fail);
        } else {
            resultDTO.setData(dataAccessApp);
        }
        return resultDTO;
    }

    @GetMapping("/apiAuthList")
    @ApiOperation("API授权列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", required = true)
    })
    @AuditLog
    public PageResultDTO<Page<AuthApiVO>> apiAuthList(Integer appId, PageQuery pageQuery) {
        PageResultDTO<Page<AuthApiVO>> pageResultDTO = new PageResultDTO<>();
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        Page<AuthApiVO> authApiVOPage = appApprovalService.selectAuthApiListWithAppAuthStatus(lesseeId, appId, pageQuery);
        pageResultDTO.setData(authApiVOPage);
        pageResultDTO.setPageNo(pageQuery.getPageNo());
        pageResultDTO.setPageSize(pageQuery.getPageSize());
        pageResultDTO.setTotalCount(Long.valueOf(authApiVOPage.getTotal()).intValue());
        pageResultDTO.isSuccess();
        return pageResultDTO;
    }

    @GetMapping("/auth")
    @ApiOperation("当前接口授权给当前应用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", required = true),
            @ApiImplicitParam(name = "apiId", value = "接口id", required = true)
    })
    @AuditLog
    public ResultDTO auth(Integer appId,Integer apiId) {
        ResultDTO resultDTO = new ResultDTO<>();
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        appApprovalService.appAuthApi(lesseeId, appId, apiId);
        resultDTO.isSuccess();
        return resultDTO;
    }

    @GetMapping("/deauth")
    @ApiOperation("当前接口解除授权当前应用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", required = true),
            @ApiImplicitParam(name = "apiId", value = "接口id", required = true)
    })
    @AuditLog
    public ResultDTO deauth(Integer appId,Integer apiId) {
        ResultDTO resultDTO = new ResultDTO<>();
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        appApprovalService.appDeauthApi(lesseeId, appId, apiId);
        resultDTO.isSuccess();
        return resultDTO;
    }

    @GetMapping("/app/reference")
    @ApiOperation("接入引用的api")
    public ResultDTO<List<ApiInfoDo>> getAppReferenceApi(@RequestParam Integer appId) {
        ResultDTO<List<ApiInfoDo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(appAccessService.getAppReferenceApi(appId));
        return resultDTO;
    }

    @GetMapping("/app/auth/date")
    @ApiOperation("app接入在api中的授权时间")
    public ResultDTO<Date> getAppAuthDate(@RequestParam Integer appId, @RequestParam Integer apiId) {
        ResultDTO<Date> resultDTO = new ResultDTO<>();
        resultDTO.setData(appAccessService.getAppAuthDate(appId, apiId));
        return resultDTO;
    }

    @ApiOperation(value = "获取token值")
    @GetMapping(value = "/getToken")
    public ResultDTO<String> getToken(
            @NotNull(message = "appId不能为空") String appId,
            @NotNull(message = "appSecret不能为空") String appSecret,
            @NotNull(message = "apiPath不能为空") String apiPath,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date issuedAt) {
        if (issuedAt == null || issuedAt.before(new Date())) {
            throw new OpenException(MsgCodeEnum.w_api_jwt_apply_token_issued_date_before_current_error);
        }

        try {
            AppAccessPo appAccess = appAccessService.getAppAccess(appId);
            if (!appAccess.getDataAccessSecret().equals(appSecret)) {
                throw new OpenException(MsgCodeEnum.w_data_app_id_not_found);
            }
            DataAssetApiPo detail = dataAssetApiService.selectByApiPath(apiPath);

            String token = appAccessService.getToken(appId, appAccess.getDataAccessSecret(), detail.getDataAssetApiMethod(), issuedAt);
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setMessage(MsgCodeEnum.s_success, true);
            resultDTO.setData(token);
            return resultDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_api_jwt_apply_token_error);
        }
    }

    @ApiOperation(value = "获取token值, 已登录")
    @GetMapping(value = "/getTokenInternal")
    public ResultDTO<String> getToken(
            @NotNull(message = "appId不能为空") String appId,
            @NotNull(message = "apiPath不能为空") String apiPath,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date issuedAt) {
        if (issuedAt.before(new Date())) {
            throw new OpenException(MsgCodeEnum.w_api_jwt_apply_token_issued_date_before_current_error);
        }

        try {
            AppAccessPo appAccess = appAccessService.getAppAccess(appId);
            DataAssetApiPo detail = dataAssetApiService.selectByApiPath(apiPath);

            String token = appAccessService.getToken(appId, appAccess.getDataAccessSecret(), detail.getDataAssetApiMethod(), issuedAt);
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setMessage(MsgCodeEnum.s_success, true);
            resultDTO.setData(token);
            return resultDTO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_api_jwt_apply_token_error);
        }
    }

    @ApiOperation(value = "获取授权额外请求参数")
    @GetMapping(value = "/getAuthParams")
    public ResultDTO<ApiConditionPo> getAuthParams(@NotNull(message = "appId不能为空") String appId,
                                                   @NotNull(message = "apiId不能为空") Integer apiId) {
        AppAccessPo appAccess = appAccessService.getAppAccess(appId);
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);

        if (DataAssetEnums.DataAccessAppAuthType.TOKEN_AUTH == appAccess.getAuthType()) {
            DataAssetApiPo detail = dataAssetApiService.show(apiId);
            OpenJsonWebTokenContext context = new OpenJsonWebTokenContext();
            context.setAppId(appId);
            context.setExpireTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
            context.setApiPath(appAccessService.normalization(detail.getDataAssetApiMethod()));

            ApiConditionPo apiParam = ApiConditionPo.build(
                    apiId,
                    JsonWebTokenUtils.AUTH_HEADER_KEY,
                    "Token鉴权",
                    "varchar",
                    HttpParamKind.HEAD,
                    1024,
                    DataAssetEnums.FiledTypeEnums.PARAMETERS,
                    JsonWebTokenUtils.createToken(appAccess.getDataAccessSecret(), context),
                    true,
                    false
            );
            resultDTO.setData(apiParam);
        }

        return resultDTO;
    }

    @ApiOperation(value = "获取授权类型")
    @GetMapping(value = "/getAuthTypes")
    public ResultDTO<Map<String, String>> getAuthTypes() {
        DataAssetEnums.DataAccessAppAuthType[] authTypes = DataAssetEnums.DataAccessAppAuthType.values();
        Map<String, String> results = Maps.newLinkedHashMap();
        for (DataAssetEnums.DataAccessAppAuthType authType : authTypes) {
            results.put(authType.name(), authType.getDesc());
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(results);
        return resultDTO;
    }

    @ApiOperation(value = "根据应用id获取当前的应用配置信息")
    @GetMapping(value = "/getAuthConfigByAppId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", required = true)
    })
    @AuditLog
    public ResultDTO<String> getAuthConfigByAppId(Integer appId) {
        return dwOpenAccessAuthService.getAuthConfigByAppId(appId);
    }

}
