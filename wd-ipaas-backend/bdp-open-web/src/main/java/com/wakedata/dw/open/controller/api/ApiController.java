package com.wakedata.dw.open.controller.api;

import com.alibaba.nacos.common.utils.HttpMethod;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.datasource.model.SqlAnalysisDo;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.enums.DataAssetEnums.DataApiType;
import com.wakedata.dw.open.event.ApiStatusUpdateEvent;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.dto.ApiInfoDTO;
import com.wakedata.dw.open.model.api.dto.AppAuthApiReqDTO;
import com.wakedata.dw.open.model.api.dto.AuthApiRespDTO;
import com.wakedata.dw.open.model.api.dto.NotAuthApiReqParam;
import com.wakedata.dw.open.model.domain.*;
import com.wakedata.dw.open.model.query.ApiQuery;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.api.ApiStatisticsService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.BuildExpressionDTO;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowResultTemplateDTO;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.vo.ApiAuthorizationVo;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.util.WebUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author tanzhi
 * @title ApiController
 * @date 2019/11/28 17:09
 * @projectName bdp-open
 * @descriptor
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/api")
@Api(value = "API管理", tags = "新的API管理")
@Validated
public class ApiController extends BaseController implements ApplicationEventPublisherAware {

    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Autowired
    private ApiStatisticsService apiStatisticsService;
    @Autowired
    private AppApprovalService appApprovalService;
    @Autowired
    private AuthInfoService authInfoService;
    @Autowired
    private ApiResponseParamService apiResponseParamService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @ApiOperation("流程编排-新增API")
    @PostMapping(value = "/liteflow/add")
    @AuditLog
    public ResultDTO liteflowAdd(@RequestBody ApiDetailVo apiDetailVo) {
        DataAssetApiPo dataAssetApiPo = apiDetailVo.getDataAssetApi();
        dataAssetApiPo.setApiType(DataApiType.LITE_FLOW);
        dataAssetApiPo.setUpdateFrequency(DataAssetEnums.UpdateFrequency.DAY);
        dataAssetApiPo.setInCharge(IpaasUserContext.getUserInfo().getUserIdentification());

        dataAssetApiService.checkApiMethodExists(dataAssetApiPo);

        dataAssetApiService.addApi(apiDetailVo);

        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(dataAssetApiPo.getDataAssetApiId());
        resultDTO.isSuccess();
        return resultDTO;
    }

    @ApiOperation("添加API")
    @PostMapping(value = "/add_api")
    @AuditLog
    public ResultDTO add(@RequestBody ApiDetailVo dataAssetApi) {
        dataAssetApiService.checkApiMethodExists(dataAssetApi.getDataAssetApi());
        dataAssetApi.getDataAssetApi().setInCharge(IpaasUserContext.getUserInfo().getUserIdentification());
        dataAssetApiService.addApi(dataAssetApi);
        // 添加API事件- 事件中心接入流程编排
        applicationEventPublisher.publishEvent(new ApiStatusUpdateEvent(this, dataAssetApi, ApiOperateStatusEnum.API_ADD.name()));
        return show(dataAssetApi.getDataAssetApi().getDataAssetApiId(), dataAssetApi.getDataAssetApi().getAccessAppId());
    }

    @ApiOperation("修改API")
    @AuditLog
    @PostMapping(value = "/update_api")
    public ResultDTO update(@RequestBody @Valid ApiDetailVo dataAssetApi) {
        dataAssetApiService.updateApi(dataAssetApi);
        applicationEventPublisher.publishEvent(new ApiStatusUpdateEvent(this, dataAssetApi, ApiOperateStatusEnum.API_UPDATE.name()));
        return show(dataAssetApi.getDataAssetApi().getDataAssetApiId(), dataAssetApi.getDataAssetApi().getAccessAppId());
    }


    @PostMapping("/delete")
    @ApiOperation("删除API")
    @AuditLog
    public ResultDTO<?> delete(@NotNull(message = "dataAssetApiId不能为空") Integer dataAssetApiId) {
        applicationEventPublisher.publishEvent(new ApiStatusUpdateEvent(this, dataAssetApiId, ApiOperateStatusEnum.API_DELETE.name()));
        dataAssetApiService.deleteApi(dataAssetApiId);
        // 清理API响应参数表
        apiResponseParamService.clearByDataAssertId(dataAssetApiId);
        return new ResultDTO<>();
    }

