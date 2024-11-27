create table dw_open_connector_environment_address
(
    id                  bigint auto_increment comment '主键id'
        primary key,
    connector_id        bigint       not null comment '平台id',
    address_name        varchar(50)  not null comment '环境名称',
    environment_address varchar(500) not null comment '环境地址',
    lessee_id           int          not null comment '租户id',
    create_by           varchar(100) not null comment '创建人',
    create_time         datetime     not null comment '创建时间',
    update_by           varchar(100) not null comment '更新人',
    update_time         datetime     not null comment '更新时间',
    constraint uk_dw_open_connector_environment_address_aname_cid
        unique (connector_id, address_name)
)
    comment '平台环境地址表';

create index idx_dw_open_connector_environment_address_connector_id
    on dw_open_connector_environment_address (connector_id);

