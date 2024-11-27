create table dw_open_xxl_job_info
(
    id                  int auto_increment comment '自增Id'
        primary key,
    data_asset_api_id   int          null comment '数据apiId',
    api_group_id        int          null comment '接口分组id',
    api_head_param      json         null comment 'api请求头',
    api_query_param     json         null comment 'apiQuery参数',
    api_body_param      json         null comment 'api请求体',
    task_name           varchar(100) null comment '任务名称',
    task_type           tinyint      null comment '任务状态',
    task_execute_type   tinyint      null comment '定时任务执行类型',
    task_start_time     datetime     null comment '自定义执行起始时间',
    task_end_time       datetime     null comment '自定义执行结束时间',
    task_execute_amount int          null comment '定时任务执行次数',
    task_cron           varchar(100) null comment '定时任务表达式',
    task_desc           varchar(100) null comment '任务描述',
    lessee_id           int          null comment '租户ID',
    create_time         datetime     null comment '创建时间',
    create_by           varchar(64)  null comment '创建人',
    update_time         datetime     null comment '更新时间',
    update_by           varchar(64)  null comment '更新人',
    xxl_time_stamp      varchar(64)  null comment '定时任务唯一标识：时间戳',
    data_asset_app_id   int          null comment '接入应用的id',
    execute_time        datetime     null comment '最后一次执行时间'
);

