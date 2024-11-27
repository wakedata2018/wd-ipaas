package com.wakedata.dw.open.enums;

/**
 * @author pengxu
 * @title StatusEnum
 * @projectName bdp-open
 * @date 2018/5/12
 * @description
 */
public enum StatusEnum {

    /**
     * 状态编码
     */
    ACTIVE(1, "启用"),
    INACTIVE(0, "禁用"),
    ;

    /**
     * 编码
     */
    private int code;

    /**
     * 名称
     */
    private String value;


    StatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }


    public static StatusEnum parse(int code) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
