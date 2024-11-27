create table dw_open_api_event_receive_attr
(
    id                int auto_increment
        primary key,
    data_asset_api_id int          null comment 'Api;流程的ID',
    event_code        varchar(255) null comment '事件编码',
    mq_type           int          not null comment '订阅mq类型',
    cluster_address   varchar(255) not null comment '订阅地址',
    topic             varchar(255) not null comment '算子监听的topic',
    sub_status        int          null comment '订阅状态',
    api_url           varchar(255) null comment 'Api流程的url',
    api_method        varchar(255) null comment 'Api的请求方法',
    create_time       datetime     null,
    update_time       datetime     null,
    lessee_id         int          null comment '租户ID',
    access_key        varchar(255) null comment '请求服务端所需的accesskey',
    secret_key        varchar(255) null comment '请求服务端所需的secretkey',
    tag               varchar(255) null comment '标签',
    message_template  text         null comment '消息内容模板',
    operator_name     varchar(100) not null comment '算子节点步骤名',
    constraint uk_data_asset_api_id
        unique (data_asset_api_id)
);

