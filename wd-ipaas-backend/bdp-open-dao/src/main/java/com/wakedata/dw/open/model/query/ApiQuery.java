package com.wakedata.dw.open.model.query;

import com.wakedata.dw.open.model.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author WangChenSheng
 * @descriptor api列表查询条件
 * @title ApiQuery
 * @date 2022/8/5 11:15
 */
@Getter
@Setter
@ToString
@ApiModel("api列表查询条件")
public class ApiQuery extends PageQuery {

    @ApiModelProperty("租户id")
    private Long lesseeId;

    @ApiModelProperty("API名称")
    private String apiName;

    @ApiModelProperty("-1全部 0代表自定义sql,1代表普通的单表API，2注册HTTP API,3服务编排")
    private Integer apiType;

    @ApiModelProperty("发布状态 -1：全部 0：未发布，1：已发布")
    private Integer dataAssetPublishStatus;

    @ApiModelProperty("申请状态 -1：全部 0：未申请，1：申请中，2：申请成功，3：申请失败")
    private Integer apiApplyStatus;

    @ApiModelProperty("接口分组id,不传默认查全部")
    private Integer apiGroupId;

    @ApiModelProperty("api Path：可模糊查询")
    private String dataAssetApiMethod;

    @ApiModelProperty("发布时间-区间开始时间")
    private String startPublishTime;

    @ApiModelProperty("发布时间-区间结束时间")
    private String endPublishTime;

    @ApiModelProperty("是否为管理员,上下文获取")
    private boolean isPlatformAdmin;

    @ApiModelProperty("api是否公开")
    private Integer secret;


}
