package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 16:21
 */
@Data
@ApiModel(value = "连接器平台api信息DTO", description = "连接器平台api信息DTO")
public class ConnectorApiDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty("平台名称")
    private String connectorName;

    @ApiModelProperty("连接器API鉴权类型")
    private ConnectorApiAuthTypeEnum connectorApiAuthType;

    @NotNull(message = "apiGroupId cannot be null")
    @ApiModelProperty(value = "接口分组id")
    private Long apiGroupId;

    @ApiModelProperty(value = "接口分组名称")
    private String apiGroupName;

    @NotBlank(message = "apiName cannot be null")
    @ApiModelProperty(value = "api名称")
    private String apiName;

    @NotBlank(message = "apiMethod cannot be null")
    @ApiModelProperty(value = "api地址")
    private String apiMethod;

    @NotBlank(message = "reqMethod cannot be null")
    @ApiModelProperty(value = "请求方式")
    private DataAssetEnums.ReqMethod reqMethod;

    @NotBlank(message = "apiType cannot be null")
    @ApiModelProperty(value = "接口类型")
    private DataAssetEnums.DataApiType apiType;

    @NotBlank(message = "enableStatus cannot be null")
    @ApiModelProperty(value = "api上线状态")
    private DataAssetEnums.DataAccessPublishStatus enableStatus;

    @ApiModelProperty(value = "api描述")
    private String apiDescription;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("租户id")
    private Long lesseeId;

}
