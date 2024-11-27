package com.wakedata.dw.open.model.api.flow.operator.branch;

import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import lombok.Data;

import java.util.List;

/**
 * 分支算子属性包装类
 *
 * @author wujunqiang
 * @since 2022/12/14 11:33
 */
@Data
public class BranchAttribute {

    /**
     * 分支条件集合
     */
    private List<BranchParam> branchParams;

    /**
     * 存放分支算子的出边线条连接信息
     */
    private List<ApiRuleAttr> apiAttrs;

}
