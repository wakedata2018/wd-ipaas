package com.wakedata.dw.open.service.connector.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 10:07
 */
@Data
@ApiModel(value = "连接器平台信息DTO", description = "连接器平台信息DTO")
public class ConnectorDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @NotNull(message = "groupId cannot be empty")
    @ApiModelProperty(value = "平台分类id")
    private Long groupId;

    @ApiModelProperty(value = "鉴权类型")
    private String authType;

    @NotNull(message = "enableStatus cannot be empty")
    @ApiModelProperty(value = "启用状态")
    private Integer enableStatus;

    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(value = "名称")
    private String name;

    @NotBlank(message = "version cannot be empty")
    @ApiModelProperty(value = "版本")
    private String version;

    @NotBlank(message = "developer cannot be empty")
    @ApiModelProperty(value = "开发者")
    private String developer;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @NotBlank(message = "website cannot be empty")
    @ApiModelProperty(value = "官网")
    private String website;

    @ApiModelProperty(value = "帮助文档")
    private String helpDocument;

    @ApiModelProperty(value = "隐私协议")
    private String privacyAgreement;

    @ApiModelProperty(value = "使用协议")
    private String usageAgreement;

    @ApiModelProperty(value = "平台介绍")
    private String platformIntroduction;

    @ApiModelProperty(value = "删除标记")
    private Integer isDelete;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "平台下接口数量")
    private Integer apiCount;

    @ApiModelProperty(value = "平台所属分类名称")
    private String groupName;

    @ApiModelProperty(value = "平台环境地址")
    private List<ConnectorEnvironmentAddressDTO> connectorEnvironmentAddressDTOList;

    @ApiModelProperty(value = "平台鉴权字段")
    private List<ConnectorParamsDTO> connectorParamsDTOList;

    /**
     * 目前这个参数只有导出连接器API时用到，所以在文档中隐藏此参数
     */
    @ApiModelProperty(value = "连接器API分组集合", hidden = true)
    private List<ConnectorApiGroupDTO> connectorGroupList;

}
