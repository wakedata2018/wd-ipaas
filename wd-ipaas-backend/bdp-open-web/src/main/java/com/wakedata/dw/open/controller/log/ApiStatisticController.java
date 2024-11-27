package com.wakedata.dw.open.controller.log;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.business.AppApprovalController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.IntervalType;
import com.wakedata.dw.open.model.domain.AppAccessDo;
import com.wakedata.dw.open.model.domain.AppInvokeDo;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.query.StatisticQuery;
import com.wakedata.dw.open.service.api.ApiStatisticsService;
import com.wakedata.dw.open.service.log.AccessLogService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.vo.StatisticsIndexVo;
import com.wakedata.dw.open.utils.DateUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title ApiStatisticController
 * @date 2019/11/30 16:32
 * @projectName bdp-open
 * @descriptor
 */
@RestController
@Api(value = "统计分析", tags = "统计分析")
@RequestMapping("${spring.mvc.backend.prefix}/business/statistics")
public class ApiStatisticController {

    //1,统计API累计调用次数  ok
    //2,API发布数 OK
    //3,API接入数 ok
    //4,API异常数 ok
    //5,平台访问次数
    //6,API访问次数排名  ok
    //7,API调用平均时长排名 ok
    //8，API调用方排名 ok
    //9,不同主题分析 ok
    //10,错误类型分布 ok
    //11，我的API统计

    @Autowired
    private ApiStatisticsService apiStatisticsService;

    @Autowired
    private AccessLogService accessLogService;

    @GetMapping("/index")
    @ApiOperation("首页统计数据查询")
    @AuditLog
    public ResultDTO index(String date) {

        // 平台管理员不需要租户隔离 开发者需要租户隔离
        StatisticQuery query = new StatisticQuery();
        if (!IpaasUserContext.getUserInfo().getIsPlatformAdmin()){
            query.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        }

        StatisticsIndexVo statistics = null;
        if (StringUtils.isBlank(date)) {
            statistics = accessLogService.statistics(query);

        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(new Date());
            if (format.equals(date)) {
                statistics = accessLogService.statistics(query);
            } else {
                statistics = accessLogService.statistics(date);
            }
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(statistics);
        return resultDTO;
    }


    /**
     * 所有接口的累计调用次数
     *
     * @return
     */
    @GetMapping(value = "/total_call_times")
    @ApiOperation("API总记调用次数")
    public ResultDTO totalCallTimes() {
        ResultDTO resultDTO = new ResultDTO();
        Integer l = apiStatisticsService.totalCallTimes();
        resultDTO.setData(l);
        return resultDTO;
    }

    /**
     * 数据接口调用次数分组统计
     * 依赖每15分钟的定时任务和每天凌晨3点的定时任务
     *
     * @return
     */
    @GetMapping(value = "/access_method_times")
    @ApiOperation("各个API调用次数")
    public ResultDTO groupByAccessMethod() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(apiStatisticsService.totalGroupByAccessMethod(AuthUtils.currentAppId(),DateUtils.getTodayZero()));
        return resultDTO;
    }

    @GetMapping(value = "/access_app_times")
    @ApiOperation("各个API调用次数")
    public ResultDTO groupByAccessApp() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(apiStatisticsService.totalGroupByAccessApp(AuthUtils.currentAppId()));
        return resultDTO;
    }

    @GetMapping(value = "/result_code_times")
    @ApiOperation("各个API调用次数")
    public ResultDTO groupByResultCode() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(apiStatisticsService.totalGroupByResultCode(AuthUtils.currentAppId(), DateUtils.getTodayZero()));
        return resultDTO;
    }

    @GetMapping(value = "/api_group_times")
    @ApiOperation("API分组调用次数")
    public ResultDTO groupByApiGroup() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(apiStatisticsService.totalGroupByApiGroup());
        return resultDTO;
    }

    @GetMapping(value = "/api_elapsed_times")
    @ApiOperation("API调用平均时间")
    public ResultDTO groupByElapsed() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setData(apiStatisticsService.totalGroupByElapsed(AuthUtils.currentAppId()));
        return resultDTO;
    }

    @PostMapping(value = "/api_right_rate")
    @ApiOperation("API调用统计查询")
    @AuditLog
    public PageResultDTO<List<AppAccessDo>> groupByRightRate(@RequestBody StatisticQuery query) {
        query.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        return apiStatisticsService.totalGroupByRightRate(query);
    }

    @PostMapping("/api_error")
    @ApiOperation("api错误原因查询")
    @AuditLog
    public PageResultDTO<Page<AppAccessDo>> apiError(@RequestBody StatisticQuery query) {
        query.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        Page<AppAccessDo> appAccessDos = accessLogService.apiError(query);
        PageResultDTO<Page<AppAccessDo>> resultDTO = new PageResultDTO<Page<AppAccessDo>>();
        resultDTO.setData(appAccessDos);
        if (appAccessDos != null) {
            resultDTO.setTotalCount((int) appAccessDos.getTotal());
        }
        return resultDTO;
    }

    @PostMapping("/app_invoke")
    @ApiOperation("应用访问统计查询")
    @AuditLog
    public PageResultDTO<Page<AppInvokeDo>> appInvokeInfo(@RequestBody StatisticQuery query){
        query.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        Page<AppInvokeDo> appInvokeDos = accessLogService.appInvokeInfo(query);
        PageResultDTO<Page<AppInvokeDo>> resultDTO = new PageResultDTO<>();
        resultDTO.setData(appInvokeDos);
        if (appInvokeDos != null) {
            resultDTO.setTotalCount((int) appInvokeDos.getTotal());
        }
        return resultDTO;
    }

    @GetMapping("/getAccessLogInfo")
    @ApiOperation("获取应用访问日志详情")
    @AuditLog
    public ResultDTO<AccessLogPo> getAccessLogInfo(@RequestParam("id") Integer id){
        return ResultDTO.success(accessLogService.getAccessLogInfo(id));
    }


    @GetMapping(value = "/group_by_time_unit")
    @ApiOperation("API一定时间内调用次数")
    public ResultDTO<List<AppApprovalController>> groupIn24Hours(String accessMethod, Integer num, IntervalType type, Integer accessAppId) {
        ResultDTO resultDTO = new ResultDTO();
        num = Math.min(num, 30);
        resultDTO.setData(apiStatisticsService.groupByTimeUnit(accessMethod, num, type, accessAppId));
        return resultDTO;
    }

    @PostMapping("/api_invoke")
    @ApiOperation("api调用列表")
    @AuditLog
    public PageResultDTO<Page<AppAccessDo>> apiInvoke(@RequestBody StatisticQuery query) {
        query.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        Page<AppAccessDo> appAccessDos = accessLogService.apiInvoke(query);
        PageResultDTO<Page<AppAccessDo>> resultDTO = new PageResultDTO<Page<AppAccessDo>>();
        resultDTO.setData(appAccessDos);
        if (appAccessDos != null) {
            resultDTO.setTotalCount((int) appAccessDos.getTotal());
        }
        return resultDTO;
    }


}
