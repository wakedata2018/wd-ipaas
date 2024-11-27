package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * api响应体参数映射规则实体
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/2 15:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_api_response_param_mapping_rule")
public class RespParamMappingRulePo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    @Column(name = "id")
    private Integer id;

    @ApiModelProperty("响应体参数映射规则名称")
    @Column(name = "resp_param_mapping_rule_name")
    private String respParamMappingRuleName;

    @ApiModelProperty("响应体参数映射规则JSONSchema")
    @Column(name = "resp_param_mapping_rule_jsonschema")
    private String respParamMappingRuleJsonSchema;

    @ApiModelProperty("响应体参数映射规则")
    @Column(name = "resp_param_mapping_rule")
    private String respParamMappingRule;

    @ApiModelProperty("启用状态；0-未启用，1-已启用")
    @Column(name = "status")
    private Integer status;

    @ApiModelProperty("是否是默认映射规则；0-否，1-是")
    @Column(name = "is_default_rule")
    private Boolean isDefaultRule;

    @ApiModelProperty("描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty("创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty("更新人")
    @Column(name = "update_by")
    private String updateBy;


}
