create table uaa_service
(
    ID               varchar(36)                  not null comment '模块id(uuid)'
        primary key,
    SCOPE_ID         bigint                       null comment '业务域id',
    NAME             varchar(50)                  not null comment '服务名称',
    SERVICE_DESCRIBE varchar(255)                 null comment '服务描述',
    PRICE            bigint unsigned  default '0' null comment '价格 单位分',
    STATUS           tinyint unsigned default '1' null comment '状态 -1:删除 0-禁用 1-启用',
    CREATE_TIME      datetime                     null comment '创建时间',
    UPDATE_TIME      datetime                     null comment '修改时间'
)
    comment '服务模块表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('197', 1, 'default', '零售默认模块', null, 0, '2021-03-03 19:15:26', '2021-03-03 19:15:37');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('199', 117, 'default', '家居建材会自动绑定的菜单模块', null, 0, '2021-03-16 12:51:41', '2021-03-16 12:54:41');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('200', 116, 'default', null, null, 0, '2021-03-16 12:52:11', '2021-03-16 12:52:11');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('201', 115, 'default', null, null, 0, '2021-03-16 12:52:23', '2021-03-16 12:52:23');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('202', 114, 'default', null, null, 0, '2021-03-16 12:53:02', '2021-03-16 12:53:02');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('203', 113, 'default', null, null, 0, '2021-03-16 12:53:22', '2021-03-16 12:53:22');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('204', 112, 'default', null, null, 0, '2021-03-16 12:53:42', '2021-03-16 12:53:42');
INSERT INTO wd_permission.uaa_service (ID, SCOPE_ID, NAME, SERVICE_DESCRIBE, PRICE, STATUS, CREATE_TIME, UPDATE_TIME) VALUES ('b19456fb-6f33-446e-b6f8-9fdd5287c530', 111, '大会员集团default', '大会员集团所有菜单不要动', null, 1, '2022-02-17 18:09:01', '2022-02-23 11:46:23');
