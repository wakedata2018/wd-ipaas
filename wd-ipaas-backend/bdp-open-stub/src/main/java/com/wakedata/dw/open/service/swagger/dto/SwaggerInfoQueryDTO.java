package com.wakedata.dw.open.service.swagger.dto;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Swagger分页查询条件DTO
 *
 * @author wujunqiang
 * @since 2022/8/24 10:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("Swagger分页查询条件DTO")
public class SwaggerInfoQueryDTO extends PageQuery {

    /**
     * Swagger名称
     */
    @ApiModelProperty("Swagger名称")
    private String swaggerName;

    /**
     * 接口分类ID
     */
    @ApiModelProperty("接口分类ID")
    private Integer apiGroupId;

    /**
     * 更新时间开始节点
     */
    @ApiModelProperty("更新时间开始节点")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    /**
     * 更新时间结束节点
     */
    @ApiModelProperty("更新时间结束节点")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", hidden = true)
    private Long lesseeId;

}
