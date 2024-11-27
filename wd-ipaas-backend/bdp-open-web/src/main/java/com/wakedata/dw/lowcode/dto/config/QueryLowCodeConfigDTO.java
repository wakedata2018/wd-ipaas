package com.wakedata.dw.lowcode.dto.config;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分类信息查询参数
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@ApiModel("分类信息查询参数")
public class QueryLowCodeConfigDTO extends PageQuery {

    @ApiModelProperty("类型")
    private String type;

}
