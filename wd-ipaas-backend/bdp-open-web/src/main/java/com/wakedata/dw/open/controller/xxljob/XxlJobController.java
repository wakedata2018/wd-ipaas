package com.wakedata.dw.open.controller.xxljob;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.XxlJobQuery;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;
import com.wakedata.dw.open.service.xxljob.XxlJobService;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobController
 * @date 2022/10/25 14:13
 */
@Validated
@Api(value = "定时任务接口", tags = "定时任务接口")
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/xxl")
public class XxlJobController {

    @Resource
    private XxlJobService xxlJobService;

    @ApiOperation("查询XxlJob任务列表")
    @PostMapping(value = "/page.list")
    @ApiImplicitParam(name = "id", value = "id", required = true)
    @AuditLog
    public PageResultDTO<List<XxlJobDTO>> temporaryApiPageList(@RequestBody @Valid XxlJobQuery xxlJobQuery){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        xxlJobQuery.setLesseeId(userInfo.getIsPlatformAdmin() ? null : userInfo.getLesseeId());
        return xxlJobService.pageList(xxlJobQuery);
    }

    @ApiOperation("新增定时任务")
    @PostMapping(value = "/add")
    @AuditLog
    public ResultDTO<Boolean> addTemporaryApi(@RequestBody @Valid XxlJobDTO xxlJobDTO){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        xxlJobDTO.setLesseeId(userInfo.getLesseeId());
        xxlJobDTO.setCreateBy(userInfo.getUserIdentification());
        xxlJobDTO.setCreateTime(new Date());
        xxlJobDTO.setUpdateBy(userInfo.getUserIdentification());
        xxlJobDTO.setUpdateTime(new Date());
        xxlJobDTO.setTaskExecuteAmount(0L);
        return xxlJobService.addXxlJob(xxlJobDTO);
    }

    @ApiOperation("更新定时任务")
    @PostMapping(value = "/update")
    @AuditLog
    public ResultDTO<Boolean> updateTemporaryApi(@RequestBody @Valid XxlJobDTO xxlJobDTO){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        xxlJobDTO.setLesseeId(userInfo.getLesseeId());
        xxlJobDTO.setUpdateBy(userInfo.getUserIdentification());
        xxlJobDTO.setUpdateTime(new Date());
        xxlJobDTO.setXxlTimeStamp(null);
        return xxlJobService.updateXxlJob(xxlJobDTO);
    }

    @ApiOperation("(批量)删除定时任务")
    @GetMapping("/delete")
    public ResultDTO<Boolean> deleteByIds(Integer id){
        return xxlJobService.deleteXxlJob(id);
    }
}
