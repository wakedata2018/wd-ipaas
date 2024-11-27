package com.wakedata.dw.open.service.log;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.mapper.log.AccessLogMapper;
import com.wakedata.dw.open.model.domain.AppAccessDo;
import com.wakedata.dw.open.model.domain.AppInvokeDo;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.model.query.StatisticQuery;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.ApiStatisticsService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.vo.StatisticsIndexVo;
import com.wakedata.dw.open.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tanzhi
 * @title AccessLogServiceImpl
 * @date 2019/11/22 9:33
 * @projectName dw-open
 * @descriptor
 */
@Service
public class AccessLogServiceImpl extends BaseServiceImpl<AccessLogPo, AccessLogMapper> implements AccessLogService {

    private static final HashMap<Integer, String> ERROR_MAP = new HashMap<>();

    static {
        MsgCodeEnum[] values = MsgCodeEnum.values();
        for (MsgCodeEnum value : values) {
            ERROR_MAP.put(value.getCode(), value.getDesc());
        }
        OpenApiMsgCodeEnum[] openApiMsgCodeEnums = OpenApiMsgCodeEnum.values();
        for (OpenApiMsgCodeEnum value : openApiMsgCodeEnums) {
            ERROR_MAP.put(value.getCode(), value.getDesc());
        }
    }

    @Autowired
    private DataAssetApiMapper dataAssetApiMapper;
    @Autowired
    private AppAccessMapper appAccessMapper;
    @Autowired
    private AccessLogMapper accessLogMapper;
    @Autowired
    private ApiStatisticsService apiStatisticService;

