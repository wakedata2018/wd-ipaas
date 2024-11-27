package com.wakedata.dw.open.parammapping;

/**
 * 参数映射类型
 * @author luomeng
 * @date 2022/8/22 10:09
 */
public enum ParamMappingTypeEnum {
    /**
     * 类型
     */
    FIXED_TYPE("fixed","固定值"),
    REFERENCE_TYPE("reference","引用值"),
    FUNCTION_TYPE("function","函数")
    ;
    /**
     * 类型
     */
    private String type;
    /**
     * 说明
     */
    private String desc;

    ParamMappingTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
