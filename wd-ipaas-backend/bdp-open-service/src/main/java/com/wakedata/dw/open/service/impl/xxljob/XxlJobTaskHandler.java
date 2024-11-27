package com.wakedata.dw.open.service.impl.xxljob;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.thread.LocalThreadPoolExecutorUtil;
import com.wakedata.common.redis.lock.annotation.RedisLock;
import com.wakedata.dw.open.accesstoken.AccessToken;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.xxljob.XxlJobInvokeLogMapper;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.xxljob.XxlJobDO;
import com.wakedata.dw.open.model.xxljob.XxlJobInvokeLogDO;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskHelper;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskParamHelper;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskScheduleFiledHelper;
import com.wakedata.dw.open.service.xxljob.XxlJobService;
import com.wakedata.dw.open.utils.RestTemplateUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobTaskHandler
 * @date 2022/10/24 10:32
 */
@Component
@Slf4j
public class XxlJobTaskHandler {

    private static final String API_INFO_TEMPLATE = "NAME=%s开始执行,PATH=%s的API开始执行";

    private static final String XXL_JOB_INVOKE_SOURCE_VALUE = "定时任务ID为%s";

    @Resource
    private DataAssetApiService dataAssetApiService;

    @Resource
    private XxlJobTaskHelper xxlJobTaskHelper;

    @Resource
    private XxlJobTaskParamHelper xxlJobTaskParamHelper;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Resource
    private XxlJobInvokeLogMapper xxlJobInvokeLogMapper;

    @Resource
    private XxlJobService xxlJobService;

    @Resource
    private XxlJobTaskHandler xxlJobTaskHandler;

    @XxlJob("xxlJobTaskHandler")
    public ReturnT<String> xxlJobTaskHandler(String param){

        String jobParam = XxlJobHelper.getJobParam();
        log.info("IPAAS_API定时任务任务开始创建线程");
        try {
            LocalThreadPoolExecutorUtil.getThreadPool(DwOpenConstant.XXL_JOB_THREAD_EXECUTOR).submit(()->
                    xxlJobTaskHandler.invokeOpenApi(jobParam));
        }catch (Exception e){
            log.error("IPAAS_API定时任务创建线程失败：{}",e.toString());
            throw new OpenException(e.getMessage());
        }
        return new ReturnT<>(ReturnT.SUCCESS_CODE,"IPAAS_API定时任务任务创建线程成功");
    }

    /**
     * 调用API
     * @param param apiId
     */
    @RedisLock(keys = {"#param"})
    public void invokeOpenApi(String param) {

        XxlJobDO xxlJobDO = xxlJobTaskHelper.getXxlJobDO(param);
        DataAssetApiPo dataAssetApiPo = dataAssetApiService.detail(xxlJobDO.getDataAssetApiId());

        // 定时任务的状态为停止时 手动同步ipaas和xxlJob调度中心的任务状态
        if (Objects.equals(xxlJobDO.getTaskType(),XxlJobEnums.TaskType.TASK_STOP.getValue())){
            updateXxlJobStatus(xxlJobDO);
            return;
        }
        if (checkBefore(xxlJobDO)){
            return;
        }

        String response;
        XxlJobInvokeLogDO xxlJobInvokeLog = createXxlJobInvokeLog(xxlJobDO);
        try {

            // 前置校验 (判断当前时间在时间范围以后 判断api下线)
            if (check(xxlJobDO, dataAssetApiPo, xxlJobInvokeLog)){
                xxlJobInvokeLog.setInvokeStatus(ExecuteStatusEnum.EXCEPTION.getCode());
                return;
            }

            xxlJobTaskParamHelper.convertParamsToObject(xxlJobDO);

            // HEAD
            Map<String, String> headParams = xxlJobTaskParamHelper.buildHeadParams(xxlJobDO.getApiHeadParams());
            xxlJobInvokeLog.setApiHeadParam(JSONObject.parseObject(JSON.toJSONString(headParams)).toJSONString());

            // QUERY
            Map<String, Object> queryParams = xxlJobTaskParamHelper.buildQueryParams(xxlJobDO.getApiQueryParams());
            queryParams.put(DwOpenConstant.API_INVOKE_SOURCE_PARAM_NAME,buildInvokeSourceValue(xxlJobDO.getId()));
            xxlJobInvokeLog.setApiQueryParam(JSONObject.parseObject(JSON.toJSONString(queryParams)).toJSONString());

            // BODY
            JSON bodyParams = xxlJobTaskParamHelper.buildBodyParams(xxlJobDO.getApiBodyParams());
            xxlJobInvokeLog.setApiBodyParam(bodyParams.toJSONString());

            // 记录执行次数
            Long taskExecuteAmount = xxlJobTaskHelper.updateTaskExecuteAmount(xxlJobDO);

            // 调用API测试接口
            log.info(String.format(API_INFO_TEMPLATE,dataAssetApiPo.getApiName(),dataAssetApiPo.getDataAssetApiMethod()));
            log.info("---------------------------定时任务调用API执行开始---------------------------");
            log.info("---------------------------定时任务执行该API的次数：{}-----------------------",taskExecuteAmount);
            response = invokeApi(dataAssetApiPo, xxlJobDO, headParams, queryParams, (JSONObject) bodyParams);
            log.info("---------------------------定时任务执行该API的结果：{}-----------------------",response);

            xxlJobInvokeLog.setInvokeStatus(ExecuteStatusEnum.SUCCESS.getCode());
            xxlJobInvokeLog.setInvokeResult(response);
        } catch (Exception e){
            xxlJobInvokeLog.setInvokeStatus(ExecuteStatusEnum.EXCEPTION.getCode());
            xxlJobInvokeLog.setInvokeResult(e.toString());
            log.info("---------------------------定时任务执行该API的结果：{}-----------------------",e.getMessage());
        } finally {
            // 新增定时任务日志
            xxlJobInvokeLogMapper.insert(xxlJobInvokeLog);
            log.info("---------------------------定时任务调用API执行结束---------------------------");
        }
    }

