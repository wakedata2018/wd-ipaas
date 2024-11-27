package com.wakedata.dw.open.service.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.common.core.hashids.annotation.HashidsConvert;
import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.model.BaseLesseePo;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.ApiTagInfo;
import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/9/1 15:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "API基础信息Vo", description = "API基础信息Vo")
public class DocumentDataAssetApiVo extends BaseLesseePo {

    @ApiModelProperty("api自增Id")
    @HashidsConvert
    private Integer dataAssetApiId;

    @ApiModelProperty("数据资产名称")
    private String dataAssetName;

    @ApiModelProperty("数据资产描述")
    private String dataAssetDescription;

    @ApiModelProperty("负责人(标识1;标识2,标识3)")
    private String inCharge;

    @ApiModelProperty("发布状态（数据资产状态）")
    private DataAssetPublishStatusEnum dataAssetPublishStatus;

    @ApiModelProperty("发布时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    @ApiModelProperty("API地址")
    private String dataAssetApiMethod;

    @ApiModelProperty("数据源配置Id")
    private Integer dataSourceId;

    @ApiModelProperty("API名称")
    private String apiName;

    @ApiModelProperty("更新频率")
    private DataAssetEnums.UpdateFrequency updateFrequency;

    @ApiModelProperty("请求协议")
    private DataAssetEnums.ReqProtocol protocol;

    @ApiModelProperty("加密方式")
    private DataAssetEnums.PublicEnums secret;

    @ApiModelProperty("分组Id")
    private Integer apiGroupId;

    @ApiModelProperty("API描述")
    private String apiDescription;

    @ApiModelProperty("返回格式")
    private DataAssetEnums.ResContentType responseContentType;

    @ApiModelProperty("请求方法")
    private DataAssetEnums.ReqMethod reqMethod;

    @ApiModelProperty("发布者")
    private String publisher;

    @ApiModelProperty("API类型;0代表自定义sql,1代表普通的单表API")
    private DataAssetEnums.DataApiType apiType;

    @ApiModelProperty("api的操作类型：0:增加;1:删除，2:修改，3:查看")
    private DataAssetEnums.DataApiOperationType operationType;

    @ApiModelProperty("自定义api的SQL")
    private String apiSql;

    @ApiModelProperty("图标路径")
    private String iconUrl;

    @ApiModelProperty("0:mq和kafka接收地址;1:http接收地址 3，默认之前的逻辑")
    private Integer isHttpSubscriber;

    /**
     * 事件中心DW_OPEN_EVENT_SUBSCRIBE_ADDRESS表对应的id
     */
    @ApiModelProperty("事件中心dw_open_event_SUBSCRIBE_ADDRESS表http类型对应的id")
    private String eventCenterId;

    @ApiModelProperty("资产表名称")
    private String dataAssetApiUri;

    @ApiModelProperty("审批状态")
    private ApprovalStatusEnum approvalStatus;

    /**
     * 外部API
     */
    @ApiModelProperty("外部API")
    private AbstractApiAttr apiAttr;

    /**
     * 外部API,一对多
     */
    @ApiModelProperty("外部API,一对多")
    private List<ApiRuleAttr> apiAttrs;

    private String accessAppId;

    private String accessAppName;

    @ApiModelProperty("API标签信息")
    private ApiTagInfo apiTagInfo;

    @ApiModelProperty("接口分组名称")
    private String apiGroupName;
}
