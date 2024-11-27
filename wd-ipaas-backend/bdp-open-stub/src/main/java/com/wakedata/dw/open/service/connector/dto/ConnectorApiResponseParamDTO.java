package com.wakedata.dw.open.service.connector.dto;

import com.wakedata.dw.open.parammapping.HttpParamKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wujunqiang
 * @since 2022/11/21 17:59
 */
@Data
@ApiModel(value = "第三方api响应参数DTO", description = "第三方api响应参数DTO")
public class ConnectorApiResponseParamDTO implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("第三方apiId")
    private Long connectorApiId;

    @ApiModelProperty(value = "参数名称")
    private String assetColumns;

    @ApiModelProperty(value = "数据类型")
    private String assetDataType;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "示例值")
    private String sample;

    @ApiModelProperty(value = "参数位置")
    private HttpParamKind httpParamKind;

    @ApiModelProperty(value = "响应体内容(jsonSchema或xml)")
    private String responsePostData;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("租户id")
    private Long LesseeId;
}
