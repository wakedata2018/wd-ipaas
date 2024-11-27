package com.wakedata.dw.open.service.vo;

import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tanzhi
 * @title ApiPublishVo
 * @date 2019/11/28 19:45
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class ApiDetailVo {
    private SubscribeRecordVo subscribeRecord;
    /**
     * api基本信息
     */
    private DataAssetApiPo dataAssetApi;
    /**
     * 请求参数，带结构
     * GET请求方式-平铺（多少个参数数据库就多少条记录），POST请求方式-请求体格式（数据库只有一条记录，使用JSONSchema格式存储）
     */
    private List<ApiConditionPo> parameters;
    /**
     * 请求参数，不带结构
     * 平铺，主要用于字段鉴权
     */
    private List<ApiConditionPo> results;
    /**
     * 步骤英文名
     */
    private String operatorName;

    /**
     * API响应参数列表
     */
    private List<ApiRespParamDTO> responseParams;

    /**
     * 编排响应结果参数信息树
     */
    @ApiModelProperty("编排响应结果参数信息树")
    private List<ApiRespParamDTO> resutRespParamDTOS;

    /**
     * 请求参数示例（xml格式），API文档中使用字段
     */
    private Map<String, String> parametersExample;

    /**
     * 域名，API文档使用字段
     */
    private String domainName;

    public ApiDetailVo() {
    }

}
