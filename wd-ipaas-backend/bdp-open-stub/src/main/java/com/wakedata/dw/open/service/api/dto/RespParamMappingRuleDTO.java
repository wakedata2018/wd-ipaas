package com.wakedata.dw.open.service.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/3 9:45
 */
@Data
@ApiModel(value = "RespParamMappingRuleDTO", description = "api响应体参数映射规则实体DTO")
public class RespParamMappingRuleDTO implements Serializable {

    private static final long serialVersionUID = -1;

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("响应体参数映射规则名称")
    @NotNull(message = "respParamMappingRuleName is not null")
    private String respParamMappingRuleName;

    @ApiModelProperty("响应体参数映射规则JsonSchema")
    @NotBlank(message = "respParamMappingRuleJsonSchema is not null")
    private String respParamMappingRuleJsonSchema;

    @ApiModelProperty("启用状态；0-未启用，1-已启用")
    @NotNull(message = "status is not null")
    private Integer status;

    @ApiModelProperty("是否是默认映射规则；false-否，1-true")
    private Boolean isDefaultRule;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("规则关联的api数量")
    private Integer apiCount;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public RespParamMappingRuleDTO() {

    }

    public RespParamMappingRuleDTO(Integer id, String respParamMappingRuleName) {
       this.id = id;
       this.respParamMappingRuleName = respParamMappingRuleName;
    }
}
