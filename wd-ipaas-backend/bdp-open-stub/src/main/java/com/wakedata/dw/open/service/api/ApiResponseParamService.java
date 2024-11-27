package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.model.api.ApiResponseParamPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.BuildExpressionDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowResultTemplateDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;

import java.util.List;

/**
 * API响应参数(DwOpenApiResponseParam)表服务接口
 *
 * @author wanghu
 * @since 2021-12-10 10:16:22
 */
public interface ApiResponseParamService extends BaseDbService<ApiResponseParamPo> {

    /**
     * 根据apiId查询返回参数
     *
     * @param apiId apiId
     * @return 返回参数
     */
    List<ApiRespParamDTO> listByApiId(Integer apiId);

    /**
     * 根据连接器API查询返回参数
     *
     * @param connectorApiId 连接器API ID
     * @return 返回参数
     */
    List<ApiRespParamDTO> getApiRespParamByConnectorId(Long connectorApiId);

    /**
     * 根据apiId查询流程编排结果集
     * @param apiId 接口id
     * @return
     */
    List<ApiRespParamDTO> findLiteflowResult(Integer apiId);

    /**
     * 保存流程编排结果模板
     * @param liteFlowResultTemplateDTO
     * @return
     */
    void saveLiteFlowResult(LiteFlowResultTemplateDTO liteFlowResultTemplateDTO, IpaasUserInfo userInfo);

    /**
     * 转换选择的列为表达式
     *
     * @param dto 入参
     * @param expressionType 表达式类型{@link com.wakedata.dw.open.enums.DataAssetEnums.ExpressionType}
     * @return 表达式
     */
    String convertColumnToExpression(BuildExpressionDTO dto,String expressionType);

    /**
     * 通过api资产id清除
     *
     * @param dataAssetApiId api资产id
     * @return 影响行数
     */
    Integer clearByDataAssertId(Integer dataAssetApiId);

    /**
     * 获取api响应参数
     * @param lesseeId
     * @param dataAssetApiId
     * @return
     */
    List<ApiResponseParamPo> getApiResponseData(Long lesseeId, Integer dataAssetApiId);

}
