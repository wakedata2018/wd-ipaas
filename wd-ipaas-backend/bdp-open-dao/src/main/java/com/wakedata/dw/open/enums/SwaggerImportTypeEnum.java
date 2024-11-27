package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

/**
 * @author wujunqiang
 * @since 2023/2/1 14:33
 */
@Getter
public enum SwaggerImportTypeEnum implements BaseDbEnum<Integer> {

    /**
     * URL导入
     */
    URL(0),
    /**
     * 文件导入
     */
    FILE(1);

    private final Integer value;

    SwaggerImportTypeEnum(Integer value) {
        this.value = value;
    }
}
