package com.wakedata.dw.open.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 获取前一天时间
     *
     * @return int类型
     */
    public static Integer getYesterDay() {
        Date d = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd");
        //获取昨天日期
        String day = sp.format(d);
        Integer yesterDay = Integer.valueOf(day);
        return yesterDay;
    }

    public static Date getStartTime(Calendar day) {
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        return day.getTime();
    }
    /*
     * 获取当前天的结束时间
     */
    public static Date getEndTime(Calendar day) {
        day.set(Calendar.HOUR_OF_DAY, 23);
        day.set(Calendar.MINUTE, 59);
        day.set(Calendar.SECOND, 59);
        day.set(Calendar.MILLISECOND, 999);
        return day.getTime();
    }


    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static String strToDateFormat(String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        formatter.setLenient(false);
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(newDate);
    }

    /**
     * 获取当天0点
     * @return
     */
    public static Date getTodayZero(){
        return org.apache.commons.lang3.time.DateUtils.truncate(new Date(),Calendar.DAY_OF_MONTH);
    }
    /**
     * 获取一个星期前的日期
     */
    public static Date getBeforeWeekDay(){
        return org.apache.commons.lang3.time.DateUtils.addWeeks(new Date(),-1);
    }

    /**
     * 获取一个月前的日期
     */
    public static Date getBeforeMonthDay(){
        return org.apache.commons.lang3.time.DateUtils.addMonths(new Date(),-1);
    }

    /**
     * 获取30天前的日期
     */
    public static Date getBefore30Day(){
        return org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -30);
    }

    /**
     * 获取本月起始日期
     */
    public static Date getStartMonth(Date date){
        return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.MONTH);
    }
}
