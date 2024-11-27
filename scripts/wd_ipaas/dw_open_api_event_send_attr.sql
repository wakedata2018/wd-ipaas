create table dw_open_api_event_send_attr
(
    id                int auto_increment
        primary key,
    data_asset_api_id int          null comment 'API;主键',
    event_code        varchar(255) null comment '事件编码',
    topic             varchar(255) null comment '算子发送的topic',
    cluster_address   varchar(255) null comment '集群地址',
    mq_type           int          null comment '仓库类型',
    create_time       datetime     null,
    update_time       datetime     null,
    lessee_id         int          null comment '租户ID',
    access_key        varchar(255) null comment '请求服务端所需的accesskey',
    secret_key        varchar(255) null comment '请求服务端所需的secretkey',
    tag               varchar(255) null comment '标签',
    message_template  text         null comment '消息内容模板',
    config_name       varchar(64)  not null comment '配置名（唯一）',
    constraint uk_dw_open_api_event_send_attr_config_name
        unique (config_name, data_asset_api_id)
);

