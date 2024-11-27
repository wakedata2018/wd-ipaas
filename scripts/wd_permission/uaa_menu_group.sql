create table uaa_menu_group
(
    id               varchar(36)  not null comment '主键'
        primary key,
    group_name       varchar(64)  not null comment '应用分类名称',
    create_by        varchar(64)  null comment '创建人',
    update_by        varchar(64)  null comment '更新人',
    create_time      datetime     null comment '创建时间',
    update_time      datetime     null comment '更新时间',
    group_desc       varchar(255) null comment '应用分类描述',
    group_identifier varchar(255) null comment '终端唯一标识'
);

INSERT INTO wd_permission.uaa_menu_group (id, group_name, create_by, update_by, create_time, update_time, group_desc, group_identifier) VALUES ('22', '惟客云B端', '0', '211', '2022-05-20 19:24:58', '2022-06-01 22:01:30', '惟客云系统B端菜单', 'groupWeb');
INSERT INTO wd_permission.uaa_menu_group (id, group_name, create_by, update_by, create_time, update_time, group_desc, group_identifier) VALUES ('23', '微信小程序', '0', '0', '2022-05-17 09:18:46', '2022-05-19 09:56:45', '', 'groupWx');
INSERT INTO wd_permission.uaa_menu_group (id, group_name, create_by, update_by, create_time, update_time, group_desc, group_identifier) VALUES ('24', '企微员工端', '27', '0', '2022-05-18 10:44:28', '2022-05-19 09:57:04', '', 'groupQwx');
INSERT INTO wd_permission.uaa_menu_group (id, group_name, create_by, update_by, create_time, update_time, group_desc, group_identifier) VALUES ('26', 'App端', '0', '0', '2022-05-19 17:32:31', '2022-07-18 23:03:46', '', 'groupApp');
INSERT INTO wd_permission.uaa_menu_group (id, group_name, create_by, update_by, create_time, update_time, group_desc, group_identifier) VALUES ('b2b44822e3550405c8c7eff32696c2c1', '商户小程序', '0', '0', '2024-09-04 20:51:56', '2024-09-04 20:51:56', '', 'groupMerchantWx');
