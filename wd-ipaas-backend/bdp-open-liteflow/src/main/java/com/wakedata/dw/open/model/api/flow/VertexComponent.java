package com.wakedata.dw.open.model.api.flow;

import lombok.NoArgsConstructor;

/**
 * 虚拟顶点组件
 *
 * @author ZhangXueJun
 * @title VertexComponent
 * @date 2019/10/26 15:49
 * @projectName dw-stream
 * @description
 */
@NoArgsConstructor
public class VertexComponent extends AbstractComponent {

    private VertexComponent(Long componentId, String name) {
        setComponentId(componentId);
        setName(name);
        setKind(Kind.vertex);
    }

    /**
     * 虚拟Transform组件
     */
    public final static VertexComponent VERTEX_COMPONENT = new VertexComponent(-1L, "virtual vertex component");

}
