create table dw_auth_info
(
    id               int auto_increment
        primary key,
    app_name         varchar(30)   not null comment '应用名称',
    app_logo         text          null comment 'applogo',
    app_type         varchar(50)   null comment '类型',
    configs          text          not null comment '配置',
    description      varchar(200)  not null comment '描述',
    create_user      varchar(20)   null comment '创建者',
    create_time      datetime      not null comment '创建时间',
    update_time      datetime      not null comment '更新时间',
    api_num          int           null comment '外部授权的api数量',
    lessee_id        int           null comment '租户ID',
    authorizationapi varchar(1000) null comment '认证信息'
)
    comment '接入第三方授权管理;1、主要用于api内部鉴权方式配置';

create index idx_lessee_Id
    on dw_auth_info (lessee_id);

