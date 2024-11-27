create table dw_open_lowcode_page_detail
(
    id                 int auto_increment comment '自增ID'
        primary key,
    simple_id          int      not null comment 'dw_open_lowcode_page_simple自增id',
    content            json     null comment '组件详情',
    compressed_content json     null comment '压缩版本组件详情',
    create_time        datetime null comment '创建时间',
    update_time        datetime null comment '最后更新时间',
    create_by          bigint   null comment '创建者',
    update_by          bigint   null comment '最后更新者'
)
    comment '页面详情';

