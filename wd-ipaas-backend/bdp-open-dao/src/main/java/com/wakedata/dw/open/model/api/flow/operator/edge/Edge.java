package com.wakedata.dw.open.model.api.flow.operator.edge;

/**
 * 标记接口：边
 *
 * @author ZhangXueJun
 * @title Edge
 * @date 2019/10/31 15:28
 * @projectName dw-stream
 * @description
 */
public interface Edge {

    enum Type {

        /**
         * 无输入边
         */
        withoutInput,

        /**
         * 有单个输入边
         */
        input,

        /**
         * 多条输入变
         */
        multiInput,

        /**
         * 无输出边
         */
        withoutOutput,

        /**
         * 有单个输出边
         */
        output,

        /**
         * 多条输出变
         */
        multiOutput
    }
}
