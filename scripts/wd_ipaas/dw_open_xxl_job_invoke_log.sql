create table dw_open_xxl_job_invoke_log
(
    id                int auto_increment comment '自增主键'
        primary key,
    task_id           int         null comment '定时任务id',
    data_asset_app_id int         null comment '接入的应用id',
    data_asset_api_id int         null comment '数据apiId',
    api_group_id      int         null comment '接口分组id',
    api_head_param    json        null comment 'api请求头',
    api_query_param   json        null comment 'apiQuery参数',
    api_body_param    json        null comment 'apiBody参数',
    invoke_status     int         null comment 'api执行状态',
    invoke_result     text        null comment '定时任务调用结果(正确：接口返回数据 错误：错误详情)',
    create_time       datetime    null comment '创建时间',
    create_by         varchar(40) null comment '创建人',
    update_time       datetime    null comment '更新时间',
    update_by         varchar(40) null comment '更新人'
);

