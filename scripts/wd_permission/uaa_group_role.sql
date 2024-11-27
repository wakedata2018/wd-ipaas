create table uaa_group_role
(
    GROUP_ID    bigint      not null,
    ROLE_ID     varchar(36) not null comment '角色id',
    UPDATE_TIME datetime    null comment '更新时间',
    CREATE_TIME datetime    null comment '创建时间',
    primary key (GROUP_ID, ROLE_ID)
)
    row_format = DYNAMIC;

create index IDX_ROLE_ID
    on uaa_group_role (ROLE_ID);

