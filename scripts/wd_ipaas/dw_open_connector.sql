create table dw_open_connector
(
    id                    bigint auto_increment comment '主键'
        primary key,
    lessee_id             int                                      not null comment '租户id',
    group_id              bigint                                   not null comment '分类id',
    auth_type             varchar(100) default 'NO_AUTHENTICATION' not null comment '平台鉴权类型',
    enable_status         tinyint                                  not null comment '启用状态',
    name                  varchar(200)                             not null comment '名称',
    version               varchar(200)                             not null comment '版本',
    developer             varchar(200)                             not null comment '开发者',
    phone                 varchar(200)                             null comment '联系电话',
    email                 varchar(200)                             null comment '邮箱',
    website               varchar(500)                             not null comment '官网',
    help_document         varchar(500)                             null comment '帮助文档',
    privacy_agreement     varchar(500)                             null comment '隐私协议',
    usage_agreement       varchar(500)                             null comment '使用协议',
    platform_introduction text                                     null comment '平台介绍',
    is_delete             tinyint      default 0                   null comment '删除标记',
    create_by             varchar(100)                             not null comment '创建人',
    create_time           datetime                                 not null comment '创建时间',
    update_by             varchar(100)                             not null comment '更新人',
    update_time           datetime                                 not null comment '更新时间',
    constraint uk_dw_open_connector_name
        unique (name)
)
    comment '平台信息表';

