package com.wakedata.dw.open.enums;

/**
 * 第三方应用认证类型
 * @author 佟蕊
 */
public enum AuthType {

    /**
     * 自定义方式
     */
    CUSTOM("1"),
    /**
     * 惟客云登录方式
     */
    WAKE_CLOUD("2");

    private String value;

    AuthType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
