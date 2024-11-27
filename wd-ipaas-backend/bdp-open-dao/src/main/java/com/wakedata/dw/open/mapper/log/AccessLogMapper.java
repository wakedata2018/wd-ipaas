package com.wakedata.dw.open.mapper.log;

import com.wakedata.dw.open.model.domain.AppAccessDo;
import com.wakedata.dw.open.model.domain.AppInvokeDo;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.model.query.StatisticQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title AccessLogMapper
 * @projectName bdp-open
 * @date 2019/8/16 10:22
 * @descprition
 */
public interface AccessLogMapper extends Mapper<AccessLogPo> {

    /**
     * 某个接口一定时间内访问接口的次数
     *
     * @param accessMethod 不传accessMethod时显示所有
     * @param num
     * @param format
     * @param type
     * @param accessAppId
     * @return
     */
    List<AppAccessDo> groupByTimeUnit(@Param("accessMethod") String accessMethod, @Param("num")
            Integer num, @Param("format") String format, @Param("type") String type, @Param("accessAppId") Integer accessAppId);

    /**
     * 历史所有调用次数
     * @param lesseeId 租户ID
     * @param dataZero 当天0点
     * @return 租户调用次数
     */
    Integer countAll(@Param("lesseeId") Long lesseeId,@Param("today") Date dataZero);

    /**
     * 今日调用次数
     * @param lesseeId 租户ID
     * @param dataZero 当天0点
     * @return 租户调用次数
     */
    Integer countAllToday(@Param("lesseeId") Long lesseeId,@Param("today") Date dataZero);

    /**
     * 某个时间段累调用次数
     *
     * @param start
     * @param end
     * @return
     */
    Integer count(@Param("start") Date start, @Param("end") Date end);

    /**
     * 某个时间段累调用次数,租户分组
     * @param start 开始
     * @param end 结束
     * @return
     */
    List<AccessLogPo> countGroupLessee(@Param("start") Date start, @Param("end") Date end);

    /**
     * 累计异常数
     *
     * @param start 开始时间
     * @param end 结束时间
     * @param lesseeId 租户id
     * @return
     */
    Integer countAllError(@Param("start") Date start, @Param("end") Date end,@Param("lesseeId") Long lesseeId);

    /**
     * 方法分组显示次数
     *
     * @param start
     * @param end
     * @return
     */
    List<AccessLogPo> groupMethodCount(@Param("start") Date start, @Param("end") Date end);

    /**
     * 时长统计
     *
     * @param start
     * @param end
     * @return
     */
    List<AccessLogPo> groupElapsed(@Param("start") Date start, @Param("end") Date end);

    /**
     * 调用方
     *
     * @param start
     * @param end
     * @return
     */
    List<AccessLogPo> groupAppIdCount(@Param("start") Date start, @Param("end") Date end);

    /**
     * 错误码
     *
     * @param start
     * @param end
     * @return
     */
    List<AccessLogPo> groupResultCode(@Param("start") Date start, @Param("end") Date end);

    /**
     * 正确率
     *
     * @param start
     * @param end
     * @return
     */
    List<AccessLogPo> groupByRightRate(@Param("start") Date start, @Param("end") Date end);

    /**
     * 分组显示次数
     *
     * @param start 开始
     * @param end 结束
     * @param lesseeId 租户id
     * @param todayZero 当天0点
     * @return app 调用列表
     */
    List<AppAccessDo> totalGroupByAccessMethod(@Param("start") Date start, @Param("end") Date end, @Param("lesseeId") Long lesseeId,@Param("today") Date todayZero);

    /**
     * 接入端调用次数
     *
     * @param start 开始
     * @param end 结束
     * @param lesseeId 租户id
     * @return app调用次数列表
     */
    List<AppAccessDo> totalGroupByAccessApp(@Param("start") Date start, @Param("end") Date end, @Param("lesseeId") Long lesseeId);

    /**
     * API分组调用次数
     *
     * @param start
     * @param end
     * @return
     */
    List<AppAccessDo> totalGroupByApiGroup(@Param("start") Date start, @Param("end") Date end);

    /**
     * 错误码分布
     *
     * @param lesseeId 租户id
     * @param todayZero 当天0点
     * @return api调用错误码信息
     */
    List<AppAccessDo> totalGroupByResultCode(@Param("lesseeId") Long lesseeId,@Param("today") Date todayZero);

    /**
     * 调用时长
     *
     * @param start 开始
     * @param end 结束
     * @param lesseeId 租户id
     * @return api调用时长列表
     */
    List<AppAccessDo> totalGroupByElapsed(@Param("start") Date start, @Param("end") Date end, @Param("lesseeId") Long lesseeId);

    /**
     * 正确率
     *
     * @param dataAssetId
     * @param start
     * @param end
     * @return
     */
    List<AppAccessDo> totalGroupByRightRate(@Param("dataAssetId") Integer dataAssetId, @Param("start") Date start, @Param("end") Date end);

    /**
     * 今天以前的
     *
     * @param start
     * @param end
     * @return
     */
    List<AppAccessDo> pastGroupByAccessMethod(@Param("start") Date start, @Param("end") Date end);

    /**
     * 今天以前的
     *
     * @param start
     * @param end
     * @return
     */
    List<AppAccessDo> pastGroupByAccessApp(@Param("start") Date start, @Param("end") Date end);

