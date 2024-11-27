create table dw_open_event_source_address
(
    id                             int auto_increment comment '自增id'
        primary key,
    enable                         tinyint default 0 null comment '是否启用',
    bootstrap_servers              varchar(50)       null comment 'kafka/mq;地址',
    username                       varchar(50)       null comment 'kafka/mq;账户',
    password                       varchar(50)       null comment 'kafka/mq;密码',
    event_type                     tinyint           null comment '事件源类型;1:http,2:kafka,3:rocketmq',
    topics                         varchar(200)      null comment 'topics，多个topic;逗号隔开',
    event_center_id                varchar(128)      null comment '事件中心对应的id;同步成功后回写',
    create_time                    datetime          null comment '创建时间;',
    update_time                    datetime          null comment '更新时间;',
    lessee_id                      int               null comment '租户id;',
    event_center_subscribe_address varchar(128)      null comment '新增订阅地址同步事件中心后回传的订阅地址的id'
)
    comment '仓库地址';

