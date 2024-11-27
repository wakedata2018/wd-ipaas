create table uaa_user_auths
(
    ID            bigint auto_increment
        primary key,
    USER_ID       bigint      null comment '用户唯一标识',
    IDENTITY_TYPE tinyint(1)  null comment '登录类型（手机号）',
    IDENTIFIER    varchar(64) null comment '标识（手机号 邮箱 用户名或第三方应用的唯一标识）',
    CREDENTIAL    varchar(64) null comment '密码凭证',
    UPDATE_TIME   datetime    null comment '最后更新时间',
    CREATE_TIME   datetime    null comment '创建时间',
    constraint UNI_IDENTIFIER_TYPE
        unique (IDENTITY_TYPE, IDENTIFIER)
)
    row_format = DYNAMIC;

create index IDX_USER_ID
    on uaa_user_auths (USER_ID);

INSERT INTO wd_permission.uaa_user_auths (ID, USER_ID, IDENTITY_TYPE, IDENTIFIER, CREDENTIAL, UPDATE_TIME, CREATE_TIME) VALUES (4, 1, 1, '13012345678', '8f0293ba1bd1af4ca3edf7f582f52321', '2024-09-02 16:46:31', '2022-05-17 14:50:40');
INSERT INTO wd_permission.uaa_user_auths (ID, USER_ID, IDENTITY_TYPE, IDENTIFIER, CREDENTIAL, UPDATE_TIME, CREATE_TIME) VALUES (5, 1, 3, '13012345678', '8f0293ba1bd1af4ca3edf7f582f52321', '2024-09-02 16:46:31', '2022-05-17 14:50:40');
INSERT INTO wd_permission.uaa_user_auths (ID, USER_ID, IDENTITY_TYPE, IDENTIFIER, CREDENTIAL, UPDATE_TIME, CREATE_TIME) VALUES (6, 1, 4, '13012345678', 'HAjahKJherqvgsjhKJkjHj', '2022-05-17 14:50:40', '2022-05-17 14:50:40');
INSERT INTO wd_permission.uaa_user_auths (ID, USER_ID, IDENTITY_TYPE, IDENTIFIER, CREDENTIAL, UPDATE_TIME, CREATE_TIME) VALUES (10, 3, 1, '13123456789', '9de9dbbbe8a6306b9edddc43ae8e7ae7', '2023-05-08 17:02:06', '2023-05-08 17:02:06');
INSERT INTO wd_permission.uaa_user_auths (ID, USER_ID, IDENTITY_TYPE, IDENTIFIER, CREDENTIAL, UPDATE_TIME, CREATE_TIME) VALUES (11, 3, 3, '13123456789', '9de9dbbbe8a6306b9edddc43ae8e7ae7', '2023-05-08 17:02:06', '2023-05-08 17:02:06');
INSERT INTO wd_permission.uaa_user_auths (ID, USER_ID, IDENTITY_TYPE, IDENTIFIER, CREDENTIAL, UPDATE_TIME, CREATE_TIME) VALUES (12, 3, 4, '13123456789', 'HAjahKJherqvgsjhKJkjHj', '2023-05-08 17:02:06', '2023-05-08 17:02:06');
