package com.wakedata.dw.open.service.exportapi.dto.connector;

import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 连接器信息导出DTO
 *
 * @author wujunqiang
 * @since 2023/1/12 14:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "连接器信息导出DTO", description = "连接器信息导出DTO")
public class ConnectorExportInfoDTO {

    @ApiModelProperty(value = "连接器信息集合")
    private List<ConnectorDTO> connectorList;

}
