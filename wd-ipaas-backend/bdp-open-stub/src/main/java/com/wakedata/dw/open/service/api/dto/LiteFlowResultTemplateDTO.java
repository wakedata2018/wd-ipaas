package com.wakedata.dw.open.service.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 流程编排返回值模板DTO
 * @author 佟蕊
 */
@Getter
@Setter
@ToString
@ApiModel("流程编排返回值模板DTO")
public class LiteFlowResultTemplateDTO implements Serializable {

    /**
     * 接口id
     */
    @ApiModelProperty("接口Id")
    private Integer dataAssetApiId;

    /**
     * 参数信息树
     */
    @ApiModelProperty("参数信息树")
    List<ApiRespParamDTO> apiRespParamDTOS;
}
