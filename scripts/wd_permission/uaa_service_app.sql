create table uaa_service_app
(
    ID          bigint auto_increment comment '自增id'
        primary key,
    APP_BU_ID   bigint      null comment '应用业务单元ID',
    TENANT_ID   bigint      null comment '租户ID',
    SERVICE_ID  varchar(36) not null comment '模块id',
    CREATE_TIME datetime    null comment '创建时间'
)
    comment '品牌与菜单模块关联表' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_service_app (ID, APP_BU_ID, TENANT_ID, SERVICE_ID, CREATE_TIME) VALUES (7, 2065, 476, 'b19456fb-6f33-446e-b6f8-9fdd5287c530', '2024-08-02 09:48:15');
INSERT INTO wd_permission.uaa_service_app (ID, APP_BU_ID, TENANT_ID, SERVICE_ID, CREATE_TIME) VALUES (8, 2063, 467, 'b19456fb-6f33-446e-b6f8-9fdd5287c530', '2024-08-08 16:11:54');
INSERT INTO wd_permission.uaa_service_app (ID, APP_BU_ID, TENANT_ID, SERVICE_ID, CREATE_TIME) VALUES (9, 2086, 477, 'b19456fb-6f33-446e-b6f8-9fdd5287c530', '2024-08-08 16:12:44');
