package com.wakedata.dw.open.service.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author WangChenSheng
 * date 2022/8/24 10:41
 */
@Data
public class SwaggerApiDTO implements Serializable {

    @ApiModelProperty("swaggerId")
    @NotNull(message = "swaggerId不能为空")
    private Integer swaggerId;

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("ids")
    private List<Integer> ids;

    @ApiModelProperty("api名称")
    private String apiName;

    @ApiModelProperty("api类型")
    private Integer apiType;

    @ApiModelProperty("apiPath")
    private String dataAssetApiMethod;

    @ApiModelProperty("api描述")
    private String apiDescription;

    @ApiModelProperty("解析结果：1 成功 2 失败")
    private Integer parseStatus;

    @ApiModelProperty("错误详情")
    private String errorDetail;

    @ApiModelProperty("swagger基础信息")
    private String apiInfo;

    @ApiModelProperty("接口分组id")
    private Integer apiGroupId;

    @ApiModelProperty("导入临时表状态 0 未导入 1 导入成功 2 导入失败")
    private Integer importStatus;

    @ApiModelProperty("负责人")
    private String inCharge;

    @ApiModelProperty("jsonSchema")
    private String jsonSchema;

}
