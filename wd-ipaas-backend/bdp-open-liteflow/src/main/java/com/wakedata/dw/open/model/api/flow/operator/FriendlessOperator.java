package com.wakedata.dw.open.model.api.flow.operator;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author ZhangXueJun
 * @title FriendlessOperator
 * @date 2021/3/22 11:33
 * @projectName dw-open
 * @description
 */
public abstract class FriendlessOperator implements Serializable {

    /**
     * 算子类名
     */
    @Getter
    @Setter
    private String clazzName = "";

    /**
     * 算子id
     */
    @Getter
    @Setter
    private String operatorId = "";

    /**
     * 父算子id，用于嵌套流程算子，如：循环、异常算子
     */
    @Getter
    @Setter
    private String parentOperatorId = "";

    /**
     * 算子名称
     */
    @Setter
    @Getter
    private String name;

    /**
     * 算子名称
     */
    @Setter
    @Getter
    private String desc;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendlessOperator that = (FriendlessOperator) o;
        return Objects.equals(getOperatorId(), that.getOperatorId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOperatorId());
    }
}
