package com.wakedata.dw.open.service.exportapi.dto.openapi;

import com.wakedata.dw.open.service.vo.ApiDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 开放平台API信息导出DTO
 *
 * @author wujunqiang
 * @since 2023/2/2 10:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "开放平台API信息导出DTO", description = "开放平台API信息导出DTO")
public class OpenApiExportInfoDTO implements Serializable {

    @ApiModelProperty(value = "开放平台api信息集合")
    private List<ApiDetailVo> apiDetailList;

}
