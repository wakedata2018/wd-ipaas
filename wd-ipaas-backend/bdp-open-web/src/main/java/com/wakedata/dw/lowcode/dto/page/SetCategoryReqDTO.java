package com.wakedata.dw.lowcode.dto.page;

import com.wakedata.dw.lowcode.dto.IdDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wanghu@wakedata.com
 * @title 设置分类参数
 * @date 2021/12/2
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("设置分类参数")
public class SetCategoryReqDTO extends IdDTO {

    @NotNull(message = "分类id不能为空")
    @ApiModelProperty("分类id")
    private Integer categoryId;
}
