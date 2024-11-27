create table dw_open_lowcode_page_category
(
    id          int auto_increment comment '自增ID'
        primary key,
    app_id      int          null comment '应用ID',
    ep_id       int          null comment '租户ID',
    name        varchar(255) null comment '分类名称',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '最后更新时间',
    create_by   bigint       null comment '创建者',
    update_by   bigint       null comment '最后更新者'
)
    comment '页面分类';

create index idx_app_id
    on dw_open_lowcode_page_category (app_id);

