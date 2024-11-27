package com.wakedata.dw.open.controller.business;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.ApiRulePo;
import com.wakedata.dw.open.model.domain.ApiRuleDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.DataAssetApiRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wq
 * @title RuleController
 * @date 2020/10/19 10:44
 * @projectName dw-open
 * @description
 */
@RestController
@Api(value = "流量控制",tags = "限流规则相关接口")
@RequestMapping("${spring.mvc.backend.prefix}/business/rule")
public class RuleController extends BaseController {
    @Autowired
    private DataAssetApiRuleService service;

    @ApiOperation("新增限流规则")
    @PostMapping("/add")
    @AuditLog
    public ResultDTO add(ApiRulePo po) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.addRule(po));
        return resultDTO;
    }

    @PostMapping("/update")
    @ApiOperation("编辑限流规则")
    @AuditLog
    public ResultDTO<Integer> updateWarn(ApiRulePo po){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.updateRule(po));
        return resultDTO;
    }

    @PostMapping("/delete")
    @ApiOperation("删除限流规则")
    @AuditLog
    public ResultDTO<Integer> deleteWarn(@RequestParam Integer apiRuleId){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.deleteRule(apiRuleId));
        return resultDTO;
    }


    @GetMapping("/query")
    @ApiOperation("限流规则查询")
    @AuditLog
    public ResultDTO<Page<ApiRuleDo>> queryWarn(Integer groupId, String apiKeyWord, PageQuery pageQuery){
        Page<ApiRuleDo> apiRules = service.queryRule(groupId,apiKeyWord,pageQuery);
        PageResultDTO<Page<ApiRuleDo>> resultDTO = new PageResultDTO<>();
        resultDTO.setData(apiRules);
        if (CollectionUtils.isNotEmpty(apiRules)) {
            resultDTO.setTotalCount((int)apiRules.getTotal());
        }else {
            resultDTO.setTotalCount(0);
        }
        return resultDTO;
    }
}