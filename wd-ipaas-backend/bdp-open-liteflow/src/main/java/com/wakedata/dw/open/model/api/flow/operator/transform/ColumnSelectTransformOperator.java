package com.wakedata.dw.open.model.api.flow.operator.transform;

import com.google.common.collect.Lists;
import com.wakedata.dw.open.model.api.flow.operator.TransformOperator;
import com.wakedata.dw.open.parammapping.ResponseParamMappings;
import lombok.Data;

import java.util.List;

/**
 * 转换算子:选择多行
 * col1:$a.c1
 * col2:$b.c2
 *
 * 来源不同结果集，默认采用行合并
 *
 * @author ZhangXueJun
 * @title ColumnSelectTransformOperator
 * @date 2021/5/25 16:51
 * @projectName dw-open
 * @description
 */
@Data
public class ColumnSelectTransformOperator
        extends TransformOperator.MultiInputOutputEdge {

    private List<ResponseParamMappings> responseParamMappings = Lists.newArrayList();

    @Override
    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
    }
}
