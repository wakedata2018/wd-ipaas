package com.wakedata.dw.lowcode.dto.category;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 配置信息查询参数
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("配置信息查询参数")
public class QueryLowCodePageCategoryDTO extends PageQuery {

    @ApiModelProperty("应用id")
    @NotBlank(message = "应用id不能为空")
    private Integer appId;

    /**
     * 页面名称
     */
    @ApiModelProperty("名称")
    private String name;

}
