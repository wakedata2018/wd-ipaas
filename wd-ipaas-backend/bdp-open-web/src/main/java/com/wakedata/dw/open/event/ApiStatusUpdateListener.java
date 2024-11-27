//package com.wakedata.dw.open.event;
//
//import com.wakedata.dw.open.enums.ApiOperateStatusEnum;
//import com.wakedata.dw.open.enums.DataAssetEnums;
//import com.wakedata.dw.open.model.api.AbstractApiAttr;
//import com.wakedata.dw.open.model.api.DataAssetApiPo;
//import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
//import com.wakedata.dw.open.model.api.flow.operator.Operator;
//import com.wakedata.dw.open.model.api.flow.operator.api.ApiComponent;
//import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
//import com.wakedata.dw.open.model.api.flow.operator.crontab.CrontabOperator;
//import com.wakedata.dw.open.request.XxlJobInfoCRUDRequest;
//import com.wakedata.dw.open.service.api.DataAssetApiService;
//import com.wakedata.dw.open.service.vo.ApiDetailVo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
///**
// * API状态改变监听器
// *
// * @author 佟蕊 (待删除)
// */
//@Service
//@Slf4j
//public class ApiStatusUpdateListener implements ApplicationListener<ApiStatusUpdateEvent> {
//
//    @Autowired
//    private DataAssetApiService dataAssetApiService;
//
//    @Autowired
//    private XxlJobInfoCRUDRequest xxlJobInfoCRUDRequest;
//
//    /**
//     * 监听入口
//     *
//     * @param apiStatusUpdateEvent
//     */
//    @Override
//    public void onApplicationEvent(ApiStatusUpdateEvent apiStatusUpdateEvent) {
//        String apiStatus = apiStatusUpdateEvent.getApiStatus();
//        Integer dataAssetApiId = apiStatusUpdateEvent.getDataAssetApiId();
//        log.info("ApiStatusUpdateListener 监听：apiStatus:" + apiStatus + ",dataAssetApiId:" + dataAssetApiId);
//
//        if (null == dataAssetApiId) {
//            withoutApiIdExecute(apiStatusUpdateEvent, apiStatus);
//            return;
//        }
//
//        hasApiIdExecute(dataAssetApiId, apiStatus);
//    }
//
//    /**
//     * 无ApiId的监听
//     *
//     * @param apiStatusUpdateEvent
//     * @param apiStatus
//     */
//    public void withoutApiIdExecute(ApiStatusUpdateEvent apiStatusUpdateEvent, String apiStatus) {
//        ApiDetailVo apiDetailVo = apiStatusUpdateEvent.getApiDetailVo();
//        DataAssetApiPo dataAssetApiPo = apiDetailVo.getDataAssetApi();
//        AbstractApiAttr apiAttr = dataAssetApiPo.getApiAttr();
//        operatorDiff(apiStatus, null, dataAssetApiPo, apiAttr);
//    }
//
//    /**
//     * 有ApiId的监听
//     *
//     * @param dataAssetApiId
//     * @param apiStatus
//     */
//    public void hasApiIdExecute(Integer dataAssetApiId, String apiStatus) {
//        DataAssetApiPo dataAssetApiPo = dataAssetApiService.detail(dataAssetApiId);
//        AbstractApiAttr apiAttr = dataAssetApiPo.getApiAttr();
//        operatorDiff(apiStatus, dataAssetApiId, dataAssetApiPo, apiAttr);
//    }
//
//    /**
//     * 不同算子处理
//     *
//     * @param apiStatus
//     * @param dataAssetApiId
//     * @param dataAssetApiPo
//     * @param apiAttr
//     */
//    private void operatorDiff(String apiStatus, Integer dataAssetApiId, DataAssetApiPo dataAssetApiPo, AbstractApiAttr apiAttr) {
//        if (!dataAssetApiPo.getApiType().equals(DataAssetEnums.DataApiType.LITE_FLOW)) {
//            return;
//        }
//
//        if (!(apiAttr instanceof ApiFlowAttr)) {
//            return;
//        }
//
//        ApiFlowAttr apiFlowAttr = (ApiFlowAttr) dataAssetApiPo.getApiAttr();
//        Map<String, Operator> operators = apiFlowAttr.getOperators();
//
//        operators.forEach((key, op) -> {
//            if (op instanceof CrontabOperator) {
//                // 定时任务api新增时无需操作
//                if (null == dataAssetApiId) {
//                    return;
//                }
//                crontabOpratorProcess(apiStatus, dataAssetApiId, (CrontabOperator) op);
//            }
//            if (op instanceof ApiOperator) {
//                ApiComponent apiComponent = (ApiComponent) op.getComponent();
//                DataAssetApiPo dataAssetApi = apiComponent.getDataAssetApi();
//                DataAssetEnums.DataApiType apiType = dataAssetApi.getApiType();
//                AbstractApiAttr subApiAttr = dataAssetApi.getApiAttr();
////                if (apiType.equals(DataAssetEnums.DataApiType.EVENT_RECEIVE)) {
////                    eventReceiveProcess(apiStatus, subApiAttr, dataAssetApiId);
////                    return;
////                }
//            }
//        });
//    }
//
//    /**
//     * 流程编排接入定时任务执行流程
//     *
//     * @param apiStatus
//     * @param dataAssetApiId
//     * @param op
//     */
//    private void crontabOpratorProcess(String apiStatus, Integer dataAssetApiId, CrontabOperator op) {
//        String cronExpression = op.getCronExpression();
//        String taskDescription = op.getTaskDescription();
//
//        if (apiStatus.equals(ApiOperateStatusEnum.API_DELETE.name())) {
//
//            Integer jobCount = xxlJobInfoCRUDRequest.jobCount(dataAssetApiId);
//            if (jobCount != 1) {
//                log.error("xxl-job job数量匹配异常，dataAssetApiId：" + dataAssetApiId);
//                return;
//            }
//
//            xxlJobInfoCRUDRequest.delete(dataAssetApiId);
//            return;
//        }
//
//        if (apiStatus.equals(ApiOperateStatusEnum.API_OFFLINE.name())) {
//            xxlJobInfoCRUDRequest.stop(dataAssetApiId);
//            return;
//        }
//
//        if (apiStatus.equals(ApiOperateStatusEnum.API_PUBLISH.name())) {
//            Boolean hasJob = xxlJobInfoCRUDRequest.hasJob(dataAssetApiId);
//            if (hasJob) {
//                xxlJobInfoCRUDRequest.update(taskDescription, cronExpression, dataAssetApiId);
//                return;
//            }
//            xxlJobInfoCRUDRequest.add(taskDescription, cronExpression, dataAssetApiId);
//        }
//    }
//
//}
