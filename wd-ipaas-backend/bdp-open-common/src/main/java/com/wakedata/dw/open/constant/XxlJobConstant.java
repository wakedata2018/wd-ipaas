package com.wakedata.dw.open.constant;

/**
 * @author WangChenSheng
 * @descriptor 定时任务调度中心数据表字段
 * @title XxlJobConstant
 * @date 2022/10/26 10:34
 */
public class XxlJobConstant {

    /**
     * 调度中心默认的参数字段
     */
    public static final String ID = "id";
    public static final String JOB_CRON = "job_cron";
    public static final String JOB_DESC = "job_desc";
    public static final String ADD_TIME = "add_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String AUTHOR = "author";
    public static final String EXECUTOR_PARAM = "executor_param";

    public static final String JOB_GROUP = "job_group";
    public static final String ALARM_EMAIL = "alarm_email";
    public static final String EXECUTOR_ROUTE_STRATEGY = "executor_route_strategy";
    public static final String EXECUTOR_HANDLER = "executor_handler";
    public static final String EXECUTOR_BLOCK_STRATEGY = "executor_block_strategy";
    public static final String EXECUTOR_TIMEOUT = "executor_timeout";
    public static final String EXECUTOR_FAIL_RETRY_COUNT = "executor_fail_retry_count";
    public static final String GLUE_TYPE = "glue_type";
    public static final String GLUE_SOURCE = "glue_source";
    public static final String GLUE_REMARK = "glue_remark";
    public static final String GLUE_UPDATE_TIME = "glue_updatetime";
    public static final String CHILD_JOB_ID = "child_jobid";
    public static final String TRIGGER_STATUS = "trigger_status";
    public static final String TRIGGER_LAST_TIME = "trigger_last_time";
    public static final String TRIGGER_NEXT_TIME = "trigger_next_time";

    /**
     * 调度中心默认设置的参数值
     */
    public static final String EXECUTOR_HANDLER_VALUE = "xxlJobTaskHandler";
    public static final String ALARM_EMAIL_VALUE = "default";
    public static final String EXECUTOR_ROUTE_STRATEGY_VALUE = "FIRST";
    public static final String EXECUTOR_BLOCK_STRATEGY_VALUE = "SERIAL_EXECUTION";
    public static final String GLUE_TYPE_VALUE = "BEAN";
    public static final String GLUE_REMARK_VALUE = "GLUE代码初始化";

}
