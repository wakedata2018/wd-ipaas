create table uaa_admin_user_scope
(
    ADMIN_USER_ID bigint   not null comment '用户id',
    SCOPE_ID      bigint   not null comment '业务域id',
    CREATE_TIME   datetime not null comment '创建时间',
    primary key (ADMIN_USER_ID, SCOPE_ID)
)
    comment '用户业务域关联表' row_format = DYNAMIC;

