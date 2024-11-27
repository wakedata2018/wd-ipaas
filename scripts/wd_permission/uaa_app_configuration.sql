create table uaa_app_configuration
(
    ID              bigint auto_increment comment '自增ID'
        primary key,
    TENANT_ID       bigint       null comment '租户ID',
    APP_BU_ID       bigint       null comment '应用业务单元ID',
    CONFIG_KEY      varchar(60)  not null comment '键',
    CONFIG_VALUE    varchar(60)  not null comment '值',
    CONFIG_DESCRIBE varchar(255) null comment '描述',
    CREATE_TIME     datetime     not null comment '创建时间',
    UPDATE_TIME     datetime     not null comment '更新时间',
    OPERATOR        varchar(60)  not null comment '操作人'
)
    comment '应用全局配置表' row_format = DYNAMIC;

create index IDX_TENANT_APP_BU_ID
    on uaa_app_configuration (TENANT_ID, APP_BU_ID);

