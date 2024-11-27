create table uaa_platform
(
    ID            bigint auto_increment comment 'ID'
        primary key,
    PLATFORM_NAME varchar(255)         null comment '范围名称',
    PLATFORM_DESC varchar(255)         null comment '描述字段',
    STATUS        tinyint(1) default 1 null comment '状态 0-不启用 1-启用',
    CREATE_TIME   datetime             null comment '创建时间',
    UPDATE_TIME   datetime             null comment '修改时间'
)
    comment '平台域' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_platform (ID, PLATFORM_NAME, PLATFORM_DESC, STATUS, CREATE_TIME, UPDATE_TIME) VALUES (4, '惟客云', '平台域', 1, '2020-04-20 16:16:44', '2022-04-30 01:21:52');
INSERT INTO wd_permission.uaa_platform (ID, PLATFORM_NAME, PLATFORM_DESC, STATUS, CREATE_TIME, UPDATE_TIME) VALUES (63, '集成开发平台', 'ipaas平台，集成开放平台、api编排等功能', 1, '2022-05-13 19:35:51', '2022-08-04 10:07:21');
