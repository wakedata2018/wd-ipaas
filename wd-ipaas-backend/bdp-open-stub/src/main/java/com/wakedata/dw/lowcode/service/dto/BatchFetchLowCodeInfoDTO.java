package com.wakedata.dw.lowcode.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wanghu@wakedata.com
 * @title 批量获取组件参数
 * @date 2021/11/25
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("批量获取组件参数")
public class BatchFetchLowCodeInfoDTO {

    @ApiModelProperty("标识符（名称）")
    @NotBlank(message = "标识符（名称）不能为空")
    private String name;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
