package com.wakedata.dw.open.enums;

import lombok.Getter;

/**
 * 已经在DataAssetEnums定义枚举FiledTypeAttrEnums
 */
@Getter
@Deprecated
public enum FiledTypeAttrEnums {
    OPERATOR(0),
    FILTER(1);

    private Integer value;


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    FiledTypeAttrEnums(Integer value) {
        this.value = value;
    }
}
