package com.wakedata.dw.open.service.exportapi.dto.openapi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 导入开放平台API结果统计DTO
 *
 * @author wujunqiang
 * @since 2023/2/7 14:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "导入开放平台API/连接器结果统计DTO", description = "导入开放平台API/连接器结果统计DTO")
public class ImportOpenApiResultDTO implements Serializable {

    @ApiModelProperty(value = "导入API总条数")
    private Integer importTotal;

    @ApiModelProperty(value = "导入成功总数")
    private Integer successTotal;

    @ApiModelProperty(value = "导入失败总数")
    private Integer failTotal;

}
