package com.wakedata.dw.open.service.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 流程编排所有算子的参数模板
 * @author 佟蕊
 */
@Getter
@Setter
@ToString
@ApiModel("流程编排所有算子的参数模板")
public class LiteFlowAllOperatorTemplateDTO implements Serializable {

    /**
     * 节点id
     */
    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点所属类名")
    private String nodeClass;

    @ApiModelProperty("节点描述")
    private String nodeDesc;

    /**
     * 接口分组名称
     */
    @ApiModelProperty("接口分组名称")
    private String apiGroupName;

    /**
     * 接口名称
     */
    @ApiModelProperty("接口名称")
    private String apiName;

    /**
     * 接口描述
     */
    @ApiModelProperty("接口描述")
    private String apiDescription;

    /**
     * 接口文档链接
     */
    @ApiModelProperty("接口文档链接")
    private String apiDocUrl;

    /**
     * 参数信息树
     */
    @ApiModelProperty("参数信息树")
    private List<ApiRespParamDTO> apiRespParamDTOS;
}
