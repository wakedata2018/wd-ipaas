package com.wakedata.openapi.sdk.generator.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/9/1 14:46
 */
@Getter
@Setter
@ToString
//@ApiModel("API管理列表数据")
public class DataAssetApiDetailDTO implements Serializable {

//    @ApiModelProperty("租户id")
    private Long lesseeId;

//    @ApiModelProperty("自增id -- apiId")
    private String dataAssetApiId;

//    @ApiModelProperty("资产表名称(应用名称)")
    private String dataAssetName;

//    @ApiModelProperty("数据资产描述")
    private String dataAssetDescription;

//    @ApiModelProperty("负责人")
    private String inCharge;

//    @ApiModelProperty("数据资产状态")
//    private DataAssetPublishStatusEnum dataAssetPublishStatus;

//    @ApiModelProperty("发布时间")
    private Date publishTime;

//    @ApiModelProperty("数据资产API名称")
    private String dataAssetApiMethod;

//    @ApiModelProperty("更新时间")
    private Date updateTime;

//    @ApiModelProperty("创建时间")
    private Date createTime;

//    @ApiModelProperty("图标路径")
    private String iconUrl;

//    @ApiModelProperty("API名称")
    private String apiName;

//    @ApiModelProperty("更新频率")
//    private DataAssetEnums.UpdateFrequency updateFrequency;

//    @ApiModelProperty("请求协议")
//    private DataAssetEnums.ReqProtocol protocol;

//    @ApiModelProperty("加密方式")
//    private DataAssetEnums.PublicEnums secret;

//    @ApiModelProperty("接口分组id")
    private Integer apiGroupId;

//    @ApiModelProperty("接口分组名称")
    private String apiGroupName;

//    @ApiModelProperty("API描述")
    private String apiDescription;

//    @ApiModelProperty("返回格式")
//    private DataAssetEnums.ResContentType responseContentType;

//    @ApiModelProperty("请求方法")
    private DataAssetEnums.ReqMethod reqMethod;

//    @ApiModelProperty("数据源配置ID")
    private Integer datasourceConfigId;

//    @ApiModelProperty("发布人")
    private String publisher;

//    @ApiModelProperty("API类型;0代表自定义sql,1代表普通的单表API，2注册HTTP API")
    private DataAssetEnums.DataApiType apiType;

//    @ApiModelProperty("自定义api的SQL")
    private String apiSql;

//    @ApiModelProperty("api的操作类型：0:增加;1:删除，2:修改，3:查看")
    private DataAssetEnums.DataApiOperationType operationType;

//    @ApiModelProperty("0:mq和kafka接收地址;1:http接收地址 3，默认之前的逻辑")
    private Integer isHttpSubscriber;

//    @ApiModelProperty("事件中心dw_open_event_SUBSCRIBE_ADDRESS表http类型对应的id")
    private String eventCenterId;

//    @ApiModelProperty("api审批状态")
//    private ApprovalStatusEnum approvalStatus;

//    @ApiModelProperty("api申请状态")
//    private ApiApplyStatusEnum applyStatus;

//    @ApiModelProperty("请求参数")
    private List<ApiConditionDTO> apiConditionPoList;

//    @ApiModelProperty("响应参数")
    private List<ApiRespParamDTO> apiRespParamList;
}
