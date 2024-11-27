create table dw_open_connector_auth_config
(
    id                       bigint auto_increment comment 'ID'
        primary key,
    lessee_id                bigint            null comment '租户id',
    connector_id             bigint            null comment '连接器id',
    auth_name                varchar(64)       null comment '鉴权名称',
    auth_identification      varchar(64)       null comment '鉴权标识',
    connector_environment_id bigint            null comment '环境id',
    connector_api_id         bigint            null comment '关联的鉴权接口id',
    auth_config              text              null comment '鉴权配置;配置鉴权参数获取方式、对应key、以及接口校验规则',
    status                   tinyint default 0 null comment '状态 0：未启用 1：已启用',
    create_by                varchar(64)       null comment '创建人',
    create_time              datetime          null comment '创建时间',
    update_by                varchar(64)       null comment '更新人',
    update_time              datetime          null comment '更新时间',
    constraint uk_auth_identification
        unique (auth_identification)
)
    comment '连接器鉴权配置表;1、连接器鉴权信息配置';

