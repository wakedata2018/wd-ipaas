package com.wakedata.dw.open.enums;

/**
 * @author pengxu
 * @title StatusEnum
 * @projectName bdp-open
 * @date 2018/5/12
 * @description
 */
public enum RoleAuthEnum {

    /**
     * 状态编码
     */
    ADMIN(1, "管理员"),
    DEVELOPER(0, "开发者"),
    ;

    /**
     * 编码
     */
    private int code;

    /**
     * 名称
     */
    private String value;


    RoleAuthEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }


    public static RoleAuthEnum parse(int code) {
        for (RoleAuthEnum statusEnum : RoleAuthEnum.values()) {
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