    /**
     * 今天以前的
     *
     * @param start 开始
     * @param end 结束
     * @param lesseeId 租户id
     * @return 调用信息列表
     */
    List<AppAccessDo> pastGroupByApiGroup(@Param("start") Date start, @Param("end") Date end,@Param("lesseeId") Long lesseeId);

    /**
     * 今天以前的
     *
     * @param start
     * @param end
     * @return
     */
    List<AppAccessDo> pastGroupByResultCode(@Param("start") Date start, @Param("end") Date end);

    /**
     * 今天以前的
     *
     * @param start
     * @param end
     * @return
     */
    List<AppAccessDo> pastGroupByElapsed(@Param("start") Date start, @Param("end") Date end);


    /**
     * 最小日期
     *
     * @return
     */
    Date getMinDate();

    /**
     * 统计某个方法是谁在调用
     *
     * @param createTime
     * @param assetApiId 可以传Null
     * @return
     */

    List<AppAccessDo> whoCalls(@Param("createTime") Date createTime, @Param("accessMethod") Integer assetApiId);

    /**
     * 一定时间内所有的错误次数
     *
     * @param createTime
     * @return
     */
    Integer allError(@Param("createTime") Date createTime);

    /**
     * 错误代码分组
     *
     * @param createTime
     * @return
     */
    List<AccessLogPo> groupByErrorCode(@Param("createTime") Date createTime);


    /**
     * 错误访问日志
     *
     * @param query 查询条件
     * @return 错误信息
     */
    List<AppAccessDo> apiError(@Param("query") StatisticQuery query);

    /**
     * 获取我得api信息
     * @param start
     * @param end
     * @param accessAppId
     * @param dataAssetApiId
     * @param username
     * @param lesseeId
     * @param keyword
     * @return
     */
    List<AppAccessDo> myApi(
            @Param("start") Date start,
            @Param("end") Date end,
            @Param("accessAppId") Integer accessAppId,
            @Param("dataAssetApiId") Integer dataAssetApiId,
            @Param("username") String username,
            @Param("lesseeId") Long lesseeId,
            @Param("keyword") String keyword
    );


    /**
     * 日志清理
     *
     * @return
     */
    Integer cleanUp();

    /**
     * 查今日api调用信息
     *
     * @param query 查询条件
     * @return
     */
    List<AppAccessDo> totalGroupByRightRateToday(@Param("query") StatisticQuery query);

    /**
     * 查时间段内的api调用信息
     *
     * @query 查询条件
     * @return 查时间段内的api调用信息列表
     */
    List<AppAccessDo> totalGroupByRightRateBefore(@Param("query") StatisticQuery query);

    /**
     * 接入应用调用信息查询
     *
     * @param query 查询条件
     * @return 接入应用调用信息列表
     */
    List<AppInvokeDo> appInvokeInfo(@Param("query") StatisticQuery query);
    /**
     * 查询该api今日错误次数
     * @param dataAssetApiId
     * @return
     */
    Integer queryErrorTimes(@Param("dataAssetApiId") Integer dataAssetApiId);

    /**
     * 查询当天的api调用次数
     * @param apiId
     * @return
     */
    Integer countDayLog(Integer apiId);

    /**
     * 查询当月的api调用次数
     * @param apiId
     * @return
     */
    Integer countMonthLog(Integer apiId);

    /**
     * 查询近一个月的api调用
     * @param start 开始时间
     * @param end 结束时间
     * @param lesseeId 租户id
     * @param todayZero 当天0点
     * @return
     */
    List<AppAccessDo> pastGroupByApiGroupAndToday(@Param("start") Date start, @Param("end") Date end, @Param("lesseeId") Long lesseeId, @Param("today") Date todayZero);

    /**
     * 今日的api调用错误数
     * @param lesseeId
     * @param todayZero
     * @return
     */
    Integer countErrorToday(@Param("lesseeId") Long lesseeId,@Param("todayZero") Date todayZero);

    /**
     * 上一周的API访问次数排名
     * @param lesseeId 租户id
     * @param beforeWeek 前一周的时间
     * @return
     */
    List<AppAccessDo> totalGroupByAccessMethodWeek(@Param("lesseeId") Long lesseeId,@Param("beforeWeek") Date beforeWeek);

    /**
     * 上一周的API访问时长
     * @param lesseeId 租户id
     * @param beforeWeekDay 前一周的时间
     * @return
     */
    List<AppAccessDo> totalGroupByElapsedWeek(@Param("lesseeId") Long lesseeId,@Param("beforeWeekDay") Date beforeWeekDay);

    /**
     * 上一周的API访问时长
     * @param lesseeId 租户id
     * @param beforeWeekDay 前一周的时间
     * @return
     */
    List<AppAccessDo> totalGroupByAccessAppWeek(@Param("lesseeId") Long lesseeId,@Param("beforeWeekDay") Date beforeWeekDay);

    /**
     * 上一个月的错误类型统计
     * @param lesseeId 租户id
     * @param beforeMonthDay 上一个月的时间
     * @return
     */
    List<AppAccessDo> totalGroupByResultCodeMonth(@Param("lesseeId") Long lesseeId,@Param("beforeMonthDay") Date beforeMonthDay);

    /**
     * 查询总的api调用次数
     * @param apiId apiId
     * @return 总调用次数
     */
    Integer countTotalLog(Integer apiId);

    /**
     * 接口调用统计列表
     *
     * @param query 查询条件
     * @return 接口调用统计列表
     */
    List<AppAccessDo> apiInvoke(@Param("query") StatisticQuery query);
}
