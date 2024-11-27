package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_api_request_param")
@ApiModel(value = "第三方API请求参数表")
public class ConnectorApiRequestParamPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("第三方apiId")
    @Column(name = "connector_api_id")
    private Long connectorApiId;

    @ApiModelProperty(value = "参数名称")
    @Column(name = "asset_columns")
    private String assetColumns;

    @ApiModelProperty(value = "数据类型")
    @Column(name = "asset_data_type")
    private String assetDataType;

    @ApiModelProperty(value = "描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty(value = "是否必须")
    @Column(name = "required")
    private Boolean required;

    @ApiModelProperty(value = "示例值")
    @Column(name = "sample")
    private String sample;

    @ApiModelProperty(value = "参数位置")
    @Column(name = "http_param_kind")
    private HttpParamKind httpParamKind;

    @ApiModelProperty(value = "jsonschema类型数据样例")
    @Column(name = "json_schema")
    private String jsonSchema;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @Column(name = "update_by")
    private String updateBy;

}
