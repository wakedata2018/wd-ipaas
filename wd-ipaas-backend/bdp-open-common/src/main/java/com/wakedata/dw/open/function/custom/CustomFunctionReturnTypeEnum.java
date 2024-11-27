package com.wakedata.dw.open.function.custom;

/**
 * 自定义函数支持返回类型枚举
 *
 * @author luomeng
 * @date 2022/10/31 17:23
 */
public enum CustomFunctionReturnTypeEnum {
    /**
     * 返回类型
     */
    JSON_OBJECT("JSONObject", "对象类型，com.alibaba.fastjson.JSONObject"),
    LIST_OBJECT("List<Object>", "LIST集合"),
    MAP_OBJECT("Map<String,Object>", "MAP集合"),
    INTEGER("Integer", "int类型"),
    LONG("Long", "long类型"),
    BOOLEAN("Boolean", "boolean类型"),
    DOUBLE("Double", "double类型"),
    STRING("String", "string类型"),
    DATE("Date", "日期类型");


    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String desc;

    CustomFunctionReturnTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 查找指定数据
     *
     * @param type
     * @return
     */
    public static CustomFunctionReturnTypeEnum getDescriptionReturnTypeEnum(String type) {
        for (CustomFunctionReturnTypeEnum typeEnum : CustomFunctionReturnTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
