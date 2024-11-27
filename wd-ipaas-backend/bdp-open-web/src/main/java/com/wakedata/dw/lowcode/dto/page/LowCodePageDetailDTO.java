package com.wakedata.dw.lowcode.dto.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 页面详情DTO
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("页面详情")
public class LowCodePageDetailDTO extends LowCodePageSimpleDTO {

    /**
     * 压缩内容
     */
    @ApiModelProperty("压缩内容")
    private String compressedContent;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    private String content;

}
