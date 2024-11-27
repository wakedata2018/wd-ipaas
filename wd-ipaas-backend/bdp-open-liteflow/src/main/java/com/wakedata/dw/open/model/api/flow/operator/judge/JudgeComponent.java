package com.wakedata.dw.open.model.api.flow.operator.judge;

import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.model.api.flow.operator.api.PublicKind;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 判断算子组件
 *
 * @author wujunqiang
 * @since 2022/9/19 17:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JudgeComponent extends AbstractComponent {

    /**
     * 判断算子内部对象
     */
    private JudgeParam dataAssetApi;

    private PublicKind publicKind = PublicKind.self;
}
