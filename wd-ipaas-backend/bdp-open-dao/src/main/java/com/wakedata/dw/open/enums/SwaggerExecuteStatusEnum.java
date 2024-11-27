package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;


/**
 * @author WangChenSheng
 * @descriptor swagger导入确认按钮执行状态
 * @title SwaggerApiDO
 * @date 2022/8/23 14:58
 */
@Getter
public enum SwaggerExecuteStatusEnum implements BaseDbEnum<Integer> {

    /**
     * 未执行
     */
    UN_EXECUTE(1, "未导入"),
    /**
     * 部分导入
     */
    PARTIAL_EXECUTE(2, "部分导入"),
    /**
     * 全部导入
     */
    ALL_EXECUTE(3, "全部导入"),
    ;

    private final Integer value;
    private final String description;

    SwaggerExecuteStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
