package com.wakedata.dw.open.service.log;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.enums.IntervalType;
import com.wakedata.dw.open.mapper.AccessStatisticsMapper;
import com.wakedata.dw.open.mapper.OptimisticLockMapper;
import com.wakedata.dw.open.mapper.UserPageActionMapper;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.mapper.log.AccessLogMapper;
import com.wakedata.dw.open.mapper.log.OperatorLogMapper;
import com.wakedata.dw.open.model.OptimisticLockPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.domain.AppAccessDo;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.model.log.AccessStatisticsPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.query.StatisticQuery;
import com.wakedata.dw.open.service.api.ApiStatisticsService;
import com.wakedata.dw.open.service.lock.OptimisticLockService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tanzhi
 * @title ApiStatisticServiceImpl
 * @projectName bdp-open
 * @date 2019/8/23 10:15
 * @description api使用次数统计
 */
@EnableScheduling
@Service
@Slf4j
public class ApiStatisticServiceImpl implements ApiStatisticsService {

    private static final long QUARTER_MILLISECOND = 15 * 60 * 1000;
    private static final long DAILY_MILLISECOND = 24 * 60 * 60 * 1000;
    private static final int DAILY_LOCK_ID = 1;
    private static final int QUARTER_LOCK_ID = 2;

    //按访问URL统计
    private static final int METHOD_ID = 10;
    //按接入端统计
    private static final int APP_ID = 20;
    //按错误码统计
    private static final int RESULT_CODE = 30;
    //调用时长
    private static final int ELAPSED_CODE = 40;

    //正确率
    private static final int RIGHT_RATE_CODE = 50;

    private static String IP;


    @Autowired
    private AccessLogMapper accessLogMapper;
    @Autowired
    private AccessStatisticsMapper accessStatisticsMapper;
    @Autowired
    private OptimisticLockService optimisticLockService;
    @Autowired
    private OptimisticLockMapper optimisticLockMapper;
    @Autowired
    private OperatorLogMapper operatorLogMapper;
    @Autowired
    private UserPageActionMapper userPageActionMapper;
    @Autowired
    private AppAccessMapper appAccessMapper;


