package com.wakedata.dw.open.model.api.flow;

import lombok.NoArgsConstructor;

/**
 * 结束算子
 *
 * @author ZhangXueJun
 * @title VertexComponent
 * @date 2019/10/26 15:49
 * @projectName dw-stream
 * @description
 */
@NoArgsConstructor
public class EndComponent extends AbstractComponent {

    private EndComponent(Long componentId, String name) {
        setComponentId(componentId);
        setName(name);
        setKind(Kind.end);
    }

    /**
     * 虚拟Transform组件
     */
    public final static EndComponent END_COMPONENT = new EndComponent(-1L, "end component");

}
