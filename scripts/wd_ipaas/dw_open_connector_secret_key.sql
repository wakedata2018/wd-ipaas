create table dw_open_connector_secret_key
(
    id             bigint auto_increment comment '主键id'
        primary key,
    lessee_id      int          not null comment '租户id',
    connector_id   bigint       not null comment '平台id',
    environment_id bigint       not null comment '关联的平台环境地址id',
    name           varchar(200) not null comment '密钥名称',
    secret_key     varchar(255) not null comment '密钥唯一标识',
    is_enable      tinyint      not null comment '是否启用',
    description    varchar(200) null comment '描述',
    params_json    text         not null comment '鉴权参数值',
    create_by      varchar(100) null comment '创建人',
    create_time    datetime     null comment '创建时间',
    update_by      varchar(100) null comment '更新人',
    update_time    datetime     null comment '更新时间',
    constraint uk_dw_open_connector_secret_key
        unique (secret_key)
)
    comment '平台密钥表';

create index idx_dw_open_connector_secret_key_connector_id
    on dw_open_connector_secret_key (connector_id);