    /**
     * 校验定时任务信息
     * (校验自定义执行时，当前时间是否处于执行时间范围前)
     *
     * @param xxlJobDO 定时任务信息
     * @return true 在执行时间范围之前
     */
    private boolean checkBefore(XxlJobDO xxlJobDO) {

        if (Objects.equals(xxlJobDO.getTaskExecuteType(),XxlJobEnums.TaskExecuteType.TASK_EXECUTE_CUSTOM.getValue())){
            return new Date().getTime() < xxlJobDO.getTaskStartTime().getTime();
        }
        return false;
    }

    /**
     * 校验定时任务信息(永久或者自定义执行时，当前时间是否处于执行时间范围内)
     * 校验API基本信息(api发布状态)
     *
     * @param xxlJobDO 定时任务信息
     * @param dataAssetApiPo API基本信息
     * @return true/false
     */
    private boolean check(XxlJobDO xxlJobDO, DataAssetApiPo dataAssetApiPo,XxlJobInvokeLogDO xxlJobInvokeLog) {

        // 是否在执行范围内 (判断时间区间之后的情况)
        boolean unTime = !xxlJobTaskHelper.checkCronInTime(xxlJobDO.getTaskCron()) ||
                (Objects.equals(XxlJobEnums.TaskExecuteType.TASK_EXECUTE_CUSTOM.getValue(), xxlJobDO.getTaskExecuteType())
                        && !xxlJobTaskHelper.isEffectiveDate(new Date(), xxlJobDO.getTaskStartTime(), xxlJobDO.getTaskEndTime()));
        if (unTime){
            updateXxlJobStatus(xxlJobDO);
            xxlJobInvokeLog.setInvokeResult("定时任务执行时间不在设定范围内,更新任务状态为停止状态");
            return true;
        }

        // 校验Api是否上下线
        if (Objects.equals(DataAssetPublishStatusEnum.UN_PUBLISH, dataAssetApiPo.getDataAssetPublishStatus())){
            updateXxlJobStatus(xxlJobDO);
            xxlJobInvokeLog.setInvokeResult("定时任务绑定的API已下线,更新任务状态为停止状态");
            return true;
        }
        return false;
    }

    /**
     * 更新定时任务状态
     */
    private void updateXxlJobStatus(XxlJobDO xxlJobDO) {
        xxlJobTaskParamHelper.convertParamsToObject(xxlJobDO);
        XxlJobDTO xxlJobDTO = BeanUtil.copyProperties(xxlJobDO, XxlJobDTO.class);
        xxlJobDTO.setTaskType(XxlJobEnums.TaskType.TASK_STOP.getValue());
        xxlJobDTO.setUpdateTime(new Date());
        xxlJobDTO.setIsSkip(Boolean.TRUE);
        xxlJobService.updateXxlJob(xxlJobDTO);
    }

    /**
     * 调用API
     *
     * @param dataAssetApiPo API基本信息
     * @param xxlJobDO 定时任务基本信息
     * @param headParams 请求头参数
     * @param queryParams Query参数
     * @param bodyParams 请求体参数
     */
    private String invokeApi(DataAssetApiPo dataAssetApiPo, XxlJobDO xxlJobDO, Map<String, String> headParams, Map<String, Object> queryParams, JSONObject bodyParams) {

        String timeStamp = String.valueOf(new Date().getTime());
        AccessToken token = xxlJobTaskHelper.generateToken(xxlJobDO,timeStamp);
        String url = restTemplateUtil.buildRequestUrl(dataAssetApiPo.getDataAssetApiMethod(), token.getAccessToken(), timeStamp,new JSONObject(queryParams));
        return Objects.equals(DataAssetEnums.ReqMethod.POST, dataAssetApiPo.getReqMethod())
                ? restTemplateUtil.postByJson(url, headParams, bodyParams)
                : restTemplateUtil.get(url, headParams);
    }

    /**
     * 构建定时日志基础信息
     */
    private XxlJobInvokeLogDO createXxlJobInvokeLog(XxlJobDO xxlJobDO){
        XxlJobInvokeLogDO xxlJobInvokeLogDO = new XxlJobInvokeLogDO();
        xxlJobInvokeLogDO.setTaskId(xxlJobDO.getId());
        xxlJobInvokeLogDO.setDataAssetApiId(xxlJobDO.getDataAssetApiId());
        xxlJobInvokeLogDO.setApiGroupId(xxlJobDO.getApiGroupId());
        xxlJobInvokeLogDO.setCreateBy(xxlJobDO.getCreateBy());
        xxlJobInvokeLogDO.setUpdateBy(xxlJobDO.getUpdateBy());
        xxlJobInvokeLogDO.setCreateTime(new Date());
        xxlJobInvokeLogDO.setUpdateTime(new Date());
        xxlJobInvokeLogDO.setDataAssetAppId(xxlJobDO.getDataAssetAppId());
        return xxlJobInvokeLogDO;
    }

    /**
     * 调用来源(定时任务ID)
     */
    private String buildInvokeSourceValue(Integer id){
        String value = String.format(XXL_JOB_INVOKE_SOURCE_VALUE,id);
        return ApiInvokeSource.API_XXL_JOB.getValue().concat(":").concat(value);
    }
}
