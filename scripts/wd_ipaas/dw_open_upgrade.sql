create table dw_open_upgrade
(
    id          bigint auto_increment
        primary key,
    biz_module  varchar(56) not null comment '业务模块',
    update_time datetime    not null comment '升级时间'
)
    comment '流程编排信息';

