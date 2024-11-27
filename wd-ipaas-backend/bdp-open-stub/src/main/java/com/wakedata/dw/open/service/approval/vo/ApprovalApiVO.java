package com.wakedata.dw.open.service.approval.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 审核API请求参数
 *
 * @author wujunqiang
 * @since 2022/8/3 8:20 PM
 */
@Data
public class ApprovalApiVO {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Integer approvalId;

    @ApiModelProperty("审核结果，true：通过、false：不通过")
    @NotNull(message = "审核结果不能为空")
    private Boolean auditResults;

    @ApiModelProperty("审批意见")
    private String approvalComments;

}
