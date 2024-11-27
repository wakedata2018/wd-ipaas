package com.wakedata.dw.open.service.impl.xxljob.helper;

import com.wakedata.dw.open.config.XxlJobTaskConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.XxlJobConstant;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.XxlJobEnums;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.util.CronExpression;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WangChenSheng
 * @descriptor 定时任务调度中心参数helper类
 * @title XxlJobTaskScheduleVo
 * @date 2022/10/25 17:57
 */
@Component
@Slf4j
public class XxlJobTaskScheduleFiledHelper {

    @Resource
    private XxlJobTaskHelper xxlJobTaskHelper;

    @Resource
    private XxlJobTaskConfig xxlJobTaskConfig;

    /**
     * 新增条件
     */
    public Map<String, Object> initAddParams(XxlJobDTO xxlJobDTO) {

        Map<String, Object> xxlJobParams = initDefaultXxlJobInfo(xxlJobDTO);
        xxlJobParams.put(XxlJobConstant.EXECUTOR_PARAM, xxlJobTaskHelper.getTaskCode(xxlJobDTO.getXxlTimeStamp(), xxlJobDTO.getId()));
        xxlJobParams.put(XxlJobConstant.ADD_TIME,convertTime(xxlJobDTO.getCreateTime()));
        xxlJobParams.put(XxlJobConstant.GLUE_UPDATE_TIME,convertTime(xxlJobDTO.getCreateTime()));
        xxlJobParams.put(XxlJobConstant.AUTHOR,xxlJobDTO.getCreateBy());
        getLastAndNextTime(xxlJobDTO.getTaskType(),xxlJobDTO.getTaskCron(),xxlJobParams);

        return xxlJobParams;
    }

    /**
     * 编辑条件
     */
    public Map<String, Object> initUpdateParams(XxlJobDTO xxlJobDTO, String xxlTimeStamp) {

        // 通过任务中心唯一标识获取调度中心任务信息主键id
        Integer scheduleTaskId = xxlJobTaskHelper.getScheduleTaskId(xxlTimeStamp,xxlJobDTO.getId());

        Map<String,Object> xxlJobParams = initDefaultXxlJobInfo(xxlJobDTO);
        xxlJobParams.put(DwOpenConstant.FILTER_PREFIX.concat(XxlJobConstant.ID), scheduleTaskId);
        xxlJobParams.put(XxlJobConstant.UPDATE_TIME,convertTime(xxlJobDTO.getUpdateTime()));
        xxlJobParams.put(XxlJobConstant.AUTHOR,xxlJobDTO.getUpdateBy());
        getLastAndNextTime(xxlJobDTO.getTaskType(),xxlJobDTO.getTaskCron(),xxlJobParams);

        return xxlJobParams;
    }

    /**
     * 删除条件
     */
    public Map<String, Object> initDeleteParams(Integer id,String timeStamp) {

        // 根据当前定时任务的时间戳获取调度中心的任务自增主键
        Integer scheduleTaskId = xxlJobTaskHelper.getScheduleTaskId(timeStamp,id);

        Map<String,Object> xxlJobParams = new HashMap<>();
        xxlJobParams.put(DwOpenConstant.FILTER_PREFIX.concat(XxlJobConstant.ID),scheduleTaskId);

        return xxlJobParams;
    }

    /**
     * 初始化调度中心的定时任务的默认
     */
    private Map<String,Object> initDefaultXxlJobInfo(XxlJobDTO xxlJobDTO) {
        Map<String,Object> xxlJobParams = new HashMap<>();
        xxlJobParams.put(XxlJobConstant.JOB_GROUP,xxlJobTaskConfig.getJobGroupId());
        xxlJobParams.put(XxlJobConstant.ALARM_EMAIL,XxlJobConstant.ALARM_EMAIL_VALUE);
        xxlJobParams.put(XxlJobConstant.EXECUTOR_ROUTE_STRATEGY,XxlJobConstant.EXECUTOR_ROUTE_STRATEGY_VALUE);
        xxlJobParams.put(XxlJobConstant.EXECUTOR_HANDLER,XxlJobConstant.EXECUTOR_HANDLER_VALUE);
        xxlJobParams.put(XxlJobConstant.EXECUTOR_BLOCK_STRATEGY,XxlJobConstant.EXECUTOR_BLOCK_STRATEGY_VALUE);
        xxlJobParams.put(XxlJobConstant.EXECUTOR_TIMEOUT,0);
        xxlJobParams.put(XxlJobConstant.EXECUTOR_FAIL_RETRY_COUNT,0);
        xxlJobParams.put(XxlJobConstant.GLUE_TYPE,XxlJobConstant.GLUE_TYPE_VALUE);
        xxlJobParams.put(XxlJobConstant.GLUE_REMARK,XxlJobConstant.GLUE_REMARK_VALUE);
        xxlJobParams.put(XxlJobConstant.JOB_CRON, xxlJobDTO.getTaskCron());
        xxlJobParams.put(XxlJobConstant.JOB_DESC, xxlJobDTO.getTaskName());
        xxlJobParams.put(XxlJobConstant.TRIGGER_STATUS,xxlJobDTO.getTaskType());
        return xxlJobParams;
    }

    /**
     * 根据表达式获取上一次执行时间和下一次执行时间(校验定时任务表达式)
     *
     * @param taskType 任务状态
     * @param cron 表达式
     * @param xxlJobParams 参数集合
     */
    private void getLastAndNextTime(Integer taskType, String cron, Map<String, Object> xxlJobParams){

        Date date = new Date();
        CronExpression cronSequenceGenerator;
        try {
            cronSequenceGenerator = new CronExpression(cron);
        } catch (IllegalArgumentException | ParseException e){
            throw new OpenException(MsgCodeEnum.w_xxl_job_cron_error);
        }

        // 任务状态为开启时,通过表达式获取上一次执行时间和下一次执行时间
        // 下一次执行的时间
        Date nextTime = cronSequenceGenerator.getNextValidTimeAfter(date);
        if (Objects.isNull(nextTime)) {
            throw new OpenException(MsgCodeEnum.w_xxl_job_cron_is_not_execute);
        }
        // 上一次执行的时间
        Date doubleNextTime = cronSequenceGenerator.getNextValidTimeAfter(nextTime);
        long intervalTime = doubleNextTime.getTime() - nextTime.getTime();
        Date lastTime = new Date(nextTime.getTime() - intervalTime);

        // 任务状态为停止时 默认上一个和下一个执行时间戳为0
        if (Objects.equals(taskType,XxlJobEnums.TaskType.TASK_STOP.getValue())){
            xxlJobParams.put(XxlJobConstant.TRIGGER_LAST_TIME,0);
            xxlJobParams.put(XxlJobConstant.TRIGGER_NEXT_TIME,0);
            return;
        }

        xxlJobParams.put(XxlJobConstant.TRIGGER_LAST_TIME,lastTime.getTime());
        xxlJobParams.put(XxlJobConstant.TRIGGER_NEXT_TIME,nextTime.getTime());
    }

    /**
     * 处理xxlJob数据版本低无法直接接受Date
     */
    private String convertTime(Date date){
        // 将提交的数据添加到数据库中.
        String format = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            format = sf.format(date);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return format;
    }

}
