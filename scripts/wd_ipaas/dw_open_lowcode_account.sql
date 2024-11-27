create table dw_open_lowcode_account
(
    id          int auto_increment comment '自增ID'
        primary key,
    app_id      int          null comment '应用ID',
    ep_id       int          null comment '租户ID',
    user_name   varchar(255) null comment '用户名',
    pwd         varchar(255) null comment '密码',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '最后更新时间',
    create_by   bigint       null comment '创建者',
    update_by   bigint       null comment '最后更新者',
    lessee_id   int          null comment '租户ID'
)
    comment '账号表';

create index idx_app_id
    on dw_open_lowcode_account (app_id);

