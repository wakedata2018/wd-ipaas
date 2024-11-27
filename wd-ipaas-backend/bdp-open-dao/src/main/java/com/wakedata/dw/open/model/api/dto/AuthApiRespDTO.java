package com.wakedata.dw.open.model.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 授权api列表
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/26
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("授权api列表")
public class AuthApiRespDTO {

    @ApiModelProperty("数据资产名称(api名称)")
    private String apiName;

    @ApiModelProperty("数据资产id(主键)")
    private Integer dataAssetApiId;

}
