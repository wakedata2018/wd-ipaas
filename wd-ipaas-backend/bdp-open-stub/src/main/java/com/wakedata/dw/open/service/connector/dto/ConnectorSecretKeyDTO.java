package com.wakedata.dw.open.service.connector.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wujunqiang
 * @since 2022/11/21 10:23
 */
@Data
@ApiModel(value = "平台密钥DTO", description = "平台分类DTO")
public class ConnectorSecretKeyDTO implements Serializable {

    @ApiModelProperty("主键id")
    private Long id;

    @NotNull(message = "平台不能为空")
    @ApiModelProperty("平台id")
    private Long connectorId;

    @NotNull(message = "环境地址不能为空")
    @ApiModelProperty("关联的平台环境地址id")
    private Long environmentId;

    @ApiModelProperty("关联的平台环境地址")
    private String environmentAddress;

    @NotEmpty(message = "密钥名称不能为空")
    @ApiModelProperty("密钥名称")
    private String name;

    @ApiModelProperty("密钥唯一标识")
    private String secretKey;

    @ApiModelProperty("是否启用")
    private Integer isEnable;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("鉴权参数值")
    private String paramsJson;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("平台名称")
    private String connectName;

    @ApiModelProperty("环境名称")
    private String environmentName;

    @ApiModelProperty("租户id")
    private Long lesseeId;

}
