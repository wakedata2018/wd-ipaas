package com.wakedata.dw.open.model.api.flow.operator.edge;

import com.wakedata.dw.open.model.api.flow.operator.Operator;

import java.util.Set;

/**
 * 出边
 *
 * @author ZhangXueJun
 * @title OutEdge
 * @date 2019/10/26 15:00
 * @projectName dw-stream
 * @description
 */
public interface OutputMultiEdge<Op extends Operator> extends Edge {

    /**
     * 获取出边算子
     *
     * @return
     */
    Set<String> getOutputOperators();

    /**
     * 增加算子
     *
     * @param outputOperators
     */
    void addOutOperators(Op... outputOperators);

}
