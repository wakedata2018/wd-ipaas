package com.wakedata.dw.lowcode.dto.block;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 区块列表请求参数
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/24
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("区块列表请求参数")
public class PageLowCodeBlockDTO extends PageQuery {

    @ApiModelProperty("区块名称")
    private String name;

}
