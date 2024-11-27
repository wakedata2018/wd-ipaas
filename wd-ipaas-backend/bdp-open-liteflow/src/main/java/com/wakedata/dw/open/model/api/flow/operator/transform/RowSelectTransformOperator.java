package com.wakedata.dw.open.model.api.flow.operator.transform;

import com.wakedata.dw.open.model.api.flow.operator.TransformOperator;
import lombok.Data;

/**
 * 转换算子：对某个算子进行条件选择
 * $.a[?(b='b1' || b='b')]
 *
 * @author ZhangXueJun
 * @title JsonPathSelectFieldsTransformOperator
 * @date 2021/5/25 16:51
 * @projectName dw-open
 * @description
 */
@Data
public class RowSelectTransformOperator
        extends TransformOperator.InputMultiOutputEdge {

    private String rowExpression;

    @Override
    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
    }
}
