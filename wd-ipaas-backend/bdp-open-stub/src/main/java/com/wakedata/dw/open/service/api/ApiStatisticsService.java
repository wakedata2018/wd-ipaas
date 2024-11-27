package com.wakedata.dw.open.service.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.enums.IntervalType;
import com.wakedata.dw.open.model.domain.AppAccessDo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.query.StatisticQuery;

import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title ApiStatisticsService
 * @projectName bdp-open
 * @date 2019/8/23 10:12
 */
public interface ApiStatisticsService {


    /**
     * 每天一次的任务
     */
    void dailyTask();

    /**
     * 每刻一次的任务
     */
    void quarterTask();

    /**
     * 总共调用次数
     *
     * @return
     */
    Integer totalCallTimes();

    /**
     * 每个方法调用累计次数
     *
     * @return
     * @param lesseeId
     * @param today
     */
    List<AppAccessDo> totalGroupByAccessMethod(Long lesseeId,Date today);

    /**
     * 每个接入端调用次数
     *
     * @return
     * @param lesseeId
     */
    List<AppAccessDo> totalGroupByAccessApp(Long lesseeId);

    /**
     * 每个主题（API分组）调用次数
     *
     * @return
     */
    List<AppAccessDo> totalGroupByApiGroup();

    /**
     * 错误码分布
     *
     * @return
     * @param lesseeId
     * @param todayZero
     */
    List<AppAccessDo> totalGroupByResultCode(Long lesseeId, Date todayZero);

    /**
     * 调用时长统计
     *
     * @return
     * @param lesseeId
     */
    List<AppAccessDo> totalGroupByElapsed(Long lesseeId);


    /**
     * 今天以前某天每个方法调用累计次数
     *
     * @param date
     * @return
     */
    List<AppAccessDo> pastGroupByAccessMethod(String date);

    /**
     * 今天以前某天每个接入端调用次数
     *
     * @param date
     * @return
     */
    List<AppAccessDo> pastGroupByAccessApp(String date);

    /**
     * 今天以前某天每个主题（API分组）调用次数
     *
     * @param date
     * @return
     */
    List<AppAccessDo> pastGroupByApiGroup(String date);

    /**
     * @param from
     * @param to
     * @param lesseeId
     * @return
     */
    List<AppAccessDo> pastGroupByApiGroup( Date from, Date to, Long lesseeId);

    /**
     * 今天以前某天错误码分布
     *
     * @param date
     * @return
     */
    List<AppAccessDo> pastGroupByResultCode(String date);

    /**
     * 今天以前某天调用时长统计
     *
     * @param date
     * @return
     */
    List<AppAccessDo> pastGroupByElapsed(String date);

    /**
     * API调用统计查询列表
     *
     * @param query 查询条件
     * @return API调用统计查询列表
     */
    PageResultDTO<List<AppAccessDo>> totalGroupByRightRate(StatisticQuery query);

    /**
     * 24小时内各个API接口访问的次数
     *
     * @param appId
     * @param days
     * @param typeEnum
     * @param accessAppId
     * @return
     */
    List<AppAccessDo> groupByTimeUnit(String appId, Integer days, IntervalType typeEnum, Integer accessAppId);

    /**
     * 累计异常数
     *
     * @return
     * @param lesseeId
     */
    Integer totalError(Long lesseeId);


    /**
     * 我的API调用次数
     *
     * @param username
     * @param pageQuery
     * @param accessAppId
     * @param dataAssetApiId
     * @param keyword
     * @return
     */
    Page<AppAccessDo> myApi(String username, PageQuery pageQuery, Integer accessAppId, Integer dataAssetApiId, String keyword);

    /**
     * 求api的数量，用作分页
     * @param dataAssetId
     * @return
     */
    Integer countApi(Integer dataAssetId);

    /**
     * 获取各种api当月调用次数
     * @param from
     * @param to
     * @param lesseeId
     * @param todayZero
     * @return
     */
    List<AppAccessDo> pastGroupByApiGroupAndToday(Date from, Date to, Long lesseeId, Date todayZero);

    /**
     * 今日的api调用错误数
     * @param lesseeId
     * @param todayZero
     * @return
     */
    Integer totalErrorToday(Long lesseeId, Date todayZero);

    /**
     * 上一周的API访问次数排名
     * @param lesseeId 租户id
     * @param beforeWeek 前一周的时间
     * @return
     */
    List<AppAccessDo> totalGroupByAccessMethodWeek(Long lesseeId, Date beforeWeek);

    /**
     * 上一周api访问时长
     * @param lesseeId 租户id
     * @param beforeWeekDay 前一周的时间
     * @return
     */
    List<AppAccessDo> totalGroupByElapsedWeek(Long lesseeId, Date beforeWeekDay);

    /**
     * 上一周的调用方法排名
     * @param lesseeId 租户id
     * @param beforeWeekDay 前一周的时间
     * @return
     */
    List<AppAccessDo> totalGroupByAccessAppWeek(Long lesseeId, Date beforeWeekDay);

    /**
     * 上一个月的错误类型统计
     * @param lesseeId 租户id
     * @param beforeMonthDay 上一个月的时间
     * @return
     */
    List<AppAccessDo> totalGroupByResultCodeMonth(Long lesseeId, Date beforeMonthDay);
}