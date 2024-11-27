package com.wakedata.dw.open.service.impl.xxljob.helper;


import cn.hutool.core.collection.CollectionUtil;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.accesstoken.AccessToken;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.XxlJobConstant;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.XxlJobEnums;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.mapper.xxljob.XxlJobMapper;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.query.XxlJobQuery;
import com.wakedata.dw.open.model.xxljob.XxlJobDO;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;
import com.wakedata.dw.open.service.xxljob.XxlJobTaskScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.CronExpression;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangChenSheng
 * @descriptor 定时任务业务相关helper类
 * @title XxlJobTaskHelper
 * @date 2022/10/26 11:52
 */
@Component
@Slf4j
public class XxlJobTaskHelper {

    @Resource
    private XxlJobTaskScheduleService xxlJobTaskScheduleService;

    @Resource
    private DataAssetApiService dataAssetApiService;

    @Resource
    private ApiGroupService apiGroupService;

    @Resource
    private AccessTokenService accessTokenService;

    @Resource
    private AppAccessService appAccessService;

    @Resource
    private XxlJobMapper xxlJobMapper;

    @Resource
    private AppAccessMapper appAccessMapper;

    /**
     * 获取应用名称
     */
    public Map<Integer, String> getAppName(XxlJobQuery query) {
        AppAccessPo appAccessPo = new AppAccessPo();
        appAccessPo.setLesseeId(query.getLesseeId());

        List<AppAccessPo> appAccessPoList = appAccessMapper.pageList(appAccessPo);
        if (CollectionUtil.isEmpty(appAccessPoList)){
            return new HashMap<>();
        }

        return appAccessPoList.stream().collect(Collectors.toMap(AppAccessPo::getDataAccessAppId, AppAccessPo::getDataAccessAppName, (appId, appName) -> appName));
    }

    /**
     * 根据APP_KEY生成测试token
     */
    public AccessToken generateToken(XxlJobDO xxlJobDO, String timeStamp) {
        AppAccessPo appAccessPo = appAccessService.queryAppInfoById(xxlJobDO.getDataAssetAppId());
        return accessTokenService.getAppTokenByAppKey(appAccessPo.getDataAccessKey(),timeStamp);
    }

    /**
     * 更新定时任务调用次数
     */
    public Long updateTaskExecuteAmount(XxlJobDO xxlJobDO){
        if (Objects.equals(xxlJobDO.getTaskType(), XxlJobEnums.TaskType.TASK_STOP.getValue())){
            return 0L;
        }

        // 从缓存中获取执行次数并实现自增
        long incr = RedisUtil.getInstance().incr(this.getTaskCode(xxlJobDO.getXxlTimeStamp(), xxlJobDO.getId()), 1L);
        XxlJobDO updateCount = new XxlJobDO();
        updateCount.setId(xxlJobDO.getId());
        updateCount.setExecuteTime(new Date());
        updateCount.setTaskExecuteAmount(incr);
        xxlJobMapper.updateByPrimaryKeySelective(updateCount);

        return incr;
    }

    /**
     * 根据主键id获取任务信息
     *
     * @param id 主键id
     * @return 任务信息
     */
    public XxlJobDO queryXxlJobInfo(Integer id){
        XxlJobDO xxlJobDO = xxlJobMapper.selectByPrimaryKey(XxlJobDO.builder().id(id).build());
        if (Objects.isNull(xxlJobDO)){
            throw new OpenException(MsgCodeEnum.w_xxl_job_info__is_null);
        }

        return xxlJobDO;
    }

