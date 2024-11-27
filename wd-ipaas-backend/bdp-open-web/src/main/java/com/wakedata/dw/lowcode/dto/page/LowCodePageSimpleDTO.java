package com.wakedata.dw.lowcode.dto.page;

import com.wakedata.dw.lowcode.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 页面基础信息DTO
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("页面基础信息")
public class LowCodePageSimpleDTO extends BaseDTO {

    /**
     * 页面名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 页面标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 页面参数
     */
    @ApiModelProperty("参数")
    private String params;

    /**
     * 页面封面
     */
    @ApiModelProperty("封面")
    private String cover;

    /**
     * 页面分类
     */
    @ApiModelProperty("分类Id")
    private String categoryId;

}
