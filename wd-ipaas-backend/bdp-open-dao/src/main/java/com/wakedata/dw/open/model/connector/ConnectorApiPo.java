package com.wakedata.dw.open.model.connector;

import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author zhengqinghui@wakedata.com
 * date 2022/11/16 20:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "dw_open_connector_api")
@ApiModel(value = "第三方api信息表")
public class ConnectorApiPo extends BaseLesseePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("平台id")
    @Column(name = "connector_id")
    private Long connectorId;

    @ApiModelProperty(value = "接口分组id")
    @Column(name = "api_group_id")
    private Long apiGroupId;

    @ApiModelProperty(value = "api名称")
    @Column(name = "api_name")
    private String apiName;

    @ApiModelProperty(value = "api地址")
    @Column(name = "api_method")
    private String apiMethod;

    @ApiModelProperty(value = "请求方式")
    @Column(name = "req_method")
    private DataAssetEnums.ReqMethod reqMethod;

    @ApiModelProperty(value = "接口类型")
    @Column(name = "api_type")
    private DataAssetEnums.DataApiType apiType;

    @ApiModelProperty(value = "api上线状态")
    @Column(name = "enable_status")
    private DataAssetEnums.DataAccessPublishStatus enableStatus;

    @ApiModelProperty(value = "api描述")
    @Column(name = "api_description")
    private String apiDescription;

    @ApiModelProperty(value = "创建人")
    @Column(name = "create_by")
    private String createBy;

    @ApiModelProperty(value = "更新人")
    @Column(name = "update_by")
    private String updateBy;

    @Transient
    @ApiModelProperty(value = "连接器名称")
    private String connectorName;

    @Transient
    @ApiModelProperty(value = "连接器分组名称")
    private String apiGroupName;
}
