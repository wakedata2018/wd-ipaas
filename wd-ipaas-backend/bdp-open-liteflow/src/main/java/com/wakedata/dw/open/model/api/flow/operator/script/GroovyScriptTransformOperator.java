package com.wakedata.dw.open.model.api.flow.operator.script;

import com.wakedata.dw.open.model.api.flow.operator.TransformOperator;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;

import javax.persistence.Table;

/**
 * @author ZhangXueJun
 * @title GroovyFunctionTransformOperator
 * @date 2021/3/26 17:29
 * @projectName bdp-open
 * @description
 */
@Data
@Table(name = "dw_open_api_operator_groovy_script")
@PK({"apiId", "operatorId"})
public class GroovyScriptTransformOperator extends TransformOperator.MultiInputOutputEdge {

    /**
     * groovy脚本
     */
    @Column("groovy")
    private String groovy;

    /**
     * groovy脚本返回值参数（json schema字符串）
     */
    @Column("resultData")
    private String resultData;

    @Override
    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
    }
}
