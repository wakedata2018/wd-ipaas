package com.wakedata.dw.open.model.api.rule;

import com.wakedata.dw.open.interceptor.annotation.CreateTime;
import com.wakedata.dw.open.interceptor.annotation.UpdateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 分子算子条件
 * Date: 2021/12/10
 * @author wangchensheng
 */
@Table(name = "DW_OPEN_API_COND_DETAIL")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCondition {

    /**
     * 规则id
     */
    @Column(name = "rule_id")
    private Integer ruleId;

    /**
     * 条件id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 条件编号
     */
    @Column(name = "condition_number")
    @NotBlank(message = "条件编码不能为空")
    private String conditionNumber;

    /**
     * 数据来源节点
     */
    @Column(name = "date_source_id")
    @NotBlank(message = "数据来源节点不能为空,需要连线")
    private String dateSourceId;

    /**
     * 数据来源表达式
     */
    @Column(name = "date_source_expression")
    @NotBlank(message = "数据来源表达式不能为空")
    private String dateSourceExpression;

    /**
     * 判断符号
     * 大于 0,大于等于 1,等于 2,小于 3,小于等于 4,不等于 5
     */
    @Column(name = "sign_type")
    @NotBlank(message = "判断符号不能为空")
    private Integer signType;

    /**
     * 取值类型
     * 变量 0,常量 1
     */
    @Column(name = "value_type")
    @NotBlank(message = "取值类型不能为空")
    private Integer valueType;

    /**
     * 取值来源节点
     */
    @Column(name = "value_source_id")
    @NotBlank(message = "取值来源节点不能为空")
    private String valueSourceId;

    /**
     * 取值来源表达式
     */
    @Column(name = "value_source_expression")
    @NotBlank(message = "取值来源表达式不能为空")
    private String valueSourceExpression;

    /**
     * 常量值
     */
    @Column(name = "constant_value")
    private String constantValue;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @CreateTime
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @UpdateTime
    private Date updateTime;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 更新人
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 租户ID
     */
    @Column(name = "lessee_id")
    private Long lesseeId;
}
