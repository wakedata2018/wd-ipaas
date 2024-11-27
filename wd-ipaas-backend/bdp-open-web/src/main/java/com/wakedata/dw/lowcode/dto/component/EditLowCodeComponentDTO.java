package com.wakedata.dw.lowcode.dto.component;

import com.wakedata.dw.lowcode.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 编辑组件入参
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/24
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("编辑组件")
public class EditLowCodeComponentDTO extends BaseDTO {

    @ApiModelProperty(value = "标识符, 必须唯一", required = true)
    @NotBlank(message = "名称不能为空")
    private String name;

    @ApiModelProperty(value = "标题", required = true)
    @NotBlank(message = "标题不能为空")
    private String title;

    @ApiModelProperty("详细描述")
    private String desc;

    @ApiModelProperty("组件元数据,JSON 字符串")
    private String meta;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("封面")
    private String cover;

    @ApiModelProperty("组件详情，JSON 字符串(批量获取组件接口只有content有值，接口会自动判断获取那个值)")
    private String content;

    @ApiModelProperty(" 压缩版本组件详情")
    private String compressedContent;

}
