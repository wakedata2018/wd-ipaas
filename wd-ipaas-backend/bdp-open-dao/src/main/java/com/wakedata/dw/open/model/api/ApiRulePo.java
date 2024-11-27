package com.wakedata.dw.open.model.api;

import com.wakedata.dw.open.model.BasePo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author wq
 * @title ApiRulePo
 * @date 2020/10/19 10:46
 * @projectName dw-open
 * @description
 */
@Data
@Table(name = "DW_OPEN_API_RULE")
public class ApiRulePo extends BasePo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty("规则名称")
    @Column(name = "rule_name")
    private String ruleName;
    @ApiModelProperty("规则描述")
    @Column(name = "rule_desc")
    private String ruleDesc;
    @ApiModelProperty("data_asset_api_id")
    @Column(name = "data_asset_api_id")
    private Integer dataAssetApiId;
    @ApiModelProperty("日限制")
    @Column(name = "day_limit")
    private Integer dayLimit;
    @ApiModelProperty("月限制")
    @Column(name = "month_limit")
    private Integer monthLimit;
    @ApiModelProperty("总限制")
    @Column(name = "total_limit")
    private Integer totalLimit;
    @ApiModelProperty("QPS")
    @Column(name = "qps")
    private Integer qps;
    @ApiModelProperty("TTL")
    @Column(name = "ttl")
    private Integer ttl;
}