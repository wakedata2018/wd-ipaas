package com.wakedata.dw.open.enums;

/**
 * 逻辑运算符
 * @author luomeng
 * @date 2022/9/20 14:50
 */
public enum OperatorLogicalEnum {
    /**
     * 运算符
     */
    LOGICAL_AND("and","&&","并且"),
    LOGICAL_OR("or","||","或者");

    /**
     * 代码
     */
    private String code;
    /**
     * 运算符脚本
     */
    private String script;
    /**
     * 描述
     */
    private String desc;

    OperatorLogicalEnum(String code, String script, String desc) {
        this.code = code;
        this.script = script;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getScript() {
        return script;
    }

    public String getDesc() {
        return desc;
    }
}
