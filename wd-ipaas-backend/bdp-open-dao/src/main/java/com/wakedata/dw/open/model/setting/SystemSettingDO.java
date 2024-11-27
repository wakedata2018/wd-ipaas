package com.wakedata.dw.open.model.setting;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月22日 11:43:09
 */
@EqualsAndHashCode(callSuper = true)
@Table(name = "DW_OPEN_SETTING")
@Data
@ApiModel("系统设置")
public class SystemSettingDO extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("id")
    private Integer id;

    @Column(name = "setting_content")
    @ApiModelProperty("系统设置")
    private String settingContent;

    @Column(name = "info_type")
    @ApiModelProperty("系统设置种类")
    private Integer infoType;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @Column(name = "update_by")
    private String updateBy;
}
