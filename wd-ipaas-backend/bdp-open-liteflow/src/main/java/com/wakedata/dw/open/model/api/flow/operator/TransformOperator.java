package com.wakedata.dw.open.model.api.flow.operator;

import com.google.common.collect.Sets;
import com.wakedata.dw.open.model.api.flow.TransformComponent;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import lombok.Data;
import lombok.Getter;
import org.nutz.dao.entity.annotation.Column;

import java.util.Set;

/**
 * @author ZhangXueJun
 * @title TransformOperator
 * @date 2021/3/22 11:39
 * @projectName dw-open
 * @description
 */
@Data
public abstract class TransformOperator extends AbstractOperator<TransformComponent> implements Operator {

    @Column("TASK_ID")
    private Long apiId;

    /**
     * 算子：有多条出边,多入边
     *
     * @author ZhangXueJun
     * @title OperatorWithOuptEdge
     * @date 2019/10/28 11:09
     * @projectName dw-stream
     * @description
     */
    public abstract static class MultiInputOutputEdge
            extends TransformOperator
            implements OutputMultiEdge<Operator>, InputMultiEdge<Operator> {

        @Getter
        private Set<String> outputOperators;

        public MultiInputOutputEdge() {
            this.outputOperators = Sets.newHashSet();
        }

        @Override
        public void addOutOperators(Operator... outputOperators) {
            for (Operator outputOperator : outputOperators) {
                this.outputOperators.add(outputOperator.getOperatorId());
            }
        }
    }

    /**
     * 算子：有多条出边,单入边
     *
     * @author ZhangXueJun
     * @title OperatorWithOuptEdge
     * @date 2019/10/28 11:09
     * @projectName dw-stream
     * @description
     */
    public abstract static class InputMultiOutputEdge
            extends TransformOperator
            implements OutputMultiEdge<Operator>, InputEdge<Operator> {

        @Getter
        private Set<String> outputOperators;

        public InputMultiOutputEdge() {
            this.outputOperators = Sets.newHashSet();
        }

        @Override
        public void addOutOperators(Operator... outputOperators) {
            for (Operator outputOperator : outputOperators) {
                this.outputOperators.add(outputOperator.getOperatorId());
            }
        }
    }

}
