package com.wakedata.dw.open.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wujunqiang
 * @since 2022/9/14 17:32
 */
@Data
@ApiModel(value = "函数表达式校验入参")
public class FunctionExpressCheckReq {

    /**
     * 函数表达式
     */
    @ApiModelProperty("函数表达式")
    private String functionExpress;

}
