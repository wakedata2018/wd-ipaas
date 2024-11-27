package com.wakedata.dw.open.service.exportapi.dto.openapi;

import com.wakedata.dw.open.enums.DataAssetEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author wujunqiang
 * @since 2023/2/2 10:16
 */
@Data
@ApiModel(value = "导出开放平台API需要传的参数DTO", description = "导出开放平台API需要传的参数DTO")
public class ExportOpenApiParamDTO implements Serializable {

    @NotEmpty(message = "请选择分组")
    @ApiModelProperty(value = "开放平台API分组ID集合")
    private List<Integer> apiGroupIdList;

    /**
     * @see DataAssetEnums.DataApiType
     */
    @NotEmpty(message = "请选择API类型")
    @ApiModelProperty(value = "开放平台API类型集合（0：SQL模式、1：数据表模式、2：HTTP模式、3：服务编排、7：WebService）")
    private List<Integer> apiTypeList;

}
