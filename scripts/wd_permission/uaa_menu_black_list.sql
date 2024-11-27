create table uaa_menu_black_list
(
    ID          bigint auto_increment comment '自增id'
        primary key,
    MENU_ID     varchar(36) not null comment '菜单id',
    SCOPE_ID    bigint      null comment '业务域id',
    CREATE_TIME datetime    null comment '创建时间'
)
    comment '菜单黑名单' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

