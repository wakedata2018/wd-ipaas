package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/8/19 15:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("官方文档api列表表查询条件")
public class ApiDocumentQuery extends PageQuery {

    @ApiModelProperty(value = "接口分组id", required = true)
    @NotNull(message = "apiGroupId is not null")
    private Integer apiGroupId;

}
