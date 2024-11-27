create table uaa_group
(
    ID          bigint auto_increment
        primary key,
    DOMAIN_ID   bigint      null comment '域ID',
    NAME        varchar(45) null,
    PARENT_ID   bigint      null comment '父用户组ID',
    STATUS      tinyint(1)  null comment '状态',
    UPDATE_TIME datetime    null comment '更新时间',
    CREATE_TIME datetime    null comment '创建时间',
    constraint UNI_DOMAIN_NAME
        unique (DOMAIN_ID, NAME)
)
    row_format = DYNAMIC;

