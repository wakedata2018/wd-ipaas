package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;


/**
 * @author WangChenSheng
 * date 2022/8/23 14:58
 */
@Getter
public enum SwaggerApiImportStatusEnum implements BaseDbEnum<Integer> {

    /**
     * 未导入
     */
    UN_IMPORT(0,"未导入"),

    /**
     * 导入成功
     */
    SUCCESS_IMPORT(1, "导入成功"),
    /**
     * 导入失败
     */
    FAIL_IMPORT(2, "导入失败"),
    ;

    private final Integer value;
    private final String description;

    SwaggerApiImportStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
