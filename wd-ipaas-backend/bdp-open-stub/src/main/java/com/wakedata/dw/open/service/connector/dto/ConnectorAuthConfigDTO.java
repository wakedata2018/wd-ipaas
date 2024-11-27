package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.common.core.constants.ActiveStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 20:41
 */
@Data
@ApiModel(value = "连接器鉴权配置")
public class ConnectorAuthConfigDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "租户id")
    private Long lesseeId;

    @ApiModelProperty("连接器id")
    @NotNull(message = "连接器id不能为空")
    private Long connectorId;

    @ApiModelProperty("连接器名称")
    private String connectorName;

    @ApiModelProperty(value = "环境id")
    @NotNull(message = "连接器环境id不能为空")
    private Long connectorEnvironmentId;

    @ApiModelProperty("连接器环境名称")
    private String connectorEnvironmentName;

    @ApiModelProperty(value = "关联的鉴权接口id")
    @NotNull(message = "连接器鉴权接口id不能为空")
    private Long connectorApiId;

    @ApiModelProperty("连接器api名称")
    private String connnectorApiName;

    @ApiModelProperty("连接器api分组id")
    private Long connectorApiGroupId;
    @ApiModelProperty("连接器api分组名称")
    private String connectorApiGroupName;

    @ApiModelProperty(value = "鉴权配置名称")
    @NotEmpty(message = "鉴权配置名称不能为空")
    private String authName;

    @ApiModelProperty(value = "鉴权标识")
//    @NotEmpty(message = "鉴权标识不能为空")
    private String authIdentification;

    @ApiModelProperty(value = "鉴权配置;配置鉴权参数获取方式、对应key、以及接口校验规则")
    @NotNull(message = "鉴权配置不能为空")
    private ConnectorAuthParamDTO authConfigParam;

    /**
     * {@link ActiveStatusEnum}
     */
    @ApiModelProperty(value = "状态 0：未启用 1：已启用")
    private Integer status;

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
}
