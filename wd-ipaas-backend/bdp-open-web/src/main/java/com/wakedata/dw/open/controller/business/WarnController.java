package com.wakedata.dw.open.controller.business;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.domain.ApiInfoDo;
import com.wakedata.dw.open.model.domain.ApiWarnDo;
import com.wakedata.dw.open.model.domain.ApiWarnQueryDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.warn.ApiWarnPo;
import com.wakedata.dw.open.service.api.DataAssetApiWarnService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wq
 * @title WarnController
 * @date 2020/9/21 11:31
 * @projectName dw-open
 * @description
 */
@RestController
@Api(value = "监控告警",tags = "告警相关接口")
@RequestMapping("${spring.mvc.backend.prefix}/business/warn")
public class WarnController extends BaseController {
    @Autowired
    private DataAssetApiWarnService service;

    @PostMapping("/add")
    @ApiOperation("新增相关告警")
    @AuditLog
    public ResultDTO<Integer> addWarn(ApiWarnPo apiWarnPo){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.addWarn(apiWarnPo));
        return resultDTO;
    }

    @PostMapping("/update")
    @ApiOperation("编辑告警")
    @AuditLog
    public ResultDTO<Integer> updateWarn(ApiWarnPo apiWarnPo){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.updateWarn(apiWarnPo));
        return resultDTO;
    }

    @PostMapping("/delete")
    @ApiOperation("删除告警")
    @AuditLog
    public ResultDTO<Integer> deleteWarn(@RequestParam Integer apiWarnId){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.deleteWarn(apiWarnId));
        return resultDTO;
    }

    @PostMapping("/status")
    @ApiOperation("启用禁用告警")
    @AuditLog
    public ResultDTO<Integer> status(@RequestParam Integer apiWarnId , @RequestParam Boolean status){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.status(apiWarnId,status));
        return resultDTO;
    }

    @GetMapping("/query")
    @ApiOperation("告警查询")
    @AuditLog
    public ResultDTO<Page<ApiWarnDo>> queryWarn(ApiWarnQueryDo apiWarnQueryDo, PageQuery pageQuery){
        apiWarnQueryDo.setLesseeId(AuthUtils.currentAppId());
        Page<ApiWarnDo> apiWarnDos = service.queryWarn(apiWarnQueryDo,pageQuery);
        PageResultDTO<Page<ApiWarnDo>> resultDTO = new PageResultDTO<>();
        resultDTO.setData(apiWarnDos);
        if (CollectionUtils.isNotEmpty(apiWarnDos)) {
            resultDTO.setTotalCount((int)apiWarnDos.getTotal());
        }else {
            resultDTO.setTotalCount(0);
        }
        return resultDTO;
    }

    @GetMapping("/query/api/group")
    @ApiOperation("查询api主题")
    public ResultDTO<List<ApiGroupPo>> getGroupList(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.getGroupList());
        return resultDTO;
    }

    @GetMapping("/query/api")
    @ApiOperation("通过api主题查api")
    public ResultDTO<List<ApiInfoDo>> queryApiByGroup(@RequestParam Integer groupId){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(service.queryApiByGroup(groupId));
        return resultDTO;
    }
}
