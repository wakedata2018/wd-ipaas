package com.wakedata.dw.lowcode.dto.config;

import com.wakedata.dw.lowcode.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 配置信息DTO
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("配置信息")
public class LowCodeConfigDTO extends BaseDTO {

    /**
     * 配置类型
     */
    @ApiModelProperty("类型")
    private String type;

    /**
     * 配置内容
     */
    @ApiModelProperty("内容")
    private String content;
}
