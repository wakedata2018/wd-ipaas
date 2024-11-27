package com.wakedata.dw.open.service.connector.dto;

import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wujunqiang
 * @since 2022/11/21 18:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "第三方API详情DTO", description = "第三方API详情DTO")
public class ConnectorApiDetailDTO implements Serializable {

    @ApiModelProperty("第三方API基本信息")
    private ConnectorApiDTO connectorApi;

    @ApiModelProperty("第三方API请求参数列表")
    private List<ConnectorApiRequestParamDTO> requestParams;

    @ApiModelProperty("第三方API响应参数列表")
    private List<ConnectorApiResponseParamDTO> responseParams;

    @ApiModelProperty("连接器API鉴权类型")
    private ConnectorApiAuthTypeEnum connectorApiAuthType;

}
