package com.wakedata.dw.lowcode.dto.block;

import com.wakedata.dw.lowcode.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 编辑区块入参
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/24
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("编辑区块入参")
public class EditLowCodeBlockDTO extends BaseDTO {

    /**
     * 区块名称
     */
    @ApiModelProperty("区块名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 区块内容, JSON 字符串
     */
    @ApiModelProperty("区块内容")
    private String dsl;

    /**
     * 截图
     */
    @ApiModelProperty("截图")
    private String snapshot;

}
