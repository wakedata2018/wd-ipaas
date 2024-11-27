create table uaa_role_permission
(
    ROLE_ID       varchar(36) not null comment '角色id',
    PERMISSION_ID varchar(36) not null comment '权限(接口)id',
    UPDATE_TIME   datetime    null comment '更新时间',
    CREATE_TIME   datetime    null comment '创建时间',
    primary key (ROLE_ID, PERMISSION_ID)
)
    row_format = DYNAMIC;

