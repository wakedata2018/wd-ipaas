package com.wakedata.dw.open.service.importapi.dto.connector;

import com.wakedata.dw.open.service.exportapi.dto.connector.ConnectorExportInfoDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 连接器信息导入DTO
 *
 * @author wujunqiang
 * @since 2023/2/6 17:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "连接器信息导入DTO", description = "连接器信息导入DTO")
public class ConnectorImportInfoDTO extends ConnectorExportInfoDTO implements Serializable {
}
