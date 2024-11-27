package com.wakedata.dw.open.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 数据类型，枚举
 * @author luomeng
 * @date 2022/9/19 17:51
 */
public enum DataTypeEnum {

    /**
     * 数据类型
     */
    INTEGER("integer"),
    NUMBER("number"),
    OBJECT("object"),
    STRING("string"),
    BOOLEAN("boolean"),
    LONG("long"),
    DOUBLE("double"),
    DATETIME("datetime"),
    DATE_TIME("date-time"),
    ARRAY("array"),
    JSON_OBJECT("JSONObject"),
    JSON_ARRAY("JSONArray");

    /**
     * 类型
     */
    private String type;

    DataTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static DataTypeEnum getEnumByType(String type) {
        for (DataTypeEnum typeEnum : DataTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        //数组类型转换，格式为array<?>
        if(StrUtil.isNotBlank(type) && type.contains(ARRAY.getType())){
            return ARRAY;
        }
        return null;
    }
}
