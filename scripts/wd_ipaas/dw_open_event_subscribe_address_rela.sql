create table dw_open_event_subscribe_address_rela
(
    id              int auto_increment comment '自增id'
        primary key,
    event_id        int          not null comment '关联事件ID',
    topic_id        int          null comment '订阅地址Id',
    http_id         int          null comment '接收事件为http的http订阅信息id',
    event_center_id varchar(128) null comment '事件中心对应的id;同步成功后回写',
    create_time     datetime     null comment '创建时间;',
    update_time     datetime     null comment '更新时间;',
    lessee_id       int          null comment '租户id;',
    api_id          int          null comment '关联api信息'
)
    comment '订阅记录表';

