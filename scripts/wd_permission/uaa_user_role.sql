create table uaa_user_role
(
    USER_ID     bigint      not null comment '用户ID',
    ROLE_ID     varchar(36) not null comment '角色id',
    UPDATE_TIME datetime    null comment '更新时间',
    CREATE_TIME datetime    null comment '创建时间',
    primary key (USER_ID, ROLE_ID)
)
    row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_user_role (USER_ID, ROLE_ID, UPDATE_TIME, CREATE_TIME) VALUES (1, '2', '2022-05-17 14:50:40', '2022-05-17 14:50:40');
INSERT INTO wd_permission.uaa_user_role (USER_ID, ROLE_ID, UPDATE_TIME, CREATE_TIME) VALUES (3, '2', '2023-05-08 17:02:06', '2023-05-08 17:02:06');
