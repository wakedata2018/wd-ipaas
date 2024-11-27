package com.wakedata.dw.open.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 批量授权请求参数
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/30
 * @since v1.0.0
 */
@ApiModel("批量授权请求参数")
@Getter
@Setter
@ToString
public class BatchAuthorizeReqParam {

    @ApiModelProperty("应用id")
    @NotNull(message = "应用id不能为空")
    private Integer dataAccessAppId;

    @ApiModelProperty("api访问接口数组")
    @NotEmpty(message = "apiId不能为空")
    private Set<Integer> dataAssetApiId;

    @ApiModelProperty(value = "是否认证", hidden = true)
    private Boolean hasAuth;
}
