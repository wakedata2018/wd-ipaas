package com.wakedata.dw.lowcode.dto.page;

import com.wakedata.dw.lowcode.dto.IdDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wanghu@wakedata.com
 * @title 拷贝页面请求参数
 * @date 2021/12/2
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("拷贝页面请求参数")
public class CopyPageReqDTO extends IdDTO {

    @ApiModelProperty("名称")
    private String name;

}
