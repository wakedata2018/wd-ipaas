create table dw_open_event
(
    id                      int auto_increment comment '自增id'
        primary key,
    name                    varchar(50)       not null comment '事件名称',
    code                    varchar(100)      not null comment '事件编码',
    remark                  varchar(100)      null comment '事件描述',
    message_max_length      int               not null comment '消息最大长度;单位KB',
    status                  tinyint default 0 not null comment '状态[0-关闭；1-启用]',
    event_expand_point      varchar(100)      null comment '事件分析扩展点',
    event_source_addressids varchar(100)      null comment '来源地址IDs',
    event_center_id         varchar(128)      null comment '事件中心对应的id;同步成功后回写',
    create_time             datetime          null,
    update_time             datetime          null,
    create_user             varchar(50)       null,
    update_user             varchar(50)       null,
    lessee_id               int               null
);

