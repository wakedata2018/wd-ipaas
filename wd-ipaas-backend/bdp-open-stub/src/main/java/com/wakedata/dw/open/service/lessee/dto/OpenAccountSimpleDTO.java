package com.wakedata.dw.open.service.lessee.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author luomeng
 * @date 2022/8/2 17:23
 */
@Data
@ApiModel("ipaas账号操作对象")
public class OpenAccountSimpleDTO implements Serializable {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id ;
    @ApiModelProperty("新密码")
    private String newPassword;
    @ApiModelProperty("确认密码")
    private String confirmPassword;

}
