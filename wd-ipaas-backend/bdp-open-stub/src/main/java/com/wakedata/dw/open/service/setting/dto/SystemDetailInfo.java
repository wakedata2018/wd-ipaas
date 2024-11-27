package com.wakedata.dw.open.service.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月22日 11:30:35
 */
@Data
@ApiModel("系统信息")
public class SystemDetailInfo implements Serializable {

    @ApiModelProperty("系统名称")
    private String systemName;

    @ApiModelProperty("系统logo(中)")
    private String logoUrl;

    @ApiModelProperty("系统logo(小)")
    private String miniLogoUrl;

}
