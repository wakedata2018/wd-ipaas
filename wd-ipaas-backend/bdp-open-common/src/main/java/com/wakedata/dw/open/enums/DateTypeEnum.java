package com.wakedata.dw.open.enums;

/**
 * @author pengxy tanzhi
 * @title DateTypeEnum
 * @projectName bdp-open
 * @date 2018/6/19
 * @description 日期枚举
 */
public enum DateTypeEnum {

    /**
     * 统计日期
     */
    STAT_BY_DAY("D", "日"),
    STAT_BY_WEEK("W", "周"),
    STAT_BY_MONTH("M", "月"),
    ;

    /**
     * 编码
     */
    private String code;

    /**
     * 值
     */
    private String value;


    DateTypeEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static DateTypeEnum parse(String code) {
        for (DateTypeEnum dateTypeEnum : DateTypeEnum.values()) {
            if (dateTypeEnum.code.equals(code)) {
                return dateTypeEnum;
            }
        }
        return null;
    }


    public String getCode() {
        return code;
    }


    private void setCode(String code) {
        this.code = code;
    }


    public String getValue() {
        return value;
    }


    private void setValue(String value) {
        this.value = value;
    }
}
