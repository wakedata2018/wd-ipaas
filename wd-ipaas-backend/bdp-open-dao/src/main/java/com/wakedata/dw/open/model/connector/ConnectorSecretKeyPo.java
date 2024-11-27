package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_secret_key")
@ApiModel(value = "平台密钥表")
public class ConnectorSecretKeyPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("平台id")
    @Column(name = "connector_id")
    private Long connectorId;

    @ApiModelProperty("关联的平台环境地址id")
    @Column(name = "environment_id")
    private Long environmentId;

    @ApiModelProperty("密钥名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty("密钥唯一标识")
    @Column(name = "secret_key")
    private String secretKey;

    @ApiModelProperty("是否启用")
    @Column(name = "is_enable")
    private Integer isEnable;

    @ApiModelProperty("描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty("鉴权参数值")
    @Column(name = "params_json")
    private String paramsJson;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty("更新人")
    @Column(name = "update_by")
    private String updateBy;

    @ApiModelProperty(value = "平台名称", hidden = true)
    @Transient
    private String connectName;

    @ApiModelProperty(value = "环境名称", hidden = true)
    @Transient
    private String environmentName;

    @ApiModelProperty(value = "关联的平台环境地址", hidden = true)
    @Transient
    private String environmentAddress;

}
