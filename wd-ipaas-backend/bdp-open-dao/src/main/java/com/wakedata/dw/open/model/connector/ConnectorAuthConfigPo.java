package com.wakedata.dw.open.model.connector;

import com.wakedata.common.core.constants.ActiveStatusEnum;
import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_auth_config")
@ApiModel(value = "连接器鉴权配置表")
public class ConnectorAuthConfigPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("连接器id")
    @Column(name = "connector_id")
    private Long connectorId;

    @ApiModelProperty(value = "环境id")
    @Column(name = "connector_environment_id")
    private Long connectorEnvironmentId;

    @ApiModelProperty(value = "关联的鉴权接口id")
    @Column(name = "connector_api_id")
    private Long connectorApiId;

    @ApiModelProperty(value = "鉴权名称")
    @Column(name = "auth_name")
    private String authName;

    @ApiModelProperty(value = "鉴权标识")
    @Column(name = "auth_identification")
    private String authIdentification;

    @ApiModelProperty(value = "鉴权配置;配置鉴权参数获取方式、对应key、以及接口校验规则")
    @Column(name = "auth_config")
    private String authConfig;

    /**
     * {@link ActiveStatusEnum}
     */
    @ApiModelProperty(value = "状态 0：未启用 1：已启用")
    @Column(name = "status")
    private Integer status;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @Column(name = "update_by")
    private String updateBy;

    @Transient
    @ApiModelProperty("连接器名称")
    private String connectorName;
    @Transient
    @ApiModelProperty("连接器环境名称")
    private String connectorEnvironmentName;
    @Transient
    @ApiModelProperty("连接器api名称")
    private String connnectorApiName;
    @Transient
    @ApiModelProperty("连接器api分组id")
    private Long connectorApiGroupId;
    @Transient
    @ApiModelProperty("连接器api分组名称")
    private String connectorApiGroupName;
}
