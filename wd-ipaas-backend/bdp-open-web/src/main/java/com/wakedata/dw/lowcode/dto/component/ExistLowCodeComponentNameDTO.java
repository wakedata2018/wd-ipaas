package com.wakedata.dw.lowcode.dto.component;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 组件名称是否存在入参
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/24
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("组件名称是否存在")
public class ExistLowCodeComponentNameDTO {

    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @ApiModelProperty("id")
    private Integer id;

}