    static {
        InetAddress localHost = null;
        try {
            localHost = Inet4Address.getLocalHost();
            IP = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * 每天统计每个接口的调用次数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Override
    public void dailyTask() {
        runTask(DAILY_LOCK_ID, DAILY_MILLISECOND);
    }

    /**
     * 每15分钟统计每个接口的调用次数
     */
    @Scheduled(fixedRate = QUARTER_MILLISECOND)
    @Override
    public void quarterTask() {
        runTask(QUARTER_LOCK_ID, QUARTER_MILLISECOND);
    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanUpDb() {
        //清理日志
        accessLogMapper.cleanUp();
        operatorLogMapper.cleanUp();
        userPageActionMapper.cleanUp();

    }

    /**
     * 所有接口累计调用次数
     *
     * @return
     */
    @Override
    public Integer totalCallTimes() {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        //未被定时任务统计的
        Integer unProcessedCount = accessLogMapper.count(dataTime, endTime);
        //已被定时任务统计的分两种，一种是天任务，一种是刻任务
        Integer processedCount = accessStatisticsMapper.totalCallTimes();
        //三者结果累加
        return unProcessedCount + processedCount;
    }

    @Override
    public List<AppAccessDo> totalGroupByAccessMethod(Long lesseeId, Date todayZero) {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        List<AppAccessDo> groupByAccessMethod = accessLogMapper.totalGroupByAccessMethod(dataTime, endTime, lesseeId, todayZero);
        return groupByAccessMethod;
    }

    @Override
    public List<AppAccessDo> totalGroupByAccessApp(Long lesseeId) {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.totalGroupByAccessApp(dataTime, endTime, lesseeId);
        return groupByAccessApp;
    }

    @Override
    public List<AppAccessDo> totalGroupByApiGroup() {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.totalGroupByApiGroup(dataTime, endTime);
        return groupByAccessApp;
    }

    @Override
    public List<AppAccessDo> totalGroupByElapsed(Long lesseeId) {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.totalGroupByElapsed(dataTime, endTime, lesseeId);
        return groupByAccessApp;

    }

    private Date[] getDateArray(String date) {
        Date[] dates = new Date[2];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = sdf.parse(date);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(parse);
            dates[0] = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dates[1] = calendar.getTime();

        } catch (ParseException e) {
            return null;
        }
        return dates;


    }

    @Override
    public List<AppAccessDo> pastGroupByAccessMethod(String date) {
        Date[] dateArray = getDateArray(date);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByAccessMethod(dateArray[0], dateArray[1]);
        return groupByAccessApp;
    }

    @Override
    public List<AppAccessDo> pastGroupByAccessApp(String date) {
        Date[] dateArray = getDateArray(date);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByAccessApp(dateArray[0], dateArray[1]);
        return groupByAccessApp;

    }

    @Override
    public List<AppAccessDo> pastGroupByApiGroup(String date) {
        Date[] dateArray = getDateArray(date);
        //List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByApiGroup(dateArray[0], dateArray[1], lesseeId);
        //return groupByAccessApp;
        return null;
    }

    @Override
    public List<AppAccessDo> pastGroupByApiGroup(Date from, Date to, Long lesseeId) {
        List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByApiGroup(from, to, lesseeId);
        return groupByAccessApp;


    }

    @Override
    public List<AppAccessDo> pastGroupByResultCode(String date) {
        Date[] dateArray = getDateArray(date);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByResultCode(dateArray[0], dateArray[1]);
        return groupByAccessApp;
    }

    @Override
    public List<AppAccessDo> pastGroupByElapsed(String date) {
        Date[] dateArray = getDateArray(date);
        List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByElapsed(dateArray[0], dateArray[1]);
        return groupByAccessApp;
    }


    @Override
    public PageResultDTO<List<AppAccessDo>> totalGroupByRightRate(StatisticQuery query) {

        // 列表默认查询当天
        if (ObjectUtil.isEmpty(query.getStartTime()) && ObjectUtil.isEmpty(query.getEndTime())){
            Calendar calendar = new GregorianCalendar();
            query.setStartTime(com.wakedata.dw.open.utils.DateUtils.getStartTime(calendar));
            query.setEndTime(com.wakedata.dw.open.utils.DateUtils.getEndTime(calendar));
        }

        List<AppAccessDo> appAccessDos =
                Optional.ofNullable(accessLogMapper.totalGroupByRightRateBefore(query)).orElse(new ArrayList<>());
        List<AppAccessDo> appAccessDoToday;
        //如果包含今天,就在log表查取今天的数据，加上记录表数据即可
        if (query.getEndTime() == null || DateUtils.isSameDay(query.getEndTime(), new Date()) || query.getEndTime().after(new Date())) {
            appAccessDoToday = Optional.ofNullable(accessLogMapper.totalGroupByRightRateToday(query)).orElse(new ArrayList<>());
            appAccessDos.addAll(appAccessDoToday);

            Map<Integer, List<AppAccessDo>> collect = appAccessDos.stream().collect(Collectors.groupingBy(AppAccessDo::getId,LinkedHashMap::new,Collectors.toList()));
            List<AppAccessDo> resultList = new Page<>();
            collect.forEach((k, v) -> {
                AppAccessDo appAccessDo = v.get(0);
                Integer resultSum = v.stream().map(AppAccessDo::getResultValue).reduce(0, Integer::sum);
                Integer errorSum = v.stream().map(AppAccessDo::getErrorValue).reduce(0, Integer::sum);
                BigDecimal takeTime = v.stream().map(AppAccessDo::getTakeTime).reduce(BigDecimal.ZERO, BigDecimal::add);
                int times = v.stream().map(AppAccessDo::getTimes).reduce(0, Integer::sum);
                if (times != 0) {
                    appAccessDo.setTakeTime(takeTime.divide(new BigDecimal(times * 1000), 2, RoundingMode.HALF_UP));
                }
                appAccessDo.setResultValue(resultSum);
                appAccessDo.setErrorValue(errorSum);
                resultList.add(appAccessDo);
            });
            appAccessDos = resultList;
        } else {
            appAccessDos.forEach(vo -> {
                vo.setTakeTime(vo.getTimes() == 0 ? BigDecimal.ZERO : vo.getTakeTime().divide(new BigDecimal(
                        vo.getTimes() * 1000), 2, RoundingMode.HALF_UP));
            });
        }
        PageResultDTO<List<AppAccessDo>> pageResult = new PageResultDTO<>();
        if (CollectionUtil.isNotEmpty(appAccessDos)) {
            pageResult.setData(appAccessDos.stream().skip((long) query.getPageSize() * (query.getPageNo() - 1))
                                       .limit(query.getPageSize()).collect(Collectors.toList()));
            pageResult.setTotalCount(appAccessDos.size());
        } else {
            pageResult.setData(new ArrayList<>());
            pageResult.setTotalCount(0);
        }
        return pageResult;
    }

    @Override
    public List<AppAccessDo> totalGroupByResultCode(Long lesseeId, Date todayZero) {
        Date date = new Date();
        //Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        //Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        List<AppAccessDo> groupByResultCode = accessLogMapper.totalGroupByResultCode(lesseeId, todayZero);
        return groupByResultCode;
    }

    @Override
    public List<AppAccessDo> groupByTimeUnit(String accessMethod, Integer days, IntervalType typeEnum, Integer accessAppId) {
        List<AppAccessDo> accessLogPos = accessLogMapper.groupByTimeUnit(accessMethod, days, typeEnum.getFormat(), typeEnum.getType(), accessAppId);
        return accessLogPos;
    }

    @Override
    public Integer totalError(Long lesseeId) {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        Integer total = accessLogMapper.countAllError(dataTime, endTime, lesseeId);
        return total;
    }


    public static void main(String[] args) {
        Date beginTime = getBeginTime(new Date(), DAILY_LOCK_ID);
        getEndTime(beginTime, DAILY_MILLISECOND);
    }

    /**
     * 定时运行的任务逻辑
     *
     * @param lockId
     * @param period
     */
    private void runTask(Integer lockId, Long period) {
        OptimisticLockPo lock = optimisticLockMapper.selectByPrimaryKey(lockId);
        if(Objects.isNull(lock)){
            return ;
        }
        Integer versionNo = lock.getVersionNo();
        boolean deadLock = optimisticLockService.isDeadLock(lock.getUpdateTime(), period);
        if (deadLock) {
            boolean releaseDeadLockSuccess = optimisticLockService.releaseDeadLock(lockId, lock.getVersionNo());
            if (releaseDeadLockSuccess) {
                versionNo++;
            } else {
                return;
            }
        }
        boolean success = optimisticLockService.getLock(lockId, versionNo, IP);
        //获取到锁
        if (success) {
            processData(lockId, versionNo, period);
        }
    }

    /**
     * 处理统计任务
     *
     * @param lockId
     * @param versionNo
     * @param period
     */
    private void processData(int lockId, Integer versionNo, Long period) {
        AccessStatisticsPo lastJob = accessStatisticsMapper.getLastJob(lockId);
        Date dataTime = null;
        if (lastJob == null) {
            Date date = accessLogMapper.getMinDate();
            if (date == null) {
                optimisticLockService.releaseLock(lockId, versionNo, IP);
                return;
            }
            dataTime = getBeginTime(date, lockId);
        } else {
            dataTime = getBeginTime(lastJob.getDataTime(), lockId);
        }
        Date endTime = getEndTime(dataTime, period);
        //累计
        while (endTime.getTime() < System.currentTimeMillis()) {
            statistics(dataTime, endTime, lockId);
            dataTime = endTime;
            endTime = getEndTime(dataTime, period);
        }
        if (lockId == DAILY_LOCK_ID) {
            accessStatisticsMapper.deleteLastDayQuarterData();
        }
        lastJob = new AccessStatisticsPo();
        lastJob.setDataTime(dataTime);
        lastJob.setStatType(lockId);
        accessStatisticsMapper.insert(lastJob);

        versionNo++;
        optimisticLockService.releaseLock(lockId, versionNo, IP);

    }

    private void statistics(Date dataTime, Date endTime, Integer lockId) {
        //这是累计的
        List<AccessLogPo> accessLogPos = accessLogMapper.countGroupLessee(dataTime, endTime);
        for (AccessLogPo accessLogPo : accessLogPos) {
            accessStatisticsMapper.insert(build(accessLogPo.getResultRow(), dataTime, lockId, null,
                                                accessLogPo.getLesseeId(), accessLogPo.getAppId()));
        }
        //这是方法分组统计的
        accessLogPos = accessLogMapper.groupMethodCount(dataTime, endTime);
        for (AccessLogPo accessLogPo : accessLogPos) {
            accessStatisticsMapper.insert(build(accessLogPo.getResultRow(), dataTime, lockId +
                    METHOD_ID, accessLogPo.getDataAssetApiId(), accessLogPo.getLesseeId(), accessLogPo.getAppId()));
        }
        //按调用方统计的
        accessLogPos = accessLogMapper.groupAppIdCount(dataTime, endTime);
        for (AccessLogPo accessLogPo : accessLogPos) {
            accessStatisticsMapper.insert(build(accessLogPo.getResultRow(), dataTime, lockId + APP_ID, accessLogPo.getAppId(),
                                                accessLogPo.getLesseeId(), accessLogPo.getAppId()));
        }
        accessLogPos = accessLogMapper.groupResultCode(dataTime, endTime);
        for (AccessLogPo accessLogPo : accessLogPos) {
            accessStatisticsMapper.insert(build(accessLogPo.getResultRow(), dataTime, lockId +
                    RESULT_CODE, accessLogPo.getResultCode(), accessLogPo.getLesseeId(), accessLogPo.getAppId()));
        }
        accessLogPos = accessLogMapper.groupElapsed(dataTime, endTime);
        for (AccessLogPo accessLogPo : accessLogPos) {
            AccessStatisticsPo build = build(accessLogPo.getResultRow(), dataTime,
                                             lockId + ELAPSED_CODE, accessLogPo.getElapsed(), accessLogPo.getLesseeId(),
                                             accessLogPo.getAppId());
            build.setRecordEntityId(accessLogPo.getDataAssetApiId());
            accessStatisticsMapper.insert(build);
        }
        accessLogPos = accessLogMapper.groupByRightRate(dataTime, endTime);
        for (AccessLogPo accessLogPo : accessLogPos) {
            AccessStatisticsPo build = build(accessLogPo.getResultRow(), dataTime, lockId + RIGHT_RATE_CODE, accessLogPo.getElapsed(),
                                             accessLogPo.getLesseeId(), accessLogPo.getAppId());
            build.setRecordEntityId(accessLogPo.getDataAssetApiId());
            accessStatisticsMapper.insert(build);
        }


    }


    private AccessStatisticsPo build(Integer result, Date dataTime, Integer statType, Integer data, Long lesseeId, Integer appId) {
        AccessStatisticsPo lastJob = new AccessStatisticsPo();
        lastJob.setLesseeId(lesseeId);
        //当正确率时，这里存总数
        lastJob.setResult(result);
        lastJob.setDataTime(dataTime);
        lastJob.setStatTime(new Date());
        lastJob.setStatType(statType);
        //这里是错误数
        lastJob.setRecordData(data);
        lastJob.setAccessAppId(appId);
        return lastJob;
    }

    /**
     * 计算一个时间点执行的数据任务的的开始时间
     *
     * @param date
     * @param lockId
     * @return
     */
    private static Date getBeginTime(Date date, int lockId) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        SimpleDateFormat sdf;
        String format = "";
        if (lockId == QUARTER_LOCK_ID) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH");
            format = sdf.format(date);
            int minute = calendar.get(Calendar.MINUTE);
            int quarter = minute / 15 * 15;
            if (quarter < 10) {
                format = format + ":0" + quarter + ":00";
            } else {
                format = format + ":" + quarter + ":00";
            }
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            format = sdf.format(calendar.getTime()) + " 00:00:00";
        }
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = sdf.parse(format);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Page<AppAccessDo> myApi(
            String username,
            PageQuery pageQuery,
            Integer accessAppId,
            Integer dataAssetApiId, String keyword) {
        Date date = new Date();
        Date dataTime = getBeginTime(date, QUARTER_LOCK_ID);
        Date endTime = getEndTime(dataTime, QUARTER_MILLISECOND);
        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setInCharge(username);
        appAccessPo.setLesseeId(AuthUtils.currentAppId());
        List<AppAccessPo> select = appAccessMapper.select(appAccessPo);
        if (CollectionUtils.isNotEmpty(select)) {
            PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
            Page<AppAccessDo> groupByAccessApp = (Page<AppAccessDo>) accessLogMapper.myApi(
                    dataTime,
                    endTime,
                    accessAppId,
                    dataAssetApiId,
                    username,
                    AuthUtils.currentAppId(),
                    keyword
            );
            return groupByAccessApp;
        }
        return new Page<>();
    }

    @Override
    public Integer countApi(Integer dataAssetId) {
        return appAccessMapper.countApi(dataAssetId);
    }

    @Override
    public List<AppAccessDo> pastGroupByApiGroupAndToday(Date from, Date to, Long lesseeId, Date todayZero) {
        List<AppAccessDo> groupByAccessApp = accessLogMapper.pastGroupByApiGroupAndToday(from, to, lesseeId, todayZero);
        return groupByAccessApp;
    }

    @Override
    public Integer totalErrorToday(Long lesseeId, Date todayZero) {
        return accessLogMapper.countErrorToday(lesseeId, todayZero);
    }

    @Override
    public List<AppAccessDo> totalGroupByAccessMethodWeek(Long lesseeId, Date beforeWeek) {
        return accessLogMapper.totalGroupByAccessMethodWeek(lesseeId, beforeWeek);
    }

    @Override
    public List<AppAccessDo> totalGroupByElapsedWeek(Long lesseeId, Date beforeWeekDay) {
        return accessLogMapper.totalGroupByElapsedWeek(lesseeId, beforeWeekDay);
    }

    @Override
    public List<AppAccessDo> totalGroupByAccessAppWeek(Long lesseeId, Date beforeWeekDay) {
        return accessLogMapper.totalGroupByAccessAppWeek(lesseeId, beforeWeekDay);
    }

    @Override
    public List<AppAccessDo> totalGroupByResultCodeMonth(Long lesseeId, Date beforeMonthDay) {
        return accessLogMapper.totalGroupByResultCodeMonth(lesseeId, beforeMonthDay);
    }


    /**
     * 计算一个时间点执行的数据任务的的结束时间
     *
     * @param date
     * @param period
     * @return
     */
    private static Date getEndTime(Date date, Long period) {
        long time = date.getTime();
        long timeMills = time + period;
        return new Date(timeMills);
    }


}