    @GetMapping("/show")
    @ApiOperation("查询API")
    public ResultDTO<ApiDetailVo> show(Integer dataAssetApiId, String appId) {
        ApiDetailVo apiDetailVo = dataAssetApiService.detailVo(dataAssetApiId, null);
        ResultDTO<ApiDetailVo> resultDTO = new ResultDTO<>();
        DataAssetApiPo dataAssetApiPo = apiDetailVo.getDataAssetApi();
        dataAssetApiPo.setAccessAppId(appId);
        apiDetailVo.setDataAssetApi(dataAssetApiPo);
        resultDTO.setData(apiDetailVo);
        return resultDTO;
    }

    @GetMapping("/list")
    @ApiOperation("查询API列表")
    @AuditLog
    public ResultDTO<List<DataAssetApiPo>> list(DataAssetApiPo dataAssetApi) {
        AuthUtils.setAppId(dataAssetApi);
        List<DataAssetApiPo> dataAssetApiPos = dataAssetApiService.find(dataAssetApi);
        ResultDTO<List<DataAssetApiPo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(dataAssetApiPos);
        return resultDTO;
    }

    @PostMapping("/list/page")
    @ApiOperation(value = "查询API列表", tags = "查询API列表")
    @AuditLog
    public PageResultDTO<List<DataAssetApiDTO>> pageList(@RequestBody ApiQuery query) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        query.setPlatformAdmin(userInfo.getIsPlatformAdmin());
        query.setLesseeId(userInfo.getLesseeId());
        return dataAssetApiService.pageList(query);
    }

    @GetMapping("/list/page/like")
    @ApiOperation("查询API列表")
    @AuditLog
    public PageResultDTO<Page<DataAssetApiPo>> pageLike(DataAssetApiPo dataAssetApiPo, PageQuery pageQuery, String keyword, String from, String to, Integer publishStatus, Integer authId) {
        AuthUtils.setAppId(dataAssetApiPo);
        //变更成目录树后,兼容in
        Integer apiGroupId = dataAssetApiPo.getApiGroupId();
        List<Integer> apiGroupIds = new ArrayList<>();
        if (apiGroupId != null) {
            apiGroupIds = dataAssetApiService.findGroupIds(apiGroupId);
            dataAssetApiPo.setApiGroupId(null);
        }


        List<String> likeColumns = Arrays.asList("apiName", "dataAssetName", "dataAssetDescription", "dataAssetApiMethod", "apiDescription");

        List<Integer> notEqualsApiType = Arrays.asList(DataApiType.EVENT_SEND.getValue(), DataApiType.EVENT_RECEIVE.getValue());

        Page<DataAssetApiPo> dataAssetApiPos =
                dataAssetApiService.selectPageLikeNotEqualOrderBy(
                        dataAssetApiPo, pageQuery.getPageNo(), pageQuery.getPageSize(),
                        keyword, likeColumns, "CREATE_TIME", false,
                        "createTime", from, to, "apiGroupId", apiGroupIds, "apiType", notEqualsApiType, publishStatus, authId);
        PageResultDTO<Page<DataAssetApiPo>> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount((int) dataAssetApiPos.getTotal());
        resultDTO.setData(dataAssetApiPos);
        return resultDTO;
    }


    @PostMapping("/publish")
    @ApiOperation("API上线")
    @AuditLog
    public ResultDTO publish(Integer dataAssetApiId) {
        dataAssetApiService.publishDataAssetApi(IpaasUserContext.getUserInfo().getUserIdentification(), dataAssetApiId);
        // 发布事件
        applicationEventPublisher.publishEvent(new ApiStatusUpdateEvent(this, dataAssetApiId, ApiOperateStatusEnum.API_PUBLISH.name()));
        return new ResultDTO();
    }

    @PostMapping("/un_publish")
    @ApiOperation("API下线")
    @AuditLog
    public ResultDTO unPublish(Integer dataAssetApi) {
        dataAssetApiService.unPublishDataAssetApi(dataAssetApi);
        // 下线事件
        applicationEventPublisher.publishEvent(new ApiStatusUpdateEvent(this, dataAssetApi, ApiOperateStatusEnum.API_OFFLINE.name()));
        return new ResultDTO();
    }

    @PostMapping("/batch.online")
    @ApiOperation("API批量上线")
    @AuditLog
    public ResultDTO<Boolean> batchOnline(@RequestBody @NotEmpty(message = "参数dataAssetApiIdList不能为空") List<Integer> dataAssetApiIdList) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        Boolean result = dataAssetApiService.dataAssetApiBatchPublish(userInfo.getUserIdentification(), dataAssetApiIdList);
        return ResultDTO.success(result);
    }

    @PostMapping("/batch.offline")
    @ApiOperation("API批量下线")
    @AuditLog
    public ResultDTO<Boolean> batchOffline(@RequestBody @NotEmpty(message = "参数dataAssetApiIdList不能为空") List<Integer> dataAssetApiIdList) {
        Boolean result = dataAssetApiService.dataAssetApiBatchUnPublish(dataAssetApiIdList, DataAssetPublishStatusEnum.UN_PUBLISH);
        return ResultDTO.success(result);
    }

    @GetMapping("/list/collect/approval")
    @ApiOperation("首页查询，关联收藏")
    public ResultDTO<List<ApiCollectDo>> indexList(Integer dataAssetId, Integer appId) {
        String username = WebUtils.getCurrentUserInfo().getAccount();
        List<ApiCollectDo> list = dataAssetApiService.listIndex(username, dataAssetId, appId);
        ResultDTO<List<ApiCollectDo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(list);
        return resultDTO;
    }

    @GetMapping("/my_api")
    @ApiOperation("我的API统计")
    public PageResultDTO<List<AppAccessDo>> myApi(
            PageQuery pageQuery,
            Integer accessAppId,
            Integer dataAssetApiId,
            String keyword) {
        Page<AppAccessDo> totalGroupByAccessMethod = apiStatisticsService.myApi(
                WebUtils.getCurrentUserInfo().getAccount(),
                pageQuery,
                accessAppId,
                dataAssetApiId,
                keyword
        );
        PageResultDTO pageResultDTO = new PageResultDTO();
        pageResultDTO.setData(totalGroupByAccessMethod);
        pageResultDTO.setTotalCount((int) totalGroupByAccessMethod.getTotal());
        return pageResultDTO;
    }

    @PostMapping("/delete_my_api")
    @ApiOperation("删除我的申请")
    @AuditLog
    public ResultDTO deleteMyApi(Integer approvalId) {
        appApprovalService.deleteApproval(approvalId);
        return new ResultDTO();
    }

    @GetMapping(value = "/asset_monitor")
    @ApiOperation(value = "Api详情监控信息")
    public ResultDTO<ApiTotalCountDo> assetMonitorInfo(Integer dataAssetId) {
        return dataAssetApiService.assetMonitorInfo(dataAssetId);
    }

    @GetMapping(value = "/monitor_times")
    @ApiOperation(value = "Api详情监控信息-调用次数分布")
    public ResultDTO<Map<String, ApiTimesDo>> monitorTimes(Integer dataAssetId, ApiCountDateType type) {
        if (type.equals(ApiCountDateType.DAY)) {
            return dataAssetApiService.countTimesDay(dataAssetId);
        } else {
            return dataAssetApiService.countTimesWeek(dataAssetId);
        }
    }

    @GetMapping(value = "/monitor_qps")
    @ApiOperation(value = "Api详情监控信息-qps-耗时")
    public ResultDTO<Map<String, BigDecimal>> monitorQps(Integer dataAssetId, ApiCountDateType type, boolean qps) {
        if (type.equals(ApiCountDateType.DAY)) {
            return dataAssetApiService.countQpsDay(dataAssetId, qps);
        } else {
            return dataAssetApiService.countQpsWeek(dataAssetId, qps);
        }
    }

    @GetMapping(value = "/monitor_app_times")
    @ApiOperation(value = "Api详情监控信息-api调用次数排行")
    public ResultDTO<List<AppTimesDo>> monitorAppTime(Integer dataAssetId, ApiCountDateType type) {
        if (type.equals(ApiCountDateType.DAY)) {
            return dataAssetApiService.countAppTimesDay(dataAssetId);
        } else {
            return dataAssetApiService.countAppTimesWeek(dataAssetId);
        }
    }

    @GetMapping(value = "/app_approve_info")
    @ApiOperation(value = "Api详情审批信息-接入详情")
    public ResultDTO<Page<DataAccessApprovalDo>> appApproveInfo(Integer dataAssetId, PageQuery pageQuery) {
        Page<DataAccessApprovalDo> approvalPoByAssetId = appApprovalService.getApprovalPoByAssetId(dataAssetId, pageQuery);
        PageResultDTO<Page<DataAccessApprovalDo>> resultDTO = new PageResultDTO<>();
        resultDTO.setData(approvalPoByAssetId);
        if (approvalPoByAssetId != null) {
            resultDTO.setTotalCount((int) approvalPoByAssetId.getTotal());
        }
        return resultDTO;
    }

    @GetMapping(value = "/analysis/sql")
    @ApiOperation("自定义sql时，sql解析接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sql", value = "sql语句", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "dataSourceId", value = "数据源id（解析select语句时候用到，不同数据源使用的分页参数有区别）", dataType = "integer", paramType = "query")
    })
    public ResultDTO<SqlAnalysisDo> parseSql(@RequestParam String sql, @RequestParam(required = false) Integer dataSourceId) {
        ResultDTO<SqlAnalysisDo> resultDTO = new ResultDTO<>();
        resultDTO.setData(dataAssetApiService.analysisSql(sql, dataSourceId));
        return resultDTO;
    }

    @GetMapping(value = "/query/external/application")
    @ApiOperation("查询已授权的外部应用")
    public ResultDTO<List<ApiAuthorizationVo>> queryExternalApplication(@RequestParam Integer dataAssetId, @RequestParam Integer dataAccessAppId) {
        ResultDTO<List<ApiAuthorizationVo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(authInfoService.queryApiAndApplication(dataAssetId, dataAccessAppId));
        return resultDTO;
    }

    @PostMapping("/listByAppIdGroupIdName")
    @ApiOperation("据应用id、分组id和名称查询授权api(低代码获取接口列表)")
    public PageResultDTO<List<AuthApiRespDTO>> listByAppIdGroupIdName(@Valid @RequestBody AppAuthApiReqDTO dto) {
        return dataAssetApiService.listByAppIdGroupIdName(dto);
    }


    @PostMapping("/listNotAuthApi")
    @ApiOperation("查询应用没有授权的api")
    public PageResultDTO<List<DataAssetApiPo>> listNotAuthApi(@Valid @RequestBody NotAuthApiReqParam param) {
        param.setLesseeId(AuthUtils.currentAppId().intValue());
        if (Boolean.TRUE.equals(param.getCreatedForSelf())) {
            param.setInCharge(AuthUtils.getCurrentUserInfo().getAccount());
        }
        return dataAssetApiService.listNotAuthApi(param);
    }

    /**
     * 根据API ID获取节点信息（无论是编排或者单个API）
     *
     * @param dataAssetApiId API ID
     * @return 节点信息集合（如果非编排，集合只有一个只有一个元素）
     */
    @GetMapping("/getApiInfos")
    public ResultDTO<List<ApiInfoDTO>> getApiInfos(Integer dataAssetApiId) {
        return ResultDTO.success(dataAssetApiService.getApiInfos(dataAssetApiId));
    }


    @GetMapping("/listRespParamByApiId")
    @ApiOperation(value = "根据apiId获取返回参数树", httpMethod = HttpMethod.GET)
    public ResultDTO<List<ApiRespParamDTO>> listRespParamByApiId(@RequestParam Integer apiId) {
        ResultDTO<List<ApiRespParamDTO>> resultDTO = new ResultDTO<>();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(apiResponseParamService.listByApiId(apiId));
        return resultDTO;
    }

    @PostMapping("/buildExpression")
    @ApiOperation(value = "构建取参表达式", httpMethod = HttpMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressionType", value = "表达式类型,BETWEEN_OPERATOR: 算子之间表达式，LITEFLOW_RESULT：流程编排结果集表达式（默认值）")
    })
    public ResultDTO<String> buildExpression(@Valid @RequestBody BuildExpressionDTO dto, @RequestParam(defaultValue = "LITEFLOW_RESULT") String expressionType) {
        ResultDTO<String> resultDTO = new ResultDTO<>();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(apiResponseParamService.convertColumnToExpression(dto, expressionType));
        return resultDTO;
    }

    @PostMapping("/saveLiteFlowResult")
    @ApiOperation(value = "保存流程编排结果模板信息", httpMethod = HttpMethod.POST)
    public ResultDTO<Boolean> saveLiteFlowResult(@RequestBody LiteFlowResultTemplateDTO liteFlowResultTemplateDTO) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        apiResponseParamService.saveLiteFlowResult(liteFlowResultTemplateDTO, userInfo);
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        return resultDTO;
    }


    @GetMapping("/findLiteflowResult")
    @ApiOperation(value = "根据apiId获取返回流程编排结果模板参数树", httpMethod = HttpMethod.GET)
    public ResultDTO<List<ApiRespParamDTO>> findLiteflowResult(@RequestParam Integer apiId) {
        ResultDTO<List<ApiRespParamDTO>> resultDTO = new ResultDTO<>();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(apiResponseParamService.findLiteflowResult(apiId));
        return resultDTO;
    }

}