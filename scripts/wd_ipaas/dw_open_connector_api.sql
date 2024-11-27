create table dw_open_connector_api
(
    id              bigint auto_increment comment '主键id'
        primary key,
    lessee_id       int               not null comment '租户id',
    api_group_id    bigint            not null comment '接口分组id',
    connector_id    bigint            not null comment '平台id',
    api_name        varchar(100)      not null comment 'api名称',
    api_method      varchar(255)      not null comment 'api地址',
    req_method      varchar(20)       not null comment '请求方式',
    api_type        tinyint           not null comment '接口类型',
    api_description varchar(1000)     null comment 'api描述',
    enable_status   tinyint default 0 not null comment 'api上线状态,0-上线，1-下线',
    create_by       varchar(100)      not null comment '创建人',
    create_time     datetime          not null comment '创建时间',
    update_by       varchar(100)      not null comment '更新人',
    update_time     datetime          not null comment '更新时间',
    constraint uk_dw_open_connector_api_connector_id_api_method
        unique (connector_id, api_method)
)
    comment '第三方api信息表';

create index idx_dw_open_connector_api_connector_id
    on dw_open_connector_api (connector_id, api_group_id);

