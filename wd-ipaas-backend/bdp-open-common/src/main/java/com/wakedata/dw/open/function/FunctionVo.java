package com.wakedata.dw.open.function;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author luomeng
 * @date 2022/8/18 20:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "函数说明")
public class FunctionVo implements Serializable {
    /**
     * 函数名
     */
    @ApiModelProperty("函数方法分类")
    private String method;

    @ApiModelProperty("函数分类说明")
    private String description;

    @ApiModelProperty("具体的函数信息")
    private List<FunctionEnumSet.FunctionEnumVo> list;
}
