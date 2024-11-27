create table uaa_admin_user_tenant
(
    ID          bigint auto_increment comment '自增ID'
        primary key,
    NAME        varchar(32)       null comment '租户名称',
    IDENTIFY    varchar(32)       null comment '租户标识',
    CREATE_BY   varchar(32)       null comment '创建人',
    CREATE_TIME datetime          null comment '创建时间',
    UPDATE_BY   varchar(32)       null comment '更新人',
    UPDATE_TIME datetime          null comment '更新时间',
    STATE       tinyint default 1 null comment '启用状态：1-开， 0-关',
    constraint uniq_user_tenant_identify
        unique (IDENTIFY)
)
    comment '租户表' row_format = DYNAMIC;

