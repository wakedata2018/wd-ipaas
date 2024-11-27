package com.wakedata.dw.open.service.approval.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 审批管理列表数据
 *
 * @author wujunqiang
 * @since 2022/8/3 7:05 PM
 */
@Data
@ApiModel("审批管理列表数据")
public class ApprovalRecordVO {

    @ApiModelProperty("审批记录id")
    private Integer approvalId;

    @ApiModelProperty("API名称")
    private String apiName;

    @ApiModelProperty("API Path")
    private String apiPath;

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("关联租户名称")
    private String lesseeName;

    @ApiModelProperty("申请时间")
    private Date submitTime;

    @ApiModelProperty("申请人")
    private String submitter;

    @ApiModelProperty("审批意见")
    private String approvalMessage;

    @ApiModelProperty("审批状态")
    private Integer approvalStatus;

}
