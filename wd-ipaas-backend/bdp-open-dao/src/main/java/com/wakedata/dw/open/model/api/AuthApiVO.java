package com.wakedata.dw.open.model.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * API授权列表VO
 * @author 佟蕊
 */
@Data
@ApiModel("API授权列表VO")
public class AuthApiVO {
    /**
     * 审批表自增id
     */
    @ApiModelProperty("审批表自增id")
    private Integer approvalId;
    /**
     * API id
     */
    @ApiModelProperty("API id")
    private String dataAssetApiId;

    /**
     * 应用id
     */
    @ApiModelProperty("应用id")
    private String dataAccessAppId;

    /**
     * API名称
     */
    @ApiModelProperty("api名称")
    private String dataAssetName;
    /**
     * API描述
     */
    @ApiModelProperty("api描述")
    private String dataAssetDescription;
    /**
     * API组别id
     */
    @ApiModelProperty("api组别id")
    private String apiGroupId;
    /**
     * API组别名称
     */
    @ApiModelProperty("api组别名称")
    private String apiGroupName;
    /**
     * 应用授权状态，0：未授权，1：已授权
     */
    @ApiModelProperty("应用授权状态，0：未授权，1：已授权")
    private Integer appAuthStatus;

}
