package com.wakedata.dw.open.model.api.dto;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wanghu@wakedata.com
 * @title
 * @date 2021/12/1
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("获取应用没有授权的api请求参数")
public class NotAuthApiReqParam extends PageQuery {

    @NotNull(message = "应用id不能为空")
    @ApiModelProperty("应用id")
    private Integer accessAppId;

    @ApiModelProperty("分组ID")
    private Integer apiGroupId;

    @ApiModelProperty("api名称")
    private String apiName;

    @ApiModelProperty("是否为我创建的")
    private Boolean createdForSelf;

    @ApiModelProperty(value = "创建人", hidden = true)
    private String inCharge;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Integer lesseeId;

    @ApiModelProperty(value = "分组id列表", hidden = true)
    private List<Integer> apiGroupIds;

}
