package com.wakedata.dw.open.service.vo;

import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/9/1 15:23
 */
@Data
@ApiModel(value = "api详情Vo", description = "api详情Vo")
public class DocumentApiDetailVo {

    @ApiModelProperty(value = "订阅记录vo")
    private SubscribeRecordVo subscribeRecord;
    @ApiModelProperty(value = "api基础信息vo")
    private DocumentDataAssetApiVo dataAssetApi;
    @ApiModelProperty(value = "请求参数")
    private List<DocumentApiConditionVo> parameters;
    @ApiModelProperty(value = "结果参数")
    private List<DocumentApiConditionVo> results;

    /**
     * API响应参数列表
     */
    @ApiModelProperty(value = "API响应参数列表")
    private List<ApiRespParamDTO> responseParams;

    /**
     * 编排响应结果参数信息树
     */
    @ApiModelProperty("编排响应结果参数信息树")
    private List<ApiRespParamDTO> resutRespParamDTOS;

    /**
     * 请求参数示例（包括xml格式），API文档中使用字段
     */
    @ApiModelProperty("请求参数示例（xml格式），API文档中使用字段")
    private Map<String, String> parametersExample;

    /**
     * 响应参数示例（包括xml格式），API文档中使用字段
     */
    @ApiModelProperty("请求参数示例（xml格式），API文档中使用字段")
    private Map<String, String> responseParamsExample;

    /**
     * 响应参数示例（包括xml格式），API文档中使用字段
     */
    @ApiModelProperty("异常示例（xml格式），API文档中使用字段")
    private Map<String, String> errorExample;


    /**
     * 域名，API文档使用字段
     */
    @ApiModelProperty("域名，API文档使用字段")
    private String domainName;


}
