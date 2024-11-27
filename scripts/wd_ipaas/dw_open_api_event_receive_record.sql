create table dw_open_api_event_receive_record
(
    id                bigint auto_increment comment '主键'
        primary key,
    message           text          null comment '消息内容',
    data_asset_api_id int           null comment 'API ID',
    request_url       varchar(1024) null comment '请求服务编排URL',
    execute_status    tinyint       null comment '请求结果，0：成功、1：失败、2：异常',
    response_body     text          null comment '响应数据',
    exception_message text          null comment '异常信息',
    lessee_id         bigint        null comment '租户ID',
    create_time       datetime      null comment '创建时间',
    update_time       datetime      null on update CURRENT_TIMESTAMP comment '更新时间'
);