    @Autowired
    @Override
    protected void init(CurdService<AccessLogPo> curdService, AccessLogMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public StatisticsIndexVo statistics(StatisticQuery query) {

        Long lesseeId = query.getLesseeId();
        StatisticsIndexVo statisticsIndexVo = new StatisticsIndexVo();

        // 统计范围：按天统计
        // 1 API累计调用次数
        statisticsIndexVo.setApiTotal(getMapper().countAllToday(lesseeId,DateUtils.getTodayZero()));
        // 2 API开放量(发布数) OK
        statisticsIndexVo.setPublished(dataAssetApiMapper.publishedCount(lesseeId));
        // 3 API申请方(接入数) ok
        statisticsIndexVo.setApps(appAccessMapper.count(lesseeId));
        // 4 API调用异常数
        statisticsIndexVo.setErrors(apiStatisticService.totalErrorToday(lesseeId,DateUtils.getTodayZero()));

        // 统计范围：近7天
        // 5 API访问次数排名
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByAccessMethod = apiStatisticService.totalGroupByAccessMethodWeek(lesseeId,DateUtils.getBeforeWeekDay());
        statisticsIndexVo.setGroupByAccessMethod(groupByAccessMethod);
        // 6 API调用时长排名
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByElapsed = apiStatisticService.totalGroupByElapsedWeek(lesseeId,DateUtils.getBeforeWeekDay());
        statisticsIndexVo.setGroupByElapsed(groupByElapsed);
        // 8 API调用方次数排名(只统计已上线的api)
        PageHelper.startPage(1, 10);
//        List<AppAccessDo> groupByAccessApp = apiStatisticService.totalGroupByAccessAppWeek(lesseeId,DateUtils.getBeforeWeekDay());
        Date date = new Date();
        List<AppAccessDo> groupByAccessApp = apiStatisticService.pastGroupByApiGroupAndToday(DateUtils.getBeforeWeekDay(), date,lesseeId,DateUtils.getTodayZero());
        statisticsIndexVo.setGroupByAccessApp(groupByAccessApp);

        // 9 接口分类的API分析
        PageHelper.startPage(1, 10);
        // 统计范围：本月1号 00:00:00到现在(本月)
        List<AppAccessDo> groupByApiGroup = apiStatisticService.pastGroupByApiGroupAndToday(DateUtils.getStartMonth(date), date,lesseeId,DateUtils.getTodayZero());
        Date beforeMonthDay = DateUtils.getBeforeMonthDay();
        // 统计范围：上个月1号 00:00:00->下个月1号 00:00:00(上个月)
        List<AppAccessDo> groupByApiGroup1 = apiStatisticService.pastGroupByApiGroup(DateUtils.getStartMonth(beforeMonthDay), DateUtils.getStartMonth(date),lesseeId);
        // 将数据合并到groupByApiGroup
        mergeGroupByApiGroup(groupByApiGroup, groupByApiGroup1);
        statisticsIndexVo.setGroupByApiGroup(groupByApiGroup);

        // 统计范围：30天前->现在(近30天)
        // 10 错误类型
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByResultCode = apiStatisticService.totalGroupByResultCodeMonth(lesseeId,DateUtils.getBefore30Day());
        for (AppAccessDo appAccessDo : groupByResultCode) {
            if(StringUtils.isNotBlank(appAccessDo.getResultDescription())){
                appAccessDo.setPrimaryName(appAccessDo.getResultDescription());
            }else {
                appAccessDo.setPrimaryName(ERROR_MAP.get(appAccessDo.getId()));
            }
        }
        statisticsIndexVo.setGroupByResultCode(groupByResultCode);
        return statisticsIndexVo;
    }

    private void mergeGroupByApiGroup(List<AppAccessDo> groupByApiGroup, List<AppAccessDo> groupByApiGroup1) {
        //将上月的数据转换成map
        Map<Integer, AppAccessDo> map = groupByApiGroup1.stream().collect(Collectors.toMap(AppAccessDo::getId, AppAccessDo -> AppAccessDo));
        //将上月的数据的合并到groupByApiGroup中
        groupByApiGroup.forEach(m ->{
            if (map.containsKey(m.getId())) {
                m.setLastResultValue(map.get(m.getId()).getResultValue());
                map.remove(m.getId());
            }
        });
        //将上个月有但是这个月没有的分类调用记录放入这个月的数据中
        if (!map.isEmpty()){
            List<AppAccessDo> list = new ArrayList<>(map.values());
            list.forEach(n->{
                n.setLastResultValue(n.getResultValue());
                n.setResultValue(0);
            });
            groupByApiGroup.addAll(list);
        }
    }

    @Override
    public StatisticsIndexVo statistics(String date) {
        StatisticsIndexVo statisticsIndexVo = new StatisticsIndexVo();
//        statisticsIndexVo.setPlatformTotal(userLoginMapper.loginCount(date, date + " 23:59:59"));
        //1,统计API累计调用次数
        //statisticsIndexVo.setApiTotal(getMapper().countAll(lesseeId));
        //2,API发布数 OK
        //statisticsIndexVo.setPublished(dataAssetApiMapper.publishedCount(lesseeId));
        //3,API接入数 ok
        //statisticsIndexVo.setApps(dataAccessAppMapper.count(lesseeId));
        //4,API调用异常数
/*        statisticsIndexVo.setErrors(apiStatisticService.totalError(lesseeId));
        //6,API访问次数排名  ok
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByAccessMethod = apiStatisticService.pastGroupByAccessMethod(date);
        statisticsIndexVo.setGroupByAccessMethod(groupByAccessMethod);
        //7,API调用平均时长排名
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByElapsed = apiStatisticService.pastGroupByElapsed(date);
        statisticsIndexVo.setGroupByElapsed(groupByElapsed);
        //8，API调用方排名 ok
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByAccessApp = apiStatisticService.pastGroupByAccessApp(date);
        statisticsIndexVo.setGroupByAccessApp(groupByAccessApp);
        //9,不同主题分析 ok
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByApiGroup = apiStatisticService.pastGroupByApiGroup(date);
        statisticsIndexVo.setGroupByApiGroup(groupByApiGroup);
        //10,错误类型
        PageHelper.startPage(1, 10);
        List<AppAccessDo> groupByResultCode = apiStatisticService.pastGroupByResultCode(date);
        for (AppAccessDo appAccessDo : groupByResultCode) {
            appAccessDo.setPrimaryName(ERROR_MAP.get(appAccessDo.getId()));
        }
        statisticsIndexVo.setGroupByResultCode(groupByResultCode);*/
        return statisticsIndexVo;
    }


    @Override
    public Page<AppAccessDo> apiError(StatisticQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        Page<AppAccessDo> appAccessDos = (Page<AppAccessDo>) accessLogMapper.apiError(query);
        if (CollectionUtils.isEmpty(appAccessDos)) {
            return null;
        }

        // code不是200/1010(统一API调用返回的错误) resultDescription字段为空
        appAccessDos.stream()
                .filter(appAccessDo -> StringUtils.isEmpty(appAccessDo.getResultDescription()))
                .forEach(appAccessDo -> appAccessDo.setResultDescription(ERROR_MAP.get(appAccessDo.getResultValue())));
        return appAccessDos;
    }

    @Override
    public Page<AppInvokeDo> appInvokeInfo(StatisticQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        return (Page<AppInvokeDo>) accessLogMapper.appInvokeInfo(query);
    }

    @Override
    @Async
    public Integer addApiAccessLog(AccessLogPo accessLogPo) {
        return this.getCurdService().insertSelective(accessLogPo, accessLogMapper);
    }

    @Override
    public AccessLogPo getAccessLogInfo(Integer id) {
        return this.getCurdService().selectByPrimaryKey(id,accessLogMapper);
    }

    @Override
    public Page<AppAccessDo> apiInvoke(StatisticQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        Page<AppAccessDo> appAccessDos = (Page<AppAccessDo>) accessLogMapper.apiInvoke(query);
        if (CollectionUtils.isEmpty(appAccessDos)) {
            return null;
        }

        // code不是200/1010(统一API调用返回的错误) resultDescription字段为空
        appAccessDos.stream()
                .filter(appAccessDo -> StringUtils.isEmpty(appAccessDo.getResultDescription()))
                .forEach(appAccessDo -> appAccessDo.setResultDescription(ERROR_MAP.get(appAccessDo.getResultValue())));
        return appAccessDos;
    }
}
