create table dw_open_event_topic
(
    id              int auto_increment comment '自增id'
        primary key,
    topic           varchar(50)       not null comment 'topic',
    address_type    tinyint           null comment '地址类型',
    remark          varchar(100)      null comment '描述',
    create_time     datetime          null,
    tag             varchar(50)       null comment '过滤标签',
    event_center_id varchar(50)       null comment '事件中心dw_open_event_SUBSCRIBE_ADDRESS表对应的id',
    address_way     tinyint default 0 null comment '0:订阅topic;1:仓库topic',
    update_time     datetime          null,
    create_user     varchar(20)       null,
    update_user     varchar(20)       null,
    lessee_id       int               null comment '租户ID'
);

