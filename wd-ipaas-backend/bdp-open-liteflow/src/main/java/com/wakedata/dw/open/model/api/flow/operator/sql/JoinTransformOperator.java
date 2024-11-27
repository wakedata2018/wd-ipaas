package com.wakedata.dw.open.model.api.flow.operator.sql;

import com.wakedata.dw.open.model.api.flow.operator.TransformOperator;
import lombok.Data;
import org.nutz.dao.entity.annotation.PK;

import javax.persistence.Table;

/**
 * @author ZhangXueJun
 * @title JoinTransformOperator
 * @date 2021/5/11 9:39
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "DW_OPEN_API_OPERATOR_UNION")
@PK({"apiId", "operatorId"})
public class JoinTransformOperator
        extends TransformOperator.MultiInputOutputEdge {

    @Override
    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
    }
}
