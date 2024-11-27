package com.wakedata.dw.open.parammapping;

import lombok.Data;

/**
 * @author ZhangXueJun
 * @title AbstractParamMapping
 * @date 2021/5/14 15:45
 * @projectName dw-open
 * @description
 */
@Data
public class AbstractParamMapping {

    /**
     * 来源算子id
     */
    private String operatorId;
    /**
     * 属性名
     */
    private String field;

    /**
     * 属性的数据类型,暂时只在响应对象里面需要配置（如object,string...）
     */
    private String dataType;

    /**
     * 固定值，引用值，函数
     *
     * @see ParamMappingTypeEnum
     */
    private String type;
    /**
     * 固定值
     */
    private String fixedValue;
    /**
     * 表达式
     */
    private String expression;
    /**
     * 参数位置
     */
    private HttpParamKind httpParamKind = HttpParamKind.QUERY;
    /**
     * 表达式是否为json
     */
    private Boolean expressionIsJson;

    /**
     * 上级节点字段名称(用于类型解析,获取key的相对应的Type)
     */
    private String parentFiled;


    public AbstractParamMapping() {
    }

    public AbstractParamMapping(String field, String expression) {
        this.field = field;
        this.expression = expression;
    }

    public AbstractParamMapping(String field, String type, String fixedValue, String expression) {
        this.field = field;
        this.type = type;
        this.fixedValue = fixedValue;
        this.expression = expression;
    }
}
