create table dw_open_white_list
(
    id                   int auto_increment comment '自增主键'
        primary key,
    data_access_app_id   int         null comment '接入方ID',
    ip                   bigint      null comment '允许的IP地址',
    data_access_app_name varchar(64) null comment '接入名称',
    create_time          datetime    null comment '创建时间',
    update_time          datetime    null comment '更新时间'
)
    comment '应用白名单ip配置表;1、接入应用白名单ip配置表，用于放开指定ip对应用授权api的访问';

create index idx_access_app_id
    on dw_open_white_list (data_access_app_id);

