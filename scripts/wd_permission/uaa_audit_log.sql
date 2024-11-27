create table uaa_audit_log
(
    ID              bigint auto_increment comment 'ID'
        primary key,
    PERMISSION_NAME varchar(255) null comment '接口名称',
    URL             varchar(255) null comment '接口url',
    USER_NAME       varchar(50)  null comment '操作用户名称',
    STATUS          tinyint(1)   null comment '操作状态 0-失败 1-成功',
    APP_BU_ID       bigint       null comment '应用业务单元ID',
    PLATFORM_ID     bigint       null comment '平台id',
    INFO            longtext     null comment '详细信息(json字符串)',
    CREATE_TIME     datetime     not null comment '创建时间',
    TENANT_ID       bigint       null comment '租户ID'
)
    comment '日志审计表' row_format = DYNAMIC;

