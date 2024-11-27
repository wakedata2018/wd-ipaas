package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author yiyufeng
 * @title ApprovalStatusEnum
 * @projectName bdp-open
 * @date
 * @description 申请状态枚举
 */
@Getter
public enum ApiApplyStatusEnum implements BaseDbEnum<Integer> {

    /**
     * 未申请
     */
    UN_APPLY(0, "未申请"),
    /**
     * 申请中(审批中,枚举值一样)
     */
    IN_APPLY(1, "申请中"),
    /**
     * 申请成功(审批成功,枚举值一样)
     */
    APPLY(2, "申请成功,枚举值一样"),
    /**
     * 申请失败(审批成功,枚举值一样)
     */
    FAILURE_APPLY(3, "申请失败"),
    ;

    private Integer value;
    private String description;

    ApiApplyStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据value获取枚举对象
     *
     * @param target value
     * @return 枚举对象
     */
    public static ApiApplyStatusEnum getEnum(Integer target){
        ApiApplyStatusEnum[] values = ApiApplyStatusEnum.values();
        Map<Integer, ApiApplyStatusEnum> collect = Arrays.stream(values).collect(Collectors.toMap(ApiApplyStatusEnum::getValue, value -> value, (key, value) -> value));
        if (Objects.isNull(target)){
            return null;
        }
        return collect.get(target);
    }
}
