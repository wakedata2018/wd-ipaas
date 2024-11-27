package com.wakedata.dw.open.model.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wq
 * @title ApiRuleDo
 * @date 2020/10/19 14:28
 * @projectName dw-open
 * @description
 */
@Data
public class ApiRuleDo {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("规则描述")
    private String ruleDesc;
    @ApiModelProperty("api id")
    private Integer dataAssetApiId;
    @ApiModelProperty("api名称")
    private String apiName;
    @ApiModelProperty("日限制")
    private Integer dayLimit;
    @ApiModelProperty("月限制")
    private Integer monthLimit;
    @ApiModelProperty("总限制")
    private Integer totalLimit;
    @ApiModelProperty("每秒最大访问次数")
    private Integer qps;
    @ApiModelProperty("API缓存失效时间")
    private Integer ttl;
    @ApiModelProperty("主题ID")
    private Integer apiGroupId;
    @ApiModelProperty("主题名称")
    private String apiGroupName;
    @ApiModelProperty("api 路径")
    private String apiPath;

}
