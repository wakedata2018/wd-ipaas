package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_params")
@ApiModel(value = "鉴权字段表")
public class ConnectorParamsPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("平台id")
    @Column(name = "connector_id")
    private Long connectorId;

    @ApiModelProperty("参数名称")
    @Column(name = "param_name")
    private String paramName;

    @ApiModelProperty("参数类型")
    @Column(name = "param_type")
    private String paramType;

    @ApiModelProperty("是否必须")
    @Column(name = "is_required")
    private Integer isRequired;

    @ApiModelProperty("展示脱敏类型 0：不脱敏 1：中间部分脱敏")
    @Column(name = "hidden_type")
    private Integer hiddenType;

    @ApiModelProperty("示例值")
    @Column(name = "default_value")
    private String defaultValue;

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
