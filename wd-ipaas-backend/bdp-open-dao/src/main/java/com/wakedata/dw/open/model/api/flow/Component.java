package com.wakedata.dw.open.model.api.flow;

import com.wakedata.dw.open.model.api.flow.operator.Operator;

import java.io.Serializable;

/**
 * @author ZhangXueJun
 * @title Component
 * @date 2021/3/22 11:24
 * @projectName dw-open
 * @description
 */
public interface Component extends Serializable {
    /**
     * 获取组件id
     *
     * @return
     */
    Long getComponentId();

    /**
     * 设置组件id
     *
     * @param componentId
     */
    void setComponentId(Long componentId);

    /**
     * 获取组件名称
     *
     * @return
     */
    String getName();

    /**
     * 设置组件名称
     *
     * @param name
     */
    void setName(String name);

    /**
     * 获取组件类型
     *
     * @return
     */
    Kind getKind();

    enum Kind {

        /**
         * 虚拟顶点组件,用于串接{@link Operator}
         */
        vertex,
        /**
         * 开始
         */
        start,

        api,
        api_external_http,
        api_normal_table,
        api_custom_sql,

        /**
         * 转换组件
         */
        transform,

        transform_sql_union,
        transform_select_column,
        transform_select_row,
        transform_groovery_script,

        /**
         * 事件组件
         */
        event,
        event_send,
        event_receive,
        /**
         * 分支算子
         */
        branch,

        /**
         * 结束算子
         */
        end,
        /**
         * 判断算子
         */
        judge,
        /**
         * 创建变量算子
         */
        create_variable,
        /**
         * 修改变量算子
         */
        update_variable,
        /**
         * 连接器算子
         */
        connector,
        /**
         * 循环算子
         */
        foreach,
        foreach_break,
        /**
         * sql算子
         */
        sql_execute,
        /**
         * 异常算子
         */
        try_catch
    }
}
