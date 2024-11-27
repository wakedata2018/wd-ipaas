package com.wakedata.dw.open.controller.business;

import static com.wakedata.dw.open.Constants.UserIdentificationParam.KEY_USER_IDENTIFICATION;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.controller.dto.BatchAuthorizeReqParam;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum;
import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.approval.vo.ApprovalApiVO;
import com.wakedata.dw.open.service.approval.vo.ApprovalDetailVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalHistoryVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalRecordVO;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.util.WebUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yiyufeng
 * @title ApprovalController
 * @projectName bdp-open
 * @date
 * @description
 */

@RestController
@Slf4j
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/approval")
@Api(value = "审批管理", tags = "审批管理")
public class AppApprovalController extends BaseController {

    @Autowired
    private AppApprovalService appApprovalService;
    @Autowired
    private AppAccessRuleService appAccessRuleService;
    @Autowired
    private ApiConditionMapper apiConditionMapper;


    /**
     * 开放平台审批
     *
     * @param approvalId
     * @param userIdentification
     * @return
     */
    @PostMapping(value = "/do")
    @ApiOperation(value = "审批")
    @AuditLog
    public ResultDTO approvalSuccess(
            @RequestParam Integer approvalId,
            @RequestParam(KEY_USER_IDENTIFICATION) String userIdentification,
            @RequestParam String message,
            @RequestParam ApprovalStatusEnum statusEnum
    ) {
        boolean success = appApprovalService.approval(userIdentification, approvalId, message, statusEnum, null);
        if (!success) {
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setFailed(MsgCodeEnum.w_data_approval_pass_fail);
            return resultDTO;
        }
        return new ResultDTO();
    }


    /**
     * 提交申请，发起审批
     * 数据访问规则
     *
     * @param dataAccessRuleList
     * @return
     */
    @PostMapping(value = "/data_access_rule")
    @ApiOperation(value = "资源访问规则")
    @AuditLog
    public ResultDTO<List<AppApprovalPo>> dataAccessRuleApproval(
            String approvalPerson,
            @RequestBody List<AppAccessRuleDo> dataAccessRuleList
    ) {

        ResultDTO<List<AppApprovalPo>> resultDTO = new ResultDTO<>();
        List<AppApprovalPo> approval = appApprovalService.createApproval(
                WebUtils.getCurrentUserInfo().getAccount(),
                ApprovalBusinessTypeEnum.DATA_ACCESS,
                dataAccessRuleList, approvalPerson);
        resultDTO.setData(approval);
        return resultDTO;
    }