    /**
     * 校验API相关信息是否正确
     *
     * @param xxlJobDTO 校验参数
     */
    public void checkApiInfo(XxlJobDTO xxlJobDTO) {

        // 是否跳过此校验
        if (Objects.equals(Boolean.TRUE,xxlJobDTO.getIsSkip())){
            return;
        }

        // 校验所选的接口分组和APi是否存在
        DataAssetApiPo assetApiPo = dataAssetApiService.detail(xxlJobDTO.getDataAssetApiId());
        ApiGroupPo apiGroupPo = apiGroupService.checkForExist(xxlJobDTO.getApiGroupId());
        if (Objects.isNull(apiGroupPo) || Objects.isNull(assetApiPo)){
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }

        // 校验所选的API是否为上线状态
        if (Objects.equals(DataAssetPublishStatusEnum.UN_PUBLISH,assetApiPo.getDataAssetPublishStatus())){
            throw new OpenException(MsgCodeEnum.w_api_is_un_public);
        }
    }

    /**
     * 通过唯一标识获取到任务信息
     */
    public XxlJobDO getXxlJobDO(String param){
        // 获取定时任务自增主键
        Integer id = Integer.valueOf(StringUtils.substringAfterLast(param, DwOpenConstant.COLON));
        return this.queryXxlJobInfo(id);
    }

    /**
     * 通过任务中心唯一标识获取调度中心任务信息主键id(返回单个结果)
     */
    public Integer getScheduleTaskId(String timeStamp,Integer id){
        Map<String,Object> queryParams = new HashMap<>();
        queryParams.put(XxlJobConstant.EXECUTOR_PARAM, getTaskCode(timeStamp,id));
        List<Map<String, Object>> scheduleInfo = xxlJobTaskScheduleService.queryXxlJobTaskScheduleInfo(queryParams);
        if (CollectionUtil.isEmpty(scheduleInfo)){
            throw new OpenException(MsgCodeEnum.w_xxl_job_schedule_is_null);
        }

        return (Integer) scheduleInfo.get(DwOpenConstant.DEFAULT_FIRST).get(XxlJobConstant.ID);
    }

    /**
     * 获取定时任务的唯一标识：时间戳:主键id
     */
    public String getTaskCode(String timeStamp, Integer taskId){
        return timeStamp.concat(DwOpenConstant.COLON).concat(String.valueOf(taskId));
    }

    /**
     * 校验API是否绑定了定时任务
     */
    public void checkApiBindXxlJob(Integer apiId){
        int count = xxlJobMapper.selectCount(XxlJobDO.builder().dataAssetApiId(apiId).build());
        if (count > 0) {
            throw new OpenException(MsgCodeEnum.w_api_have_xxl_job);
        }
    }

    /**
     * 校验定时任务表达式cron
     */
    public void checkXxlJobCron(XxlJobDTO xxlJobDTO) {
        // 校验所填表达式是否合法
        if (!CronExpression.isValidExpression(xxlJobDTO.getTaskCron())){
            throw new OpenException(MsgCodeEnum.w_xxl_job_cron_is_un_valid);
        }
    }

    /**
     * 校验执行类型为自定义：是否在执行的时间范围中
     *
     * @param nowTime 当前时间
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return true：在时间范围内 false：不在时间范围内
     */
    public boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * 校验定时任务表达式cron：是否在执行的时间范围中
     *
     * @param cron 定时任务表达式
     * @return true：在时间范围内 false：不在时间范围内
     */
    public Boolean checkCronInTime(String cron) {
        CronExpression cronExpression;
        Date date = new Date();
        try {
            cronExpression = new CronExpression(cron);

            // 下一次执行的时间
            Date nextTime = cronExpression.getNextValidTimeAfter(date);
            if (Objects.isNull(nextTime)){
                return false;
            }

            // 上一次执行的时间
            Date doubleNextTime = cronExpression.getNextValidTimeAfter(nextTime);
            long intervalTime = doubleNextTime.getTime() - nextTime.getTime();
            Date lastTime = new Date(nextTime.getTime() - intervalTime);

            // 当前时间是否在范围中
            boolean effectiveDate = isEffectiveDate(date, lastTime, nextTime);
            if (!effectiveDate){
                return false;
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return true;
    }
}
