package com.wakedata.dw.open.enums;

/**
 * @author tanzhi
 * @title IntervalType
 * @projectName bdp-open
 * @date 2019/9/25 19:31
 * @description 根据不同的值取不同的日期格式化类型
 */
public enum IntervalType {

    /**
     * 用来取相应的格式
     */
    SECOND("SECOND", "%S"),
    MINUTE("MINUTE", "%i"),
    HOUR("HOUR", "%m-%d %H"),
    DAY("DAY", "%Y-%m-%d"),
    MONTH("MONTH", "%Y-%m-%d"),
    YEAR("YEAR", "%Y"),
    WEEK("WEEK", "%Y-%m-%d");

    IntervalType(String type, String format) {
        this.type = type;
        this.format = format;
    }

    /**
     * 类型
     */
    private String type;
    /**
     * 格式
     */
    private String format;

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    private void setFormat(String format) {
        this.format = format;
    }
}
