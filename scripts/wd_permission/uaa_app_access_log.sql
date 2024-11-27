create table uaa_app_access_log
(
    ID         bigint auto_increment comment '自增ID'
        primary key,
    TENANT_ID  bigint      null comment '租户ID',
    APP_BU_ID  bigint      null comment '应用业务单元ID',
    IP_ADDRESS varchar(15) not null comment 'IP地址',
    IP_TYPE    tinyint     not null comment 'IP类型 0黑名单 1白名单 2灰名单',
    LOGIN_NAME varchar(25) not null comment '登录账号',
    LOGIN_TIME datetime    not null comment '登录时间',
    PHONE      varchar(11) null comment '手机号'
)
    comment '访问日志表' row_format = DYNAMIC;

create index IDX_TENANT_APP_BU_ID
    on uaa_app_access_log (TENANT_ID, APP_BU_ID);

