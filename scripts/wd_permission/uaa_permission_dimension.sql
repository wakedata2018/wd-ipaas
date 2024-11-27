create table uaa_permission_dimension
(
    PERMISSION_ID varchar(36) not null comment '权限(接口)id',
    DIMENSION_ID  bigint      not null comment '维度id',
    CREATE_TIME   datetime    null comment '创建时间',
    primary key (PERMISSION_ID, DIMENSION_ID)
)
    comment '权限维度表' row_format = DYNAMIC;

