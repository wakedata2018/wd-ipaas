package com.wakedata.dw.open.model.api.flow.operator;

import com.wakedata.dw.open.model.api.flow.Component;

import java.io.Serializable;

/**
 * API算子抽象
 *
 * @author ZhangXueJun
 * @title ApiNode
 * @date 2021/3/22 11:18
 * @projectName dw-open
 * @description
 */
public interface Operator<C extends Component> extends Serializable {

    /**
     * 开始算子节点id
     */
    String VERTEX_OPERATOR_ID = "start";

    /**
     * 全局变量节点id
     */
    String GLOBAL_OPERATOR_ID = "globalParam";

    /**
     * 全局变量节点id
     */
    String GLOBAL_OPERATOR_DESC = "全局参数";

    /**
     * 反序列化类属性
     */
    String DESERIALIZER_FIELD_CLAZZ_NAME = "clazzName";
    /**
     * 反序列化算子id
     */
    String DESERIALIZER_FIELD_OPERATOR_ID = "operatorId";

    /**
     * 用于生成异常算子try流程执行链的标识
     */
    String TRY_TAG = "_try";

    /**
     * 用于生成异常算子catch流程执行链的标识
     */
    String CATCH_TAG = "_catch";

    /**
     * 获取算子id
     *
     * @return 算子id
     */
    String getOperatorId();

    /**
     * 设置算子id
     *
     * @param operatorId 算子id
     */
    void setOperatorId(String operatorId);

    /**
     * 获取父算子id
     * @return
     */
    String getParentOperatorId();

    /**
     * 设置父算子id
     * @param parentOperatorId
     */
    void setParentOperatorId(String parentOperatorId);

    /**
     * 获取算子名称
     *
     * @return 算子名称
     */
    String getName();

    /**
     * 设置算子名称
     *
     * @param name 算子名称
     */
    void setName(String name);

    /**
     * 获取算子描述
     *
     * @return 算子描述
     */
    String getDesc();

    /**
     * 设置算子描述
     *
     * @param desc 算子描述
     */
    void setDesc(String desc);

    /**
     * 所属组件
     *
     * @return
     */
    C getComponent();

    /**
     * 是否被标记过
     *
     * @return
     */
    boolean isMarked();

    /**
     * 设置标记
     *
     * @param marked
     */
    void setMarked(boolean marked);

    /**
     * 获取算子所在DAG层级
     *
     * @return
     */
    int getRank();

    /**
     * 设置算在所在DAG 层级
     *
     * @param rank
     */
    void setRank(int rank);

    /**
     * 核心方法，接受Visitor的访问
     *
     * @param visitor
     */
    void accept(Visitor visitor) throws Exception;

    /**
     * 核心方法，接受Visitor的访问
     */
    interface Visitor {

    }
}