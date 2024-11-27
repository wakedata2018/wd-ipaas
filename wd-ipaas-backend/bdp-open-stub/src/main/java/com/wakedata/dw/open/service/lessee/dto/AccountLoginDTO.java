package com.wakedata.dw.open.service.lessee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 账号登录
 * @author luomeng
 * @date 2022/8/4 14:08
 */
@ApiModel(value = "账号登录DTO")
@Data
public class AccountLoginDTO implements Serializable {

    @ApiModelProperty(value = "账号")
    @NotEmpty(message = "账号不能为空")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotEmpty(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "图形验证码")
    private String captchaKey;

    @ApiModelProperty(value = "滑动验证码x轴")
    private Integer x;

    @ApiModelProperty(value = "滑动验证码y轴")
    private Integer y;

}
