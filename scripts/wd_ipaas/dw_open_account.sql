create table dw_open_account
(
    id                  bigint auto_increment comment 'ID'
        primary key,
    lessee_id           bigint       null comment '租户id',
    user_identification varchar(32)  null comment '用户账号标识',
    phone               varchar(64)  null comment '手机号',
    name                varchar(255) null comment '用户昵称',
    password            varchar(255) null comment '密码',
    status              tinyint      null comment '状态（启用、禁用）',
    relate_role_id      varchar(64)  null comment '关联角色id',
    is_admin            tinyint      null comment '是否是管理员',
    create_by           varchar(64)  null comment '创建人',
    create_time         datetime     null comment '创建时间',
    update_by           varchar(64)  null comment '更新人',
    update_time         datetime     null comment '更新时间',
    constraint uk_user_identification
        unique (user_identification)
)
    comment 'Ipaas账号;1、开发平台账号，主要用来登录开放平台，做应用和api等的管理，分两个角色，管理员和开发者';

create index idx_lessee_id
    on dw_open_account (lessee_id);

INSERT INTO wd_ipaas.dw_open_account (id, lessee_id, user_identification, phone, name, password, status, relate_role_id, is_admin, create_by, create_time, update_by, update_time) VALUES (3, 3, 'wakedata', null, '演示账号', '68b8f4b6c688af7c7384dd19093f7491', 1, 'dc27f595-12d2-4054-9b88-79148d515406', 1, null, '2022-10-26 16:54:46', null, '2024-07-16 19:54:57');
