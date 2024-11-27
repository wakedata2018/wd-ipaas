package com.wakedata.dw.open.model.api.flow.operator.api;

import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.flow.AbstractComponent;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangXueJun
 * @title ApiComponent
 * @date 2021/3/22 11:28
 * @projectName dw-open
 * @description
 */
@Data
public class ApiComponent extends AbstractComponent {

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
     * 算子响应参数模板
     * 存储算子展示的响应参数信息，以JSONSchema格式存储
     */
    private List<ApiRespParamDTO> responseParams;

    private PublicKind publicKind = PublicKind.self;
}
