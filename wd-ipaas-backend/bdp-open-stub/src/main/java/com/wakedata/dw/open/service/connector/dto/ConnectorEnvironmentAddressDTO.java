package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 10:21
 */
@Data
@ApiModel(value = "连接器平台环境信息DTO", description = "连接器平台环境信息DTO")
public class ConnectorEnvironmentAddressDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty("环境名称")
    private String addressName;

    @ApiModelProperty("环境地址")
    private String environmentAddress;

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
