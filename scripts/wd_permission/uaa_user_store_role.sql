create table uaa_user_store_role
(
    ID          bigint auto_increment comment '主键'
        primary key,
    APP_ID      bigint   not null comment '品牌id',
    USER_ID     bigint   not null comment '用户id',
    ROLE_ID     bigint   not null comment '角色id',
    STORE_ID    bigint   not null comment '门店id',
    UPDATE_TIME datetime null comment '更新时间',
    CREATE_TIME datetime null comment '创建时间',
    EP_ID       bigint   not null comment '企业id',
    constraint uniq_user_store_role
        unique (USER_ID, STORE_ID, ROLE_ID)
)
    comment '门店角色绑定表' collate = utf8mb4_unicode_ci
                             row_format = DYNAMIC;

