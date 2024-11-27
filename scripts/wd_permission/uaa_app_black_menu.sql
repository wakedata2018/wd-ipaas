create table uaa_app_black_menu
(
    ID          bigint auto_increment comment '自增id'
        primary key,
    EP_ID       bigint   null comment '企业id',
    APP_ID      bigint   null comment 'app id',
    MENU_ID     bigint   null comment '菜单id',
    CREATE_TIME datetime null comment '创建时间'
)
    comment '品牌菜单黑名单' row_format = DYNAMIC;

