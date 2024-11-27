package com.wakedata.dw.open.service.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/8/23 11:36
 */
@Data
public class PublicReturnParametersDTO {

    @ApiModelProperty(value = "属性名称")
    private String attributeName;

    @ApiModelProperty(value = "属性类型")
    private Object attributeType;

    @ApiModelProperty(value = "描述")
    private String attributeDescribe;
}
