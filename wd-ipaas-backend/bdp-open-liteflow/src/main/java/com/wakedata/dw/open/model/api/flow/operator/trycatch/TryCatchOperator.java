package com.wakedata.dw.open.model.api.flow.operator.trycatch;

import cn.hutool.core.collection.CollUtil;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 捕获异常算子
 *
 * @author wujunqiang
 * @since 2023/3/15 10:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TryCatchOperator extends AbstractOperator<TryCatchComponent>
        implements Operator<TryCatchComponent>, InputMultiEdge<Operator>, OutputMultiEdge<Operator> {

    /**
     * 出边算子集合
     */
    private Set<String> outputOperators;

    /**
     * 捕获异常算子中try块内子流程第一层算子id，并行流程可能包含多个算子，由前端传参，不传子流程无法执行
     */
    private Set<String> subFirstTryOperators;

    /**
     * 捕获异常算子中catch块内子流程第一层算子id，并行流程可能包含多个算子，由前端传参，不传子流程无法执行
     */
    private Set<String> subFirstCatchOperators;

    /**
     * 捕获异常算子内部try算子,无需前端传参，在编排运行时根据子流程的parentOperatorId进行构造
     */
    private Set<String> subTryOperators;

    /**
     * 捕获异常算子内部catch算子,无需前端传参，在编排运行时根据子流程的parentOperatorId进行构造
     */
    private Set<String> subCatchOperators;

    @Override
    public void accept(Visitor visitor) throws Exception {

    }

    @Override
    public Set<String> getOutputOperators() {
        return outputOperators;
    }

    @Override
    public void addOutOperators(Operator... outputOperators) {
        for (Operator outputOperator : outputOperators) {
            this.outputOperators.add(outputOperator.getOperatorId());
        }
    }

    /**
     * 批量添加内部try算子
     *
     * @param subTryOperatorIds try块中所有算子id
     */
    public void batchAddSubTryOperators(Set<String> subTryOperatorIds) {
        this.subTryOperators = new HashSet<>();
        if (CollUtil.isNotEmpty(subTryOperatorIds)) {
            this.subTryOperators.addAll(subTryOperatorIds);
        }
    }

    /**
     * 批量添加内部catch算子
     *
     * @param subCatchOperatorIds catch块中所有算子id
     */
    public void batchAddSubCatchOperators(Set<String> subCatchOperatorIds) {
        this.subCatchOperators = new HashSet<>();
        if (CollUtil.isNotEmpty(subCatchOperatorIds)) {
            this.subCatchOperators.addAll(subCatchOperatorIds);
        }
    }

}
