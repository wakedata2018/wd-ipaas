package com.wakedata.dw.open.service.connector.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/22 10:00
 */
@Data
@ApiModel(value = "连接器平台api分组信息关联平台DTO", description = "连接器平台api分组信息关联平台DTO")
public class ConnectorApiGroupRelevanceDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty("平台id")
    private Long connectorId;

    @ApiModelProperty(value = "名称")
    private String connectorName;

    @ApiModelProperty(value = "分组名称")
    private List<ConnectorApiGroupDTO> connectorApiGroupDTOList;
}
