package com.wakedata.dw.open.model.api.flow.operator.edge;


import com.wakedata.dw.open.model.api.flow.operator.Operator;

/**
 * 单条出边
 *
 * @author ZhangXueJun
 * @title OutEdge
 * @date 2019/10/26 15:00
 * @projectName dw-stream
 * @description
 */
public interface OutputEdge<Op extends Operator> extends Edge {

    /**
     * 获取出边算子
     *
     * @return
     */
    String getOutputOperator();

    /**
     * 增加算子
     *
     * @param outputOperator
     */
    void addOutputOperator(Op outputOperator);
}
