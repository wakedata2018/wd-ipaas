package com.wakedata.dw.open.service.lessee.dto;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luomeng
 * @date 2022/8/2 17:32
 */
@Data
@ApiModel("ipaas账号查询条件")
public class OpenAccountQuery extends PageQuery {

    @ApiModelProperty("用户账号标识")
    private String userIdentification;
    @ApiModelProperty("手机号")
    private String phone ;
    @ApiModelProperty("关联租户")
    private String tenantName ;
}
