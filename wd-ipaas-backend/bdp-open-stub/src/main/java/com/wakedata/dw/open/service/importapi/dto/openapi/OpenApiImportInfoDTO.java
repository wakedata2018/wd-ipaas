package com.wakedata.dw.open.service.importapi.dto.openapi;

import com.wakedata.dw.open.service.exportapi.dto.openapi.OpenApiExportInfoDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 开放平台API信息导入DTO
 *
 * @author wujunqiang
 * @since 2023/2/3 16:27
 */
@Data
@ApiModel(value = "开放平台API信息导入DTO", description = "开放平台API信息导入DTO")
public class OpenApiImportInfoDTO extends OpenApiExportInfoDTO implements Serializable {

}
