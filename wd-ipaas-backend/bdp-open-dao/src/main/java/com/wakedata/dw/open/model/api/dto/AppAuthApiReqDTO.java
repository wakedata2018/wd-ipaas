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
 * @title 授权api请求参数
 * @date 2021/11/26
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@ApiModel("授权api请求参数")
public class AppAuthApiReqDTO extends PageQuery {

    @ApiModelProperty("应用id")
    @NotNull(message = "应用id不能为空")
    private Integer appId;

    @ApiModelProperty("分组ID")
    @NotNull(message = "分组id不能为空")
    private Integer apiGroupId;

    @ApiModelProperty("api名称")
    private String apiName;

    /**
     * 分组id列表
     */
    private List<Integer> apiGroupIds;

}
