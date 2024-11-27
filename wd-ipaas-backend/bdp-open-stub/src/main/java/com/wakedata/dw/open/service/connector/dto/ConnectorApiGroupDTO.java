package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 17:21
 */
@Data
@ApiModel(value = "连接器平台api分组信息DTO", description = "连接器平台api分组信息DTO")
public class ConnectorApiGroupDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @NotNull(message = "connectorId cannot be null")
    @ApiModelProperty("平台id")
    private Long connectorId;

    @NotBlank(message = "groupName cannot be null")
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("该api分组下的api列表")
    private List<ConnectorApiDTO> apiDTOList;

    /**
     * 目前这个参数只有导出连接器API时用到，所以在文档中隐藏此参数
     */
    @ApiModelProperty(value = "该api分组下的api详情", hidden = true)
    private List<ConnectorApiDetailDTO> apiDetailList;

}
