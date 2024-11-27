package com.wakedata.dw.open.model.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/10/8 15:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "UserActionPageQuery", description = "操作日志查询条件")
public class UserActionPageQuery extends PageQuery{

    /**
     * 页面资源标识（所属模块）
     */
    @ApiModelProperty(value = "页面资源标识（所属模块）")
    private String pageResource;

    /**
     * 行为用户标识(当前操作的用户)
     */
    @ApiModelProperty(value = "行为用户标识(当前操作的用户)")
    private String actionUser;

    /**
     * 租户Id
     */
    @ApiModelProperty(value = "租户Id")
    private Long lesseeId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
