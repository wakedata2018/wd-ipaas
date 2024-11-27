package com.wakedata.dw.lowcode.dto.category;

import com.wakedata.dw.lowcode.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分类信息DTO
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("分类信息")
public class LowCodePageCategoryDTO extends BaseDTO {

    /**
     * 页面名称
     */
    @ApiModelProperty("名称")
    private String name;

}

