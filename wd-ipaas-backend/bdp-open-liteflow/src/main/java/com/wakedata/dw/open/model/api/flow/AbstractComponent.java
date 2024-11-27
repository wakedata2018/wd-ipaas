package com.wakedata.dw.open.model.api.flow;

import lombok.Data;

/**
 * 组件抽象
 *
 * @author ZhangXueJun
 * @title AbstractComponent
 * @date 2019/10/14 10:11
 * @projectName dw-stream
 * @description
 */
@Data
public abstract class AbstractComponent implements Component {

    /**
     * 组件Id
     */
    private Long componentId;

    /**
     * 组件名称
     */
    private String name;

    /**
     * 组件类型
     */
    private Kind kind;
}
