package com.wakedata.dw.lowcode.dto.config;

import com.wakedata.dw.lowcode.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 新增配置信息DTO
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("新增配置信息")
public class AddLowCodeConfigDTO extends BaseDTO {

    /**
     * 配置类型
     */
    @ApiModelProperty("类型")
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 配置内容
     */
    @ApiModelProperty("内容")
    private String content;
}
