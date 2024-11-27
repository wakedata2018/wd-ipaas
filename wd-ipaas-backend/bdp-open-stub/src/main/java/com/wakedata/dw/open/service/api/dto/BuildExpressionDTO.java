package com.wakedata.dw.open.service.api.dto;

import com.wakedata.dw.open.model.api.ApiConditionPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wanghu@wakedata.com
 * @title 构建表达式的请求参数
 * @date 2021/12/14
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("构建表达式的请求参数")
public class BuildExpressionDTO {

    /**
     * 选择的列
     */
    @ApiModelProperty("选择请求参数列")
    private ApiConditionPo reqApiCondition;

    /**
     * 相关的列（是start节点时要传）
     */
    @ApiModelProperty("相关联的列（start节点时要传）")
    private ApiConditionPo bindApiCondition;

    /**
     * 选择返回参数列
     */
    @ApiModelProperty("选择返回参数列")
    private ApiRespParamDTO apiRespParam;

    /**
     * 参数信息树
     */
    @ApiModelProperty("参数信息树")
    List<ApiRespParamDTO> apiRespParamDTOS;

    /**
     * 请求头参数
     */
    @ApiModelProperty("请求头参数")
    private String head;

    /**
     * 选择取值的节点名称
     */
    @NotBlank(message = "nodeName不能为空")
    @ApiModelProperty("选择取值的节点名称，如果是start节点，传值start")
    private String nodeName;
}
