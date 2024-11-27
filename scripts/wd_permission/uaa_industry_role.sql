create table uaa_industry_role
(
    ID          bigint auto_increment comment '主键'
        primary key,
    ROLE_ID     varchar(36) not null comment '角色id',
    INDUSTRY_ID bigint      not null comment '行业id  0为行业通用',
    UPDATE_TIME datetime    null comment '更新时间',
    CREATE_TIME datetime    null comment '创建时间',
    constraint uniq_role_industry
        unique (ROLE_ID, INDUSTRY_ID)
)
    comment '行业职位表' collate = utf8mb4_unicode_ci
                         row_format = DYNAMIC;

