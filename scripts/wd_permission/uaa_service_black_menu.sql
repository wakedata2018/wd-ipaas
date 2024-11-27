create table uaa_service_black_menu
(
    ID          bigint auto_increment comment '自增id'
        primary key,
    TENANT_ID   bigint      null comment '租户ID',
    APP_BU_ID   bigint      null comment '应用业务单元ID',
    SERVICE_ID  varchar(36) not null comment '模块id',
    MENU_ID     varchar(36) not null comment '菜单id',
    CREATE_TIME datetime    null comment '创建时间'
)
    comment '模块黑名单菜单' row_format = DYNAMIC;

