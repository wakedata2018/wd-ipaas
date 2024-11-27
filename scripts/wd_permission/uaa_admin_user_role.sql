create table uaa_admin_user_role
(
    ID            bigint unsigned auto_increment comment '自增ID'
        primary key,
    ADMIN_USER_ID bigint      not null comment '权限账号id',
    ROLE_ID       varchar(36) not null comment '角色id',
    CREATE_BY     varchar(32) null comment '创建人',
    CREATE_TIME   datetime    null comment '创建时间',
    UPDATE_BY     varchar(32) null comment '更新人',
    UPDATE_TIME   datetime    null comment '更新时间'
)
    comment '账号角色中间表' row_format = DYNAMIC;

