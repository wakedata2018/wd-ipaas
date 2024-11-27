package com.wakedata.dw.open.service.xxljob;

import java.util.Map;

/**
 * @author WangChenSheng
 * @descriptor 执行xxlJob调度中心相关操作
 * @title XxlJobTaskScheduleService
 * @date 2022/10/21 19:04
 */
public interface XxlJobTaskScheduleService {

    /**
     * 查询xxlJob调度中心任务信息
     *
     * @param queryParams 查询参数
     * @param <T> <T>
     * @return <T>
     */
    <T> T queryXxlJobTaskScheduleInfo(Map<String,Object> queryParams);

    /**
     * 新增xxlJob调度中心任务信息
     *
     * @param addParams 新增参数
     * @param <T> <T>
     * @return <T>
     */
    <T> T addXxlJobTaskScheduleInfo(Map<String,Object> addParams);

    /**
     * 编辑xxlJob调度中心任务信息
     *
     * @param updateParams 编辑参数
     * @param <T> <T>
     * @return <T>
     */
    <T> T updateXxlJobTaskScheduleInfo(Map<String,Object> updateParams);

    /**
     * 删除xxlJob调度中心任务信息
     *
     * @param deleteParams 删除参数
     * @param <T> <T>
     * @return <T>
     */
    <T> T deleteXxlJobTaskScheduleInfo(Map<String,Object> deleteParams);
}
