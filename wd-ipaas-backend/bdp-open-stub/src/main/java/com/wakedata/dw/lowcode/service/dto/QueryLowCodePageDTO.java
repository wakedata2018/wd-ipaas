package com.wakedata.dw.lowcode.service.dto;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 页面信息查询参数
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("页面信息查询参数")
public class QueryLowCodePageDTO extends PageQuery {

    @ApiModelProperty(value = "应用id", hidden = true)
    private Integer appId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("分类Id")
    private Integer categoryId;

    @ApiModelProperty("页面名称")
    private String name;

    @ApiModelProperty("主键")
    private Integer id;
}
