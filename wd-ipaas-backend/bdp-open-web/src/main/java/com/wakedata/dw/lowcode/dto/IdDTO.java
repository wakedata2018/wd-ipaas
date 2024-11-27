package com.wakedata.dw.lowcode.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wanghu@wakedata.com
 * @title 前端要求统一传json
 * @date 2021/12/2
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("id参数")
public class IdDTO {

    @NotNull(message = "id不能为空")
    @ApiModelProperty("主键id")
    private Integer id;

}
