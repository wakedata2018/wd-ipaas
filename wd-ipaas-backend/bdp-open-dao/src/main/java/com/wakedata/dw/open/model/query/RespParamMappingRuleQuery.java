package com.wakedata.dw.open.model.query;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/3 9:45
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "RespParamMappingRuleQuery", description = "响应参数映射规则查询条件")
public class RespParamMappingRuleQuery extends PageQuery {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("响应体参数映射规则名称")
    private String respParamMappingRuleName;

    @ApiModelProperty("启用状态；0-未启用，1-已启用")
    private Integer status;

    @ApiModelProperty("更新时间-起始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("更新时间-结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
