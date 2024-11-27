package com.wakedata.dw.open.service.log;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.model.domain.AppAccessDo;
import com.wakedata.dw.open.model.domain.AppInvokeDo;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.model.query.StatisticQuery;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.vo.StatisticsIndexVo;

import java.util.Date;

/**
 * @author tanzhi
 * @title AccessLogService
 * @date 2019/11/22 9:32
 * @projectName dw-open
 * @descriptor
 */
public interface AccessLogService extends BaseDbService<AccessLogPo> {

    /**
     * 后台首页数据
     *
     * @param query 租户id
     * @return 后台首页数据
     */
    StatisticsIndexVo statistics(StatisticQuery query);

    /**
     * 按日期统计
     * @param date 时间
     * @return 后台首页数据
     */
    StatisticsIndexVo statistics(String date);


    /**
     * 错误原因统计列表
     *
     * @param query 查询条件
     * @return 错误原因统计列表
     */
    Page<AppAccessDo> apiError(StatisticQuery query);

    /**
     * 接入应用调用信息
     *
     * @param query 查询条件
     * @return 接入应用调用信息
     */
    Page<AppInvokeDo> appInvokeInfo(StatisticQuery query);


    /**
     * 添加api访问日志
     * @param accessLogPo
     * @return
     */
    Integer addApiAccessLog(AccessLogPo accessLogPo);

    /**
     * 获取日志详情
     * @param id
     * @return
     */
    AccessLogPo getAccessLogInfo(Integer id);

    /**
     * 接口调用统计列表
     *
     * @param query 查询条件
     * @return 接口调用统计列表
     */
    Page<AppAccessDo> apiInvoke(StatisticQuery query);
}
