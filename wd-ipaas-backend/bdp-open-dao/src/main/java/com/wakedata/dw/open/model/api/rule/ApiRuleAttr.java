package com.wakedata.dw.open.model.api.rule;

import com.wakedata.dw.open.interceptor.annotation.CreateTime;
import com.wakedata.dw.open.interceptor.annotation.UpdateTime;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 规则连线对象
 * Date: 2021/12/10
 * @author wangchensheng
 */
@Table(name = "dw_open_api_cond_rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRuleAttr extends AbstractApiAttr {

    /**
     * 规则名称
     */
    @Column(name = "name")
    @NotBlank(message = "规则名不能为空")
    private String ruleName;

    /**
     * 规则所属的节点Id
     */
    @Column(name = "next_api_id")
    private Integer toApiId;

    /**
     * 规则表达式
     */
    @Column(name = "rule_expression")
    @NotBlank(message = "规则表达式不能为空")
    private String ruleExpression;

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
     * 租户ID,目前前端能传
     */
    @Column(name = "lessee_id")
    private Long lesseeId;

    /**
     * 规则所属算子ID
     */
    @Column(name = "from_operator_id")
    @NotBlank(message = "规则所属算子ID不能为空")
    private String fromOperatorId;

    /**
     * 规则对应算子ID
     */
    @Column(name = "to_operator_id")
    @NotBlank(message = "规则对应算子ID不能为空")
    private String toOperatorId;

    @Transient
    private List<ApiCondition> conditionList;
}
