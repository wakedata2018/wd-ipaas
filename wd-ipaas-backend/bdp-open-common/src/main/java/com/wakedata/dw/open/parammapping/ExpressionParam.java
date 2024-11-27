package com.wakedata.dw.open.parammapping;

import lombok.Data;

/**
 * 表达式内部参数对象
 * @author luomeng
 * @date 2022/9/16 11:42
 */
@Data
public class ExpressionParam {

    /**
     * 固定值，引用值，函数
     * {@link ParamMappingTypeEnum}
     */
    private String type;

    /**
     * 来源算子
     */
    private String operatorId;

    /**
     * 算子名称
     */
    private String operatorName;

    /**
     * 参数值，type不同，所存储的数据也不通
     */
    private Object value;

    /**
     * 属性的数据类型,暂时只在响应对象里面需要配置（如object,string...）
     */
    private String dataType;

    /**
     * 表达式是否为json
     */
    private Boolean expressionIsJson;
}
