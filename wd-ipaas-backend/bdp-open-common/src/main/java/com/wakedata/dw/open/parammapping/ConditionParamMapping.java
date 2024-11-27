package com.wakedata.dw.open.parammapping;

import lombok.Data;

/**
 * 条件判断参数映射
 * @author luomeng
 * @date 2022/9/19 19:08
 */
@Data
public class ConditionParamMapping extends AbstractParamMapping{

    private HttpParamKind httpParamKind = HttpParamKind.QUERY;

    public ConditionParamMapping() {
    }

    public ConditionParamMapping(String field, String expression) {
        super(field, expression);
    }

}
