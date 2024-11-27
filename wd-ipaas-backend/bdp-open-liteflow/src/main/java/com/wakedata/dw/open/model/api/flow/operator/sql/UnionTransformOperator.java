package com.wakedata.dw.open.model.api.flow.operator.sql;

import com.wakedata.dw.open.model.api.flow.operator.TransformOperator;
import lombok.Data;
import org.nutz.dao.entity.annotation.PK;

import javax.persistence.Table;

/**
 * @author ZhangXueJun
 * @title UnionTransformOperator
 * @date 2021/3/26 17:25
 * @projectName bdp-open
 * @description
 */
@Data
@Table(name = "DW_OPEN_API_OPERATOR_UNION")
@PK({"apiId", "operatorId"})
public class UnionTransformOperator
        extends TransformOperator.MultiInputOutputEdge {

    public UnionTransformOperator() {
    }

    @Override
    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
    }
}