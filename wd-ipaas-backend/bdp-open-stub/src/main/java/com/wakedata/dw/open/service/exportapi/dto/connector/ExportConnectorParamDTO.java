package com.wakedata.dw.open.service.exportapi.dto.connector;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 导出连接器需要传的参数DTO
 *
 * @author wujunqiang
 * @since 2023/1/12 11:46
 */
@Data
@ApiModel(value = "导出连接器需要传的参数DTO", description = "导出连接器需要传的参数DTO")
public class ExportConnectorParamDTO implements Serializable {

    @NotEmpty(message = "请选择连接器")
    @ApiModelProperty(value = "连接器ID集合")
    private List<Long> connectorIdList;

}
