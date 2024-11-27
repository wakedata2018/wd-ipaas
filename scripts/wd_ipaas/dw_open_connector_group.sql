create table dw_open_connector_group
(
    id          bigint auto_increment comment '主键'
        primary key,
    lessee_id   int          not null comment '租户id',
    parent_id   bigint       not null comment '父级id',
    group_name  varchar(255) not null comment '分类名称',
    description varchar(255) null comment '描述',
    create_by   varchar(100) not null comment '创建人',
    create_time datetime     not null comment '创建时间',
    update_by   varchar(100) not null comment '更新人',
    update_time datetime     not null comment '更新时间',
    sort_field  int          not null comment '排序字段',
    constraint uk_dw_open_connector_group_group_name
        unique (group_name)
)
    comment '平台分类表';

