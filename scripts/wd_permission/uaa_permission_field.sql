create table uaa_permission_field
(
    ID            bigint auto_increment comment '自增id'
        primary key,
    PERMISSION_ID varchar(36)  not null comment '权限(接口)id',
    FIELD         varchar(50)  null comment '字段',
    FIELD_NAME    varchar(255) null comment '字段名',
    CREATE_TIME   datetime     null comment '创建时间',
    UPDATE_TIME   datetime     null comment '修改时间'
)
    comment '接口字段映射表' row_format = DYNAMIC;

