package com.wakedata.dw.open.model.api.flow.operator.foreach;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import lombok.Data;

/**
 * for循环组件 参数
 *
 * @author luomeng
 * @date 2022/12/5 16:19
 */
@Data
public class ForComponent extends AbstractComponent {

    /**
     * 循环条件，引用数据
     */
    private RequestParamMapping loopCondition;
    /**
     * 指定循环次数，指定次数之后条件无效>0&&<10000
     */
    private Integer loopCount;
    /**
     * 循环类型 0：指定次数 1：指定条件
     */
    private Integer loopType;

    /**
     * 退出条件
     */
    private BreakCondition breakCondition;

    /**
     * 校验是否是按条件执行
     * @return
     */
    public boolean checkIsCondition() {
        return ObjectUtil.isNotNull(this.loopType) && this.loopType == CommonConstant.ONE;
    }
    /**
     * 校验是否按指定次数执行
     * @return
     */
    public boolean checkIsCount() {
        return ObjectUtil.isNotNull(this.loopType) && this.loopType == CommonConstant.ZERO;
    }
}
