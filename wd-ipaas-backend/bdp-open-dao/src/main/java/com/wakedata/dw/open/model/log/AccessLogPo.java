package com.wakedata.dw.open.model.log;

import com.wakedata.dw.open.model.BaseLesseePo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author tanzhi
 * @title AccessLogPo
 * @projectName bdp-open
 * @date 2019/8/16 10:16
 * @description
 */

@Table(name = "DW_OPEN_ACCESS_LOG")
@Data
@ApiModel("应用访问日志")
public class AccessLogPo extends BaseLesseePo {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @ApiModelProperty("id")
    private Integer id;
    /**
     * 访问者appId
     */
    @Column(name = "data_access_app_id")
    @ApiModelProperty("访问者appId")
    private Integer appId;
    /**
     * 访问者序列号
     */
    @Column(name = "SEQ_NO")
    private String seqNo;
    /**
     * 访问者资源路径
     */
    @Column(name = "data_asset_api_id")
    private Integer dataAssetApiId;
    /**
     * 版本
     */
    @Column(name = "VERSION")
    private String version;
    /**
     * 返回结果code
     */
    @Column(name = "RESULT_CODE")
    @ApiModelProperty("返回结果code")
    private Integer resultCode;
    /**
     * 返回记录行数
     */
    @Column(name = "RESULT_ROW")
    private Integer resultRow;
    /**
     * 程序处理所用时间
     */
    @Column(name = "ELAPSED")
    @ApiModelProperty("api执行时长")
    private Integer elapsed;

    @Column(name = "IP")
    @ApiModelProperty("IP")
    private String ip;

    @Column(name = "result_description")
    @ApiModelProperty("结果描述")
    private String resultDescription;

    @Column(name = "api_invoke_source_type")
    private Integer apiInvokeSourceType;

    @Column(name = "api_invoke_source")
    @ApiModelProperty("访问来源")
    private String apiInvokeSource;

    /**
     * 接口地址
     */
    @Column(name = "data_asset_method")
    @ApiModelProperty("接口地址")
    private String dataAssetMethod;

    /**
     * 应用名称
     */
    @Column(name = "data_access_name")
    @ApiModelProperty("应用名称")
    private String dataAccessName;
    /**
     * 接口名称
     */
    @Column(name = "data_asset_name")
    @ApiModelProperty("接口名称")
    private String dataAssetName;

    /**
     * 接口分组
     */
    @Column(name = "data_asset_group")
    @ApiModelProperty("接口分组")
    private String dataAssetGroup;

    /**
     * 接口入参
     */
    @Column(name = "data_request_param")
    @ApiModelProperty("接口入参")
    private String dataRequestParam;

    /**
     * 接口响应
     */
    @Column(name = "data_response_param")
    @ApiModelProperty("接口响应")
    private String dataResponseParam;


}