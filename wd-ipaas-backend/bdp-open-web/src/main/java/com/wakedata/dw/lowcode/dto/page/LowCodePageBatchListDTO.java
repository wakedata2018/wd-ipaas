package com.wakedata.dw.lowcode.dto.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 批量获取页面详情
 *
 * @author wanghu@wakedata.com
 * @date 2021/12/1
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("批量获取页面详情")
public class LowCodePageBatchListDTO extends LowCodePageSimpleDTO {

    /**
     * 内容（compressedContent不为空则返回, 否则返回content）
     */
    @ApiModelProperty("内容（compressedContent不为空则返回, 否则返回content）")
    private String content;

}
