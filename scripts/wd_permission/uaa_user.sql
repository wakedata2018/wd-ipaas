create table uaa_user
(
    ID           bigint auto_increment
        primary key,
    ACCOUNT      varchar(128)         null comment '账号',
    NICK_NAME    varchar(32)          null comment '用户昵称',
    PHONE        varchar(20)          null comment '联系电话',
    EMAIL        varchar(64)          null comment '用户邮箱',
    AVATAR       varchar(256)         null comment '用户头像',
    DOMAIN_ID    bigint               null comment '用户所属域',
    STATUS       tinyint(1)           null comment '状态',
    UPDATE_TIME  datetime             null comment '最后更新时间',
    CREATE_TIME  datetime             null comment '创建时间',
    SCOPE_ID     bigint               null comment '分站点',
    ACCOUNT_TYPE tinyint(1) default 1 null comment '用户管理类型 0-集团 1-业态',
    constraint UNI_PHONE_STATUS
        unique (PHONE, STATUS),
    constraint uk_idx_account_status
        unique (ACCOUNT, STATUS)
)
    row_format = DYNAMIC;

create index IDX_DOMAIN_ID
    on uaa_user (DOMAIN_ID);

INSERT INTO wd_permission.uaa_user (ID, ACCOUNT, NICK_NAME, PHONE, EMAIL, AVATAR, DOMAIN_ID, STATUS, UPDATE_TIME, CREATE_TIME, SCOPE_ID, ACCOUNT_TYPE) VALUES (1, null, '演示用户', '13012345678', null, null, 2, 1, '2022-05-17 14:50:40', '2022-05-17 14:50:40', null, 1);
INSERT INTO wd_permission.uaa_user (ID, ACCOUNT, NICK_NAME, PHONE, EMAIL, AVATAR, DOMAIN_ID, STATUS, UPDATE_TIME, CREATE_TIME, SCOPE_ID, ACCOUNT_TYPE) VALUES (3, null, '超级管理员', '13123456789', null, null, 807, 1, '2023-05-08 17:02:06', '2023-05-08 17:02:06', null, 1);
