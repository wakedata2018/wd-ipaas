package com.wakedata.dw.open.service.vo.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@ApiModel("事件配置")
public class EventBaseVo {
    private static final long serialVersionUID = 3401812792877386237L;

    @ApiModelProperty("创建人")
    private String createUser;
    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

}
