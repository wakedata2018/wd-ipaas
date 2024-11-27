package com.wakedata.dw.open.controller.business;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import com.wakedata.dw.open.model.query.DataAccessQuery;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.vo.AppAccessRuleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yiyufeng
 * @title DataAccessRuleController
 * @projectName bdp-open
 * @date 2019/8/16 10:22
 * @descprition
 */
@RestController
@Slf4j
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/data_access_rule")
@Api(value = "数据访问规则", tags = "数据访问规则")
public class AppAccessRuleController {

    @Autowired
    private AppAccessRuleService appAccessRuleService;

    @GetMapping(value = "/get")
    @ApiOperation(value = "获取数据访问规则")
    @AuditLog
    public ResultDTO<AppAccessRuleDo> getDataAssetRule(
            @RequestParam Integer dataAssetApiId
    ) {
        ResultDTO<AppAccessRuleDo> resultDTO = new ResultDTO<>();
        resultDTO.setData(appAccessRuleService.getAppAccessRule(dataAssetApiId, null));
        return resultDTO;
    }

    /**
     * 我的API
     *
     * @param dataAccessAppId 申请人的APP_ID
     * @return
     */
    @GetMapping(value = "/app_access_rule")
    @ApiOperation(value = "接入端的数据访问规则")
    @AuditLog
    public PageResultDTO<Page<AppAccessRuleVo>> appDataAccessRule(
            @RequestParam Integer dataAccessAppId,
            Integer pageNo, Integer pageSize,
            String keyword,
            Integer dataSourceId) {
        PageResultDTO<Page<AppAccessRuleVo>> resultDTO = new PageResultDTO<>();
        Page<AppAccessRuleVo> data = appAccessRuleService.pageAppAccessRuleMultiDatasource(dataAccessAppId, pageNo, pageSize, keyword, dataSourceId);
        resultDTO.setTotalCount(new Long(data.getTotal()).intValue());
        resultDTO.setData(data);
        return resultDTO;
    }

    @GetMapping(value = "/app_asset_access_rule")
    @ApiOperation(value = "接入端的数据对某个资源的访问规则")
    @AuditLog
    public ResultDTO<AppAccessRuleVo> appDataAccessRule(
            @RequestParam Integer dataAccessAppId,
            @RequestParam Integer dataAssetId) {
        ResultDTO<AppAccessRuleVo> resultDTO = new ResultDTO<>();
        resultDTO.setData(appAccessRuleService.appAccessRule(dataAccessAppId, dataAssetId));
        return resultDTO;
    }

    @PostMapping(value = "/validate")
    @ApiOperation("校验规则")
    @AuditLog
    public ResultDTO<Boolean> dataAssetRuleValidate(
            @RequestBody DataAccessQuery dataAccessQuery,
            @RequestParam Integer dataAccessRuleId
    ) {
        AppAccessRuleDo dataAccessRule = appAccessRuleService.getAppAccessRule(dataAccessRuleId, null);
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
        resultDTO.setData(appAccessRuleService.validateAppAccessRule(dataAccessQuery.getReqField().keySet(), dataAccessQuery.getRespField(), dataAccessRule));
        return resultDTO;
    }
}
