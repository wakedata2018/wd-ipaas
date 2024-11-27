package com.wakedata.dw.open.enums;

public enum ActiveStateEnum {

    ACTIVE(1, "active"),
    INVALID(0, "invalid");

    private Integer value;
    private String desc;

    private ActiveStateEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }
}
