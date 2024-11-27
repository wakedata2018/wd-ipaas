package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 10:27
 */
@Data
@ApiModel(value = "连接器平台鉴权字段DTO", description = "连接器平台鉴权字段DTO")
public class ConnectorParamsDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty("参数名称")
    private String paramName;

    @ApiModelProperty("参数类型")
    private String paramType;

    @ApiModelProperty("是否必须")
    private Integer isRequired;

    @ApiModelProperty("展示脱敏类型 0：不脱敏 1：中间部分脱敏")
    private Integer hiddenType;

    @ApiModelProperty("示例值")
    private String defaultValue;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
