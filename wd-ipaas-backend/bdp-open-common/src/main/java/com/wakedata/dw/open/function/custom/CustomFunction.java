package com.wakedata.dw.open.function.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 自定义函数信息
 * @author luomeng
 * @date 2022/10/31 15:11
 */
@Data
@NoArgsConstructor
@ApiModel("自定义函数-执行")
public class CustomFunction {
    /**
     * 函数名称
     */
    @ApiModelProperty("函数名称")
    private String name;

    /**
     * 租户ID
     */
    private Long lesseeId;

    /**
     * 函数语言
     */
    private String language;
    /**
     * 函数描述
     */
    private String desc;
    /**
     * 函数入参
     */
    @ApiModelProperty("函数入参")
    private List<FunctionParam> paramList;
    /**
     * 函数返回类型
     */
    private String returnType;
    /**
     * 函数代码
     */
    private String code;

    /**
     * 函数入参
     */
    @Data
    @ApiModel("函数入参")
    public static class FunctionParam {
        /**
         * 参数名称
         */
        @ApiModelProperty("参数名称")
        private String name;
        /**
         * 参数类型
         */
        @ApiModelProperty("参数类型")
        private String type;
        /**
         * 参数值
         */
        @ApiModelProperty("参数值")
        private Object value;
        /**
         * 描述
         */
        @ApiModelProperty("参数描述")
        private String desc;

        public FunctionParam() {
        }

        public FunctionParam(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

}
