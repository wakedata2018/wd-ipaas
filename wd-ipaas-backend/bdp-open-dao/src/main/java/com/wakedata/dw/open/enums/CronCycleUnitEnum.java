package com.wakedata.dw.open.enums;

import lombok.Getter;

/**
 * @description: 定时任务cron表达式周期单位枚举
 * @date: 2021/12/2 14:51
 * @author: ZhuXueLiang
 */
@Getter
public enum CronCycleUnitEnum {

    /**
     * 秒
     */
    SECONDS(0, "秒"),
    /**
     * 分
     */
    MINUTE(1, "分"),
    /**
     * 小时
     */
    HOURS(2, "小时"),
    /**
     * 日期
     */
    DAY(3, "日期"),
    /**
     * 月份
     */
    MONTH(4, "月份"),
    ///**
    // * 星期
    // */
    //WEEK(5, "星期"),
    /**
     * 年
     */
    YEAR(6, "年");

    private final Integer value;
    private final String description;

    CronCycleUnitEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }
}
