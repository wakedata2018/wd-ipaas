package com.wakedata.dw.open.parammapping;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobTaskParamMapping
 * @date 2022/10/26 17:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class XxlJobTaskParamMapping extends AbstractParamMapping{

    /**
     * 是否必填
     */
    private Integer isRequired;

    /**
     * 请求体jsonSchema
     */
    private String jsonSchema;

    /**
     * 固定值以及函数转换后的值
     */
    private String value;

    public XxlJobTaskParamMapping() {
    }

    public XxlJobTaskParamMapping(String field, String expression) {
        super(field, expression);
    }

}