    /**
     * 暂时没有用到，有需要可开放出来，需要像单个申请访问接口一样，加上api是否加密的判断
     */
    @Deprecated
    @PostMapping(value = "/approval")
    @ApiOperation(value = "批量申请访问", notes = "批量申请访问", httpMethod = "POST")
    @AuditLog
    public ResultDTO<List<AppApprovalPo>> approval(@RequestBody List<Integer> dataAssetIds) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        List<AppApprovalPo> appApprovalPos = new ArrayList<>();
        for (Integer dataAssetId : dataAssetIds) {
            appApprovalPos.add(appApprovalService.createApproval(
                    ipaasUserInfo.getUserIdentification(),
                    ApprovalBusinessTypeEnum.DATA_ACCESS,
                    dataAssetId
            ));
        }
        ResultDTO<List<AppApprovalPo>> listResultDTO = new ResultDTO<>();
        listResultDTO.setData(appApprovalPos);
        return listResultDTO;
    }

    @ApiOperation(value = "申请访问API", notes = "申请单个访问API", httpMethod = "POST")
    @ApiImplicitParam(name = "dataAssetApiId", value = "api id", required = true)
    @GetMapping(value = "/create.approval.api")
    public ResultDTO<AppApprovalPo> createApprovalApi(Integer dataAssetApiId) {
        AppApprovalPo appApprovalPo = appApprovalService.createApprovalApi(dataAssetApiId, IpaasUserContext.getUserInfo());
        return ResultDTO.success(appApprovalPo);
    }

    /**
     * 分页查询API审批列表
     *
     * @param approvalPo 查询条件
     * @param pageQuery  分页查询参数
     * @param keyword    API名称
     * @param lesseeName 租户名称
     * @return API审批列表
     */
    @GetMapping(value = "/query/api")
    @ApiOperation("分页查询API审批列表")
    public PageResultDTO<List<ApprovalRecordVO>> approvalApiRecord(AppApprovalPo approvalPo, PageQuery pageQuery, String keyword, String lesseeName) {
        return appApprovalService.approvalApiRecord(approvalPo, pageQuery.getPageNo(), pageQuery.getPageSize(), keyword, IpaasUserContext.getUserInfo(), lesseeName);
    }

    /**
     * 审核API
     *
     * @param approvalApiVO 审核API请求参数
     * @return Boolean
     */
    @PostMapping("/approval/api")
    @ApiOperation("审核API")
    public ResultDTO<Boolean> approvalApi(@RequestBody @Valid ApprovalApiVO approvalApiVO) {
        return ResultDTO.success(appApprovalService.approvalApi(approvalApiVO));
    }

    /**
     * 我的申请记录
     *
     * @param approvalType
     * @param statusEnum
     * @param dataAssetId
     * @param pageQuery
     * @param keyword
     * @return
     */
    @GetMapping(value = "/history")
    @ApiOperation("申请记录")
    @AuditLog
    public PageResultDTO<Page<ApprovalHistoryVo>> approvalHistory(
            ApprovalBusinessTypeEnum approvalType,
            ApprovalStatusEnum statusEnum,
            Integer dataAssetId,
            Integer accessAppId,
            PageQuery pageQuery,
            String keyword) {
        PageResultDTO<Page<ApprovalHistoryVo>> resultDTO = new PageResultDTO<>();
        AppApprovalPo appApproval = new AppApprovalPo();
        appApproval.setDataAssetId(dataAssetId);
        appApproval.setApprovalBusinessType(approvalType);
        appApproval.setApprovalStatus(statusEnum);
        appApproval.setUserIdentification(WebUtils.getCurrentUserInfo().getAccount());
        appApproval.setLesseeId(AuthUtils.currentAppId());
        appApproval.setAccessAppId(accessAppId);

        Page<ApprovalHistoryVo> page = appApprovalService.approvalHistory(
                appApproval,
                pageQuery.getPageNo(),
                pageQuery.getPageSize(),
                keyword
        );
        if (CollectionUtils.isNotEmpty(page)) {
            List<Map> list = appApprovalService.queryCollectInfo(
                    page.stream().map(ApprovalHistoryVo::getDataAssetApiId).collect(Collectors.toList()),
                    appApproval.getUserIdentification()
            );
            page.forEach(vo -> list.stream()
                    .anyMatch(map ->
                            {
                                if (map.get("dataAssetId").equals(vo.getDataAssetApiId()) &&
                                        map.get("accessAppId").equals(vo.getAccessAppId())) {
                                    vo.setIsCollect(true);
                                    return true;
                                } else {
                                    vo.setIsCollect(false);
                                }
                                return false;
                            }
                    ));
        }
        resultDTO.setData(page);
        resultDTO.setTotalCount(new Long(page.getTotal()).intValue());
        return resultDTO;
    }

    /**
     * 分页查询API审批列表
     *
     * @param approvalPo 查询条件
     * @param pageQuery  分页查询参数
     * @param keyword    API名称
     * @return 审批记录
     */
    @GetMapping(value = "/history/all")
    @ApiOperation("所有申请记录")
    @AuditLog
    public PageResultDTO<Page<ApprovalHistoryVo>> allApprovalHistory(AppApprovalPo approvalPo, PageQuery pageQuery, String keyword) {
        PageResultDTO<Page<ApprovalHistoryVo>> resultDTO = new PageResultDTO<>();
        approvalPo.setUserIdentification(null);
        approvalPo.setApprovalBusinessType(ApprovalBusinessTypeEnum.DATA_ACCESS);
        AuthUtils.setAppId(approvalPo);
        Page<ApprovalHistoryVo> data = appApprovalService.approvalHistory(approvalPo, pageQuery.getPageNo(), pageQuery.getPageSize(), keyword);
        resultDTO.setData(data);
        resultDTO.setTotalCount(new Long(data.getTotal()).intValue());
        return resultDTO;
    }


    /**
     * 申请数据访问规则详情
     *
     * @param approvalId
     * @param userIdentification
     * @return
     */
    @GetMapping(value = "/detail")
    @ApiOperation("申请详情")
    @AuditLog
    public ResultDTO<ApprovalDetailVo> dataAccessRuleHistory(
            @RequestParam Integer approvalId,
            @RequestParam(KEY_USER_IDENTIFICATION) String userIdentification
    ) {
        ResultDTO<ApprovalDetailVo> resultDTO = new ResultDTO<>();
        resultDTO.setData(appApprovalService.approvalDetail(userIdentification, approvalId));
        return resultDTO;
    }

    @ApiOperation(value = "主动授权")
    @PostMapping(value = "/authorize")
    @AuditLog
    public ResultDTO authorize(
            @RequestParam String userIdentification,
            @RequestBody List<AppAccessRuleDo> dataAccessRuleList) {
        List<AppApprovalPo> approval = appApprovalService.createApproval(
                userIdentification,
                ApprovalBusinessTypeEnum.DATA_ACCESS,
                dataAccessRuleList,
                null
        );
        for (AppApprovalPo appApprovalPo : approval) {
            boolean success = appApprovalService.approval(
                    userIdentification, appApprovalPo.getApprovalId(),
                    "主动授权", ApprovalStatusEnum.APPROVAL,
                    null
            );
            if (!success) {
                ResultDTO resultDTO = new ResultDTO();
                resultDTO.setFailed(MsgCodeEnum.w_data_approval_create_fail);
                return resultDTO;
            }
        }
        return new ResultDTO();
    }

    @ApiOperation(value = "撤消授权")
    @PostMapping(value = "/revoke")
    @AuditLog
    public ResultDTO revoke(@RequestBody List<AppAccessRuleDo> dataAccessRuleList) {
        appAccessRuleService.revokeAppAccessRule(dataAccessRuleList);
        return new ResultDTO();
    }

    @ApiOperation(value = "批量主动授权")
    @PostMapping(value = "/batchAuthorize")
    @AuditLog
    public ResultDTO<?> batchAuthorize(@Valid @RequestBody BatchAuthorizeReqParam param) {
        param.setHasAuth(Boolean.FALSE);
        List<AppAccessRuleDo> appAccessRuleDos = createAppAccessRuleDos(param);
        return authorize(WebUtils.getCurrentUserInfo().getAccount(), appAccessRuleDos);
    }

    @ApiOperation(value = "批量撤消授权")
    @PostMapping(value = "/batchRevoke")
    @AuditLog
    public ResultDTO<?> batchRevoke(@Valid @RequestBody BatchAuthorizeReqParam param) {
        param.setHasAuth(Boolean.TRUE);
        List<AppAccessRuleDo> appAccessRuleDos = createAppAccessRuleDos(param);
        return revoke(appAccessRuleDos);
    }

    private List<AppAccessRuleDo> createAppAccessRuleDos(BatchAuthorizeReqParam param) {
        //没有授权的列
        List<ApiConditionPo> apiConditionPos = apiConditionMapper
                .listByDataAssetId(param.getDataAssetApiId(), param.getDataAccessAppId(), param.getHasAuth());
        if (CollectionUtils.isEmpty(apiConditionPos)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }

        //apiId->api返回列
        Map<Integer, List<String>> idToColumns = apiConditionPos.stream().collect(
                Collectors.groupingBy(
                        ApiConditionPo::getDataAssetId,
                        Collectors.mapping(ApiConditionPo::getAssetColumns, Collectors.toList())));
        return param.getDataAssetApiId().stream().map(dataAssetApiId -> {
            AppAccessRuleDo appAccessRuleDo = new AppAccessRuleDo();
            appAccessRuleDo.setDataAccessAppId(param.getDataAccessAppId());
            appAccessRuleDo.setDataAssetApiId(dataAssetApiId);
            //授权列
            List<String> columnList = idToColumns.getOrDefault(dataAssetApiId, Collections.emptyList());
            appAccessRuleDo.setDataAccessRuleFieldList(columnList);

            return appAccessRuleDo;
        }).collect(Collectors.toList());
    }

}