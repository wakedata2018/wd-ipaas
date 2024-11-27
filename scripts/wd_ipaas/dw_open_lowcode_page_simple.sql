create table dw_open_lowcode_page_simple
(
    id          int auto_increment comment '自增ID'
        primary key,
    ep_id       int           null comment '租户ID',
    app_id      int           null comment '应用ID',
    category_id int           null comment '分类id',
    name        varchar(255)  not null comment '标识符,;必须唯一',
    title       varchar(255)  not null comment '标题',
    params      json          null comment '页面参数声明，JSON;字符串',
    cover       varchar(5000) null comment '封面',
    create_time datetime      null comment '创建时间',
    update_time datetime      null comment '最后更新时间',
    create_by   bigint        null comment '创建者',
    update_by   bigint        null comment '最后更新者'
)
    comment '基础页面';

create index idx_app_id
    on dw_open_lowcode_page_simple (app_id);

