package com.wakedata.dw.open.enums;

/**
 * @author wq
 * @title WarnTypesEnum
 * @date 2020/10/10 18:49
 * @projectName dw-open
 * @description
 */
public enum  WarnTypesEnum {
    /**
     * 调用超时告警
     */
    overtime("调用时间超过阈值",1),
    /**
     * 错误次数告警
     */
    overrun("错误次数超过阈值",2);

    WarnTypesEnum(String name,Integer value) {
        this.name = name;
        this.value = value;
    }
    String name;
    Integer value;

    public String getName(){
        return this.name;
    }

    public Integer getValue(){
        return this.value;
    }
}
