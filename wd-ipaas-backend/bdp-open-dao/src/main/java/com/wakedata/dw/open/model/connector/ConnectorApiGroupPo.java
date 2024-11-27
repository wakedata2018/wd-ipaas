package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_api_group")
@ApiModel(value = "第三方接口分组表")
public class ConnectorApiGroupPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("平台id")
    @Column(name = "connector_id")
    private Long connectorId;

    @ApiModelProperty(value = "分组名称")
    @Column(name = "group_name")
    private String groupName;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @Column(name = "update_by")
    private String updateBy;
}
