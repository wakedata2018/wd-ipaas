package com.wakedata.dw.open.mapper;

import com.wakedata.dw.open.model.log.AccessStatisticsPo;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author tanzhi
 * @title AccessStatisticsMapper
 * @projectName bdp-open
 * @date 2019/8/23 16:14
 * @description
 */
public interface AccessStatisticsMapper extends Mapper<AccessStatisticsPo> {

    /**
     * 获取上次任务
     *
     * @param statType
     * @return
     */
    AccessStatisticsPo getLastJob(Integer statType);

    /**
     * 删除上刻数据
     *
     * @return
     */
    int deleteLastDayQuarterData();



    /**
     * 所有调用次数
     *
     * @return
     */
    Integer totalCallTimes();


}
