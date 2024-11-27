package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/16 19:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector")
@ApiModel(value = "平台信息表")
public class ConnectorPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "平台分类id")
    @Column(name = "group_id")
    private Long groupId;

    @ApiModelProperty(value = "平台唯一标识")
    @Column(name = "auth_type")
    private String authType;

    @ApiModelProperty(value = "启用状态")
    @Column(name = "enable_status")
    private Integer enableStatus;

    @ApiModelProperty(value = "名称")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(value = "版本")
    @Column(name = "version")
    private String version;

    @ApiModelProperty(value = "开发者")
    @Column(name = "developer")
    private String developer;

    @ApiModelProperty(value = "联系电话")
    @Column(name = "phone")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Column(name = "email")
    private String email;

    @ApiModelProperty(value = "官网")
    @Column(name = "website")
    private String website;

    @ApiModelProperty(value = "帮助文档")
    @Column(name = "help_document")
    private String helpDocument;

    @ApiModelProperty(value = "隐私协议")
    @Column(name = "privacy_agreement")
    private String privacyAgreement;

    @ApiModelProperty(value = "使用协议")
    @Column(name = "usage_agreement")
    private String usageAgreement;

    @ApiModelProperty(value = "平台介绍")
    @Column(name = "platform_introduction")
    private String platformIntroduction;

    @ApiModelProperty(value = "删除标记")
    @Column(name = "is_delete")
    private Integer isDelete;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @Column(name = "update_by")
    private String updateBy;

}
