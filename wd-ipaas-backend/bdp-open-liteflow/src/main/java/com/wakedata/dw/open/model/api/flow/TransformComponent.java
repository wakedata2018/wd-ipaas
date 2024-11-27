package com.wakedata.dw.open.model.api.flow;

import lombok.NoArgsConstructor;

/**
 * @author ZhangXueJun
 * @title TransformComponent
 * @date 2021/3/27 11:03
 * @projectName bdp-open
 * @description
 */
@NoArgsConstructor
public class TransformComponent extends AbstractComponent {

    private TransformComponent(Long componentId, String name) {
        setComponentId(componentId);
        setName(name);
        setKind(Kind.transform);
    }

    /**
     * 虚拟Transform组件
     */
    public final static TransformComponent VIRTUAL_TRANSFORM_COMPONENT
            = new TransformComponent(-1L, "virtual transform component");
}
