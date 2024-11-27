create table dw_open_api_group
(
    id          int auto_increment comment '自增ID'
        primary key,
    group_name  varchar(64)  null comment 'API分组名称',
    group_code  varchar(255) null comment 'api分组编码',
    create_user varchar(64)  null comment '创建用户',
    group_path  varchar(128) null comment '分组路径',
    group_desc  varchar(128) null comment '分组描述',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '更新时间',
    lessee_id   int          null comment '租户ID',
    parent_id   int          not null comment '父id;',
    level       int          not null comment '层级;',
    constraint uk_group_code_lessee
        unique (lessee_id, group_code)
)
    comment 'api接口分组表;1、api分组表，用于对api分组管理';

INSERT INTO wd_ipaas.dw_open_api_group (id, group_name, group_code, create_user, group_path, group_desc, create_time, update_time, lessee_id, parent_id, level) VALUES (3, '组织架构', 'org', '演示账号', 'org', 'org', '2024-07-18 17:45:55', '2024-07-18 17:45:55', 3, 0, 0);
INSERT INTO wd_ipaas.dw_open_api_group (id, group_name, group_code, create_user, group_path, group_desc, create_time, update_time, lessee_id, parent_id, level) VALUES (4, '店铺', 'store', '演示账号', 'store', '店铺主数据', '2024-08-30 15:08:48', '2024-08-30 15:08:48', 3, 0, 0);
INSERT INTO wd_ipaas.dw_open_api_group (id, group_name, group_code, create_user, group_path, group_desc, create_time, update_time, lessee_id, parent_id, level) VALUES (5, '俱乐部会员', 'loyaltyV2', '演示账号', 'loyaltyV2', '', '2024-11-19 10:57:50', '2024-11-19 14:34:25', 3, 0, 0);
