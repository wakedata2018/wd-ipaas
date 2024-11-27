create table uaa_role_component
(
    COMPONENT_ID bigint      not null comment '组件ID',
    ROLE_ID      varchar(36) not null comment '角色id',
    UPDATE_TIME  datetime    not null comment '更新时间',
    CREATE_TIME  datetime    not null comment '创建时间',
    primary key (COMPONENT_ID, ROLE_ID)
)
    row_format = DYNAMIC;

