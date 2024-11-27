package com.wakedata.dw.open.controller.business;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.WhiteListPo;
import com.wakedata.dw.open.service.api.WhiteListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author tanzhi
 * @title WhiteListController
 * @projectName bdp-open
 * @date 2019/9/24 14:29
 * @description
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/whitelist")
@Api(value = "IP白名单", tags = "IP白名单")
public class WhiteListController extends BaseController {

    @Autowired
    private WhiteListService whiteListService;

    @PostMapping("/add")
    @ApiOperation("添加白名单")
    @AuditLog
    public ResultDTO addToWhiteList(Integer accessAppId, String ips) {
        whiteListService.addToAccessList(accessAppId, ips);
        return new ResultDTO();
    }

    @PostMapping("/delete")
    @ApiOperation("删除白名单")
    @AuditLog
    public ResultDTO deleteFromWhiteList(Integer whiteListId) {
        whiteListService.delete(whiteListId);
        return new ResultDTO();
    }

    @PostMapping("/show")
    @ApiOperation("查询白名单")
    @AuditLog
    public ResultDTO<List<WhiteListPo>> show(Integer accessAppId) {
        List<WhiteListPo> whiteListPos = whiteListService.showList(accessAppId);
        ResultDTO<List<WhiteListPo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(whiteListPos);
        return resultDTO;
    }
}
