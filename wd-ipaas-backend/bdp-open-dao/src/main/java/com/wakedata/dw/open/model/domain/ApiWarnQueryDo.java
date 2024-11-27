package com.wakedata.dw.open.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wq
 * @title ApiWarnQueryDo
 * @date 2020/10/13 11:39
 * @projectName dw-open
 * @description
 */
@Data
public class ApiWarnQueryDo {
    @ApiModelProperty("告警名称")
    private String warnName;
    @ApiModelProperty("api主题")
    private Integer apiGroup;
    @ApiModelProperty("api名称")
    private String apiName;
    @ApiModelProperty("电话号码")
    private String phone;
    @ApiModelProperty("邮件地址")
    private String email;
    @ApiModelProperty("状态")
    private Boolean status;

    @JsonIgnore
    private Long lesseeId;

}
