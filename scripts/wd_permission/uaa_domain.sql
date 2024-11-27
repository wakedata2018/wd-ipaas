create table uaa_domain
(
    ID          bigint auto_increment
        primary key,
    NAME        varchar(32) null comment 'domain名称',
    TENANT_ID   bigint      null comment '租户ID',
    UPDATE_TIME datetime    null comment '更新时间',
    CREATE_TIME datetime    null comment '创建时间',
    constraint UNI_TENANT_NAME
        unique (NAME, TENANT_ID)
)
    row_format = DYNAMIC;

create index IDX_TENANT
    on uaa_domain (TENANT_ID);

INSERT INTO wd_permission.uaa_domain (ID, NAME, TENANT_ID, UPDATE_TIME, CREATE_TIME) VALUES (1, 'business_common', 0, '2018-05-17 17:44:31', '2018-05-17 17:44:33');
INSERT INTO wd_permission.uaa_domain (ID, NAME, TENANT_ID, UPDATE_TIME, CREATE_TIME) VALUES (2, '演示企业', 467, '2021-03-09 09:44:12', '2021-03-09 09:44:12');
INSERT INTO wd_permission.uaa_domain (ID, NAME, TENANT_ID, UPDATE_TIME, CREATE_TIME) VALUES (807, '演示企业', 476, '2023-05-08 17:02:05', '2023-05-08 17:02:05');
INSERT INTO wd_permission.uaa_domain (ID, NAME, TENANT_ID, UPDATE_TIME, CREATE_TIME) VALUES (808, '华贸测试', 477, '2024-08-08 16:10:36', '2024-08-08 16:10:36');
