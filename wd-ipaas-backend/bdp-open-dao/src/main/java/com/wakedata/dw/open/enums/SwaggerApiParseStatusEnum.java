package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;


/**
 * @author WangChenSheng
 * @descriptor swaggerApi导入正式表的状态
 * @title SwaggerApiDO
 * @date 2022/8/23 14:58
 */
@Getter
public enum SwaggerApiParseStatusEnum implements BaseDbEnum<Integer> {

    /**
     * 解析成功
     */
    SUCCESS_PARSE(1, "成功"),
    /**
     * 解析失败
     */
    FAIL_PARSE(2, "失败"),
    ;

    private final Integer value;
    private final String description;

    SwaggerApiParseStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
