package com.wakedata.dw.open.model.api.flow.operator.foreach;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.condition.Condition;
import lombok.Data;

import java.io.Serializable;

/**
 * 退出条件
 * @author luomeng
 * @date 2023/2/6 10:02
 */
@Data
public class BreakCondition implements Serializable {

    /**
     * 指定退出次数
     */
    private Integer count;
    /**
     * 退出类型 0：指定次数 1：指定条件
     */
    private Integer type;

    /**
     * 指定条件
     */
    private Condition condition;

    /**
     * 校验是否是按条件执行
     * @return
     */
    public boolean checkIsCondition() {
        return ObjectUtil.isNotNull(this.type) && this.type == CommonConstant.ONE && ObjectUtil.isNotNull(condition);
    }

    /**
     * 校验是否按指定次数执行
     * @return
     */
    public boolean checkIsCount() {
        return ObjectUtil.isNotNull(this.type) && this.type == CommonConstant.ZERO && ObjectUtil.isNotNull(this.count);
    }

    /**
     * 校验退出
     * @return
     */
    public boolean checkBreak(){
        return checkIsCondition() || checkIsCount();
    }

}
