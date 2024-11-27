create table dw_open_connector_api_group
(
    id           bigint auto_increment comment '主键id'
        primary key,
    lessee_id    int          not null comment '租户id',
    connector_id bigint       not null comment '平台id',
    group_name   varchar(10)  not null comment '分组名称',
    create_by    varchar(100) not null comment '创建人',
    create_time  datetime     not null comment '创建时间',
    update_by    varchar(100) not null comment '更新人',
    update_time  datetime     not null comment '更新时间',
    constraint uk_dw_open_connector_api_group_connector_id_group_name
        unique (connector_id, group_name)
)
    comment '第三方接口分组表';

