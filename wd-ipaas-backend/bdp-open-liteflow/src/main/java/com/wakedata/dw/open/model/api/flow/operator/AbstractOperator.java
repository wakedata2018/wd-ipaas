package com.wakedata.dw.open.model.api.flow.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wakedata.dw.open.model.api.flow.Component;
import lombok.Getter;
import lombok.Setter;

/**
 * 算子基类
 *
 * @author ZhangXueJun
 * @title AbstractOperator
 * @date 2021/3/22 11:34
 * @projectName dw-open
 * @description
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractOperator<C extends Component> extends FriendlessOperator {
    /**
     * 是否被标记过
     */
    @Setter
    @Getter
    @JsonIgnore
    private volatile transient boolean marked;

    /**
     * 算子所在DAG 层级，用于计算LiteFlow Chain
     */
    @Setter
    @Getter
    @JsonIgnore
    private volatile transient int rank = 0;

    @Setter
    @Getter
    private C component;

    /**
     * for 序列化
     */
    public AbstractOperator() {
    }

    public AbstractOperator(C component) {
        this.component = component;
    }

    @Override
    public String toString() {
        return "AbstractOperator{" +
                "operatorId=" + getOperatorId() +
                ", name='" + getName() + '\'' +
                ", marked=" + marked +
                ", component=" + component +
                '}';
    }

    /**
     * 获取try标识
     *
     * @return try标识
     */
    @JsonIgnore
    public String getTryTag() {
        return Operator.TRY_TAG;
    }

    /**
     * 获取catch标识
     *
     * @return catch标识
     */
    @JsonIgnore
    public String getCatchTag() {
        return Operator.CATCH_TAG;
    }
}
