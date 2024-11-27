package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_environment_address")
@ApiModel(value = "平台环境地址表")
public class ConnectorEnvironmentAddressPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("平台id")
    @Column(name = "connector_id")
    private Long connectorId;

    @ApiModelProperty("环境名称")
    @Column(name = "address_name")
    private String addressName;

    @ApiModelProperty("环境地址")
    @Column(name = "environment_address")
    private String environmentAddress;

    @ApiModelProperty("创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty("更新人")
    @Column(name = "update_by")
    private String updateBy;
}
