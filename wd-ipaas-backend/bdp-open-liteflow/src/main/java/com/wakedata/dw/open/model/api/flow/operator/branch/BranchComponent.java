package com.wakedata.dw.open.model.api.flow.operator.branch;

import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分支算子组件
 *
 * @author wujunqiang
 * @since 2022/11/4 17:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BranchComponent extends AbstractComponent {

    /**
     * 分支算子参数属性包装对象，为了前端和判断算子用同一套处理逻辑所以创建了这个类
     */
    private BranchAttribute dataAssetApi;

}
