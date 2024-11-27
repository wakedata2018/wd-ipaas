create table dw_open_lowcode_config
(
    id          int auto_increment comment '自增ID'
        primary key,
    ep_id       int      null comment '租户ID',
    app_id      int      null comment '应用ID',
    type        char(10) null comment 'runtime;应用运行时配置信息,editor 应用编辑时配置信息,user 用户配置信息',
    content     json     null comment '配置信息',
    create_time datetime null comment '创建时间',
    update_time datetime null comment '最后更新时间',
    create_by   bigint   null comment '创建者',
    update_by   bigint   null comment '最后更新者'
)
    comment '配置信息';

create index idx_app_id
    on dw_open_lowcode_config (app_id);

