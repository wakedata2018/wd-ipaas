package com.wakedata.dw.open.liteflow.emptyimpl;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.liteflow.DataAssetApiLiteflowService;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.dto.ApiInfoDTO;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowRelation;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;

import java.util.List;

/**
 * 定义空实现,避免去掉liteflow模块之后启动报错
 * @author luomeng
 * @date 2022/12/14 11:05
 */
public class DataAssetApiLiteflowServiceEmptyImpl implements DataAssetApiLiteflowService {
    @Override
    public List<Integer> getApiLiteflowRelationApiAndHandle(ApiFlowAttr apiFlowAttr) {
        return null;
    }

    @Override
    public void updateInternalApiOperatorIfNecessary(SubscribeRecordVo subscribeRecordVo, ApiFlowAttr apiAttr, String inCharge) {

    }

    @Override
    public List<ApiConditionPo> getApiSinkOperatorsResultParams(ApiFlowAttr apiFlowAttr, Integer dataAssetApiId) {
        return null;
    }

    @Override
    public List<ApiInfoDTO> getApiOperatorInfo(ApiFlowAttr apiFlowAttr) {
        return null;
    }

    @Override
    public ApiFlowAttr fillApiOperatorInfo(ApiFlowAttr apiFlowAttr) {
        return null;
    }

    @Override
    public void addOperatorRelationApi(ApiFlowAttr apiFlowAttr, ApiFlowRelation apiFlowRelation) {

    }

    @Override
    public JSONObject generateApiLiteflowChain(ApiFlowAttr apiFlowAttr) {
        return null;
    }
}
