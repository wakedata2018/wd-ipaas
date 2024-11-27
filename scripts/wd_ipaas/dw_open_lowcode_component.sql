create table dw_open_lowcode_component
(
    id                 int auto_increment comment '自增ID'
        primary key,
    app_id             int           null comment '应用ID',
    ep_id              int           null comment '租户ID',
    name               varchar(255)  not null comment '标识符,;必须唯一',
    title              varchar(255)  not null comment '标题',
    description        varchar(255)  null comment '详细描述',
    meta               json          not null comment '组件元数据,JSON;字符串',
    icon               varchar(5000) null comment '图标',
    cover              varchar(5000) null comment '封面',
    content            json          null comment '组件详情，JSON;字符串',
    compressed_content json          null comment '压缩版本组件详情',
    create_time        datetime      null comment '创建时间',
    update_time        datetime      null comment '最后更新时间',
    create_by          bigint        null comment '创建者',
    update_by          bigint        null comment '最后更新者'
)
    comment '自定义组件';

create index idx_app_id
    on dw_open_lowcode_component (app_id);

create index idx_name
    on dw_open_lowcode_component (name);

