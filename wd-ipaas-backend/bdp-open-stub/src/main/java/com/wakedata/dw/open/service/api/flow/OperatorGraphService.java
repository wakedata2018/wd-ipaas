package com.wakedata.dw.open.service.api.flow;

import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.OperatorAttribute;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ZhangXueJun
 * @title OperatorGraphService
 * @date 2021/5/11 16:04
 * @projectName dw-open
 * @description
 */
public interface OperatorGraphService {
    /**
     * 获取算子属性
     *
     * @return
     */
    List<OperatorAttribute> listOperatorAttributes();

    /**
     * 获取api组件列表
     *
     * @return
     */
    List<DataSourcePo> apiComponents();

    /**
     * 保存算子
     *
     * @param operator
     * @return
     */
    Operator saveOperator(Operator operator);

    /**
     * 删除算子
     *
     * @param apiId
     * @return
     */
    Boolean deleteOperator(Long apiId);

    /**
     * 有向无环图检测
     *
     * @param operators
     * @return
     */
    Set<String> dsfCycle(Map<String, Operator> operators);

    /**
     * 获取算子的参数模板信息
     *
     * @param apiFlowAttr 流程编排图信息
     * @param operatorType 指定算子(可以为null)
     * @param isForBreak 是否是循环算子
     * @return 算子参数模版集合
     */
    List<LiteFlowAllOperatorTemplateDTO> getLiteFlowAllOperatorTemplateDTOList(ApiFlowAttr apiFlowAttr, String operatorType,Boolean isForBreak);

    /**
     * 获取判断算子的运算符列表
     *
     * @return List<String> 返回已启用的运算符（在枚举中配置）
     */
    Map<String, String> queryAllJudgeOperatorList();
}
