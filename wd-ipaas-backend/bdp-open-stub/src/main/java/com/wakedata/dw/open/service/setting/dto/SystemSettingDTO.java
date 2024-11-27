package com.wakedata.dw.open.service.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月22日 11:29:39
 */
@Data
@ApiModel("系统设置信息")
public class SystemSettingDTO {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("系统信息(可拓展)：一行数据记录一种系统设置")
    private SystemDetailInfo systemDetailInfo;

    @ApiModelProperty("系统信息种类(唯一)：1 名称和logo")
    private Integer infoType;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
