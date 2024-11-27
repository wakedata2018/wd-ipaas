create table uaa_admin_user
(
    ID           bigint auto_increment comment '自增id'
        primary key,
    ACCOUNT      varchar(50)          not null comment '账号',
    PASSWORD     varchar(50)          not null comment '密码',
    NICK_NAME    varchar(255)         null comment '昵称',
    PHONE        varchar(50)          null comment '手机号',
    EMAIL        varchar(50)          null comment '邮箱',
    STATUS       tinyint(1) default 1 null comment '状态 1-启用 0-禁用 -1删除',
    TYPE         tinyint(1) default 0 null comment '类型 1-超级管理员 0-普通账号',
    CREATE_TIME  datetime             null comment '创建时间',
    UPDATE_TIME  datetime             null comment '修改时间',
    TENANT_ID    bigint               null comment '租户ID',
    ACCOUNT_TYPE tinyint    default 0 null comment '账号类型 0：权限后台账号 1：ipaas平台账号'
)
    comment '权限系统账号表' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_admin_user (ID, ACCOUNT, PASSWORD, NICK_NAME, PHONE, EMAIL, STATUS, TYPE, CREATE_TIME, UPDATE_TIME, TENANT_ID, ACCOUNT_TYPE) VALUES (1, 'root', 'dac43acd8368663076d593e3f4928b90', '超级管理员', null, null, 1, 1, '2020-07-03 11:22:53', '2024-07-16 20:27:52', null, 0);
INSERT INTO wd_permission.uaa_admin_user (ID, ACCOUNT, PASSWORD, NICK_NAME, PHONE, EMAIL, STATUS, TYPE, CREATE_TIME, UPDATE_TIME, TENANT_ID, ACCOUNT_TYPE) VALUES (3, 'guest', 'bc64e14e6e2d6da7ad686fb8fa40813d', '体验账号', null, null, 1, 0, '2022-03-14 19:08:24', '2022-03-14 19:10:20', null, 0);
