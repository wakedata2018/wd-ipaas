package com.wakedata.dw.open.model.warn;

import com.wakedata.dw.open.model.BasePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author wq
 * @title ApiWarnPo
 * @date 2020/9/21 11:37
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "DW_OPEN_API_WARN")
public class ApiWarnPo extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty("告警名称")
    @Column(name = "WARN_NAME")
    private String name;
    @ApiModelProperty("告警描述")
    @Column(name = "WARN_DESC")
    private String warnDesc;
    @ApiModelProperty("访问超过多少秒")
    @Column(name = "RULE_SECOND")
    private Integer ruleSecond;
    @ApiModelProperty("访问错误超过多少次")
    @Column(name = "RULE_ERROR_TIMES")
    private Integer ruleErrorTimes;
    @ApiModelProperty("状态")
    @Column(name = "STATUS")
    private Boolean status;
    @ApiModelProperty("邮件")
    @Column(name = "EMAIL")
    private String email;
    @ApiModelProperty("电话")
    @Column(name = "PHONE")
    private String phone;
    @ApiModelProperty("apiId")
    @Column(name = "data_asset_api_id")
    private Integer apiId;
}
