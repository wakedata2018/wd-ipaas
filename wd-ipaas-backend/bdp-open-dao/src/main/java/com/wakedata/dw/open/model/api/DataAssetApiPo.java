package com.wakedata.dw.open.model.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.model.BaseLesseePo;
import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author tanzhi
 * @title DataAssetApiPo
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
@Data
@Table(name = "dw_open_api")
@ApiModel(value = "接口管理", description = "API主表信息")
public class DataAssetApiPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_asset_api_id")
    private Integer dataAssetApiId;

    @ApiModelProperty("资产表名称")
    @Column(name = "data_asset_name")
    private String dataAssetName;

    @ApiModelProperty("数据资产描述")
    @Column(name = "data_asset_description")
    private String dataAssetDescription;

    @ApiModelProperty("负责人")
    @Column(name = "in_charge")
    private String inCharge;

    @ApiModelProperty("发布状态")
    @Column(name = "data_asset_publish_status")
    private DataAssetPublishStatusEnum dataAssetPublishStatus;

    @ApiModelProperty("发布时间")
    @Column(name = "publish_time")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    @ApiModelProperty("api路径")
    @Column(name = "data_asset_api_method")
    private String dataAssetApiMethod;

    @ApiModelProperty("数据源id")
    @Column(name = "datasource_config_id")
    private Integer dataSourceId;

    @ApiModelProperty("API名称")
    @Column(name = "api_name")
    private String apiName;

    @ApiModelProperty("更新频率")
    @Column(name = "update_frequency")
    private DataAssetEnums.UpdateFrequency updateFrequency;

    @ApiModelProperty("请求协议，1-HTTP，2-HTTPS")
    @Column(name = "protocol")
    private DataAssetEnums.ReqProtocol protocol;

    @ApiModelProperty("加密方式")
    @Column(name = "secret")
    private DataAssetEnums.PublicEnums secret;

    @ApiModelProperty("api分组名称")
    @Column(name = "api_group_id")
    private Integer apiGroupId;

    @ApiModelProperty("API描述")
    @Column(name = "api_description")
    private String apiDescription;

    @ApiModelProperty("返回值格式")
    @Column(name = "response_content_type")
    private DataAssetEnums.ResContentType responseContentType;

    @ApiModelProperty("请求方式，GET，POST")
    @Column(name = "req_method")
    private DataAssetEnums.ReqMethod reqMethod;

    @ApiModelProperty("发布人")
    @Column(name = "publisher")
    private String publisher;

    @ApiModelProperty("api类型")
    @Column(name = "api_type")
    private DataAssetEnums.DataApiType apiType;

    @ApiModelProperty("api的操作类型：0:增加;1:删除，2:修改，3:查看")
    @Column(name = "operation_type")
    private DataAssetEnums.DataApiOperationType operationType;

    @ApiModelProperty("自定sql类型api的SQL语句")
    @Column(name = "api_sql")
    private String apiSql;

    @ApiModelProperty("图标路径")
    @Column(name = "icon_url")
    private String iconUrl;

    @ApiModelProperty("0:mq和kafka接收地址;1:http接收地址 3，默认之前的逻辑")
    @Column(name = "is_http_subscriber")
    private Integer isHttpSubscriber;

    /**
     * 事件中心DW_OPEN_EVENT_SUBSCRIBE_ADDRESS表对应的id
     */
    @ApiModelProperty("事件中心订阅地址id")
    @Column(name = "event_center_id")
    private String eventCenterId;

    /**
     * 响应参数映射规则 0：不开启
     */
    @ApiModelProperty("响应参数映射规则")
    @Column(name = "resp_mapping_rule")
    private Integer respMappingRule;

    @Transient
    private String dataAssetApiUri;

    @Transient
    private ApprovalStatusEnum approvalStatus;

    /**
     * 外部API
     */
    @Transient
    private AbstractApiAttr apiAttr;

    /**
     * 外部API,一对多
     */
    @Transient
    private List<ApiRuleAttr> apiAttrs;

    @Transient
    private String accessAppId;

    @Transient
    private String accessAppName;

    @Transient
    private ApiTagInfo apiTagInfo;

    @Transient
    @ApiModelProperty("接口分组名称")
    private String apiGroupName;
}
