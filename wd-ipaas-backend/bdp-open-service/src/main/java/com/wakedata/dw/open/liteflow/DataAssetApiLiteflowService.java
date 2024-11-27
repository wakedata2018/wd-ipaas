package com.wakedata.dw.open.liteflow;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.dto.ApiInfoDTO;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowRelation;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;

import java.util.List;

/**
 * api流程编排接口定义
 * @author luomeng
 * @date 2022/12/14 10:42
 */
public interface DataAssetApiLiteflowService {

    /**
     * 获取编排内关联的apiId
     * @param apiFlowAttr
     * @return
     */
    List<Integer> getApiLiteflowRelationApiAndHandle(ApiFlowAttr apiFlowAttr);

    /**
     * 更新算子信息
     * @param subscribeRecordVo
     * @param apiAttr
     * @param inCharge
     */
    void updateInternalApiOperatorIfNecessary(SubscribeRecordVo subscribeRecordVo, ApiFlowAttr apiAttr, String inCharge) ;

    /**
     * 获取编排算子结果参数
     * @param apiFlowAttr
     * @param dataAssetApiId
     * @return
     */
    List<ApiConditionPo> getApiSinkOperatorsResultParams(ApiFlowAttr apiFlowAttr,Integer dataAssetApiId);

    /**
     * 获取编排中api算子的信息
     * @param apiFlowAttr
     * @return
     */
    List<ApiInfoDTO> getApiOperatorInfo(ApiFlowAttr apiFlowAttr);

    /**
     * 填充api算子信息
     * @param apiFlowAttr
     * @return
     */
    ApiFlowAttr fillApiOperatorInfo(ApiFlowAttr apiFlowAttr);

    /**
     * 添加算子关联api记录
     * @param apiFlowAttr
     * @param apiFlowRelation
     */
    void addOperatorRelationApi(ApiFlowAttr apiFlowAttr, ApiFlowRelation apiFlowRelation);

    /**
     * 生成api编排执行链
     * @param apiFlowAttr
     * @return
     */
    JSONObject generateApiLiteflowChain(ApiFlowAttr apiFlowAttr);
}
