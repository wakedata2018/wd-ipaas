package com.wakedata.dw.open.controller.business;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.BlackListPo;
import com.wakedata.dw.open.service.api.BlackListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzheng
 * @title BlackListController
 * @date 2021/4/6 14:33
 * @projectName bdp-open
 * @description
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/blacklist")
@Api(value = "IP黑名单", tags = "IP黑名单")
public class BlackListController extends BaseController {

    @Autowired
    private BlackListService blackListService;


    @GetMapping(value = "/show")
    @ApiOperation(value = "查询黑名单")
    @AuditLog
    public ResultDTO<List<BlackListPo>> show(@ApiParam(value = "接入应用ID", required = true) @RequestParam Integer appId) {
        List<BlackListPo> blackListPos = blackListService.showBlackList(appId);
        ResultDTO<List<BlackListPo>> listResultDTO = new ResultDTO<>();
        listResultDTO.setData(blackListPos);
        return listResultDTO;
    }

    @Transactional
    @PostMapping(value = "/add")
    @ApiOperation(value = "添加黑名单/修改黑名单")
    @AuditLog
    public ResultDTO addToBlackList(Integer appId, String ips) {
        blackListService.addToBlackList(appId, ips);
        return new ResultDTO();
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "从黑名单中删除")
    @AuditLog
    public ResultDTO deleteFromBlackList(Integer appId) {
        blackListService.delete(appId);
        return new ResultDTO();
    }

}
