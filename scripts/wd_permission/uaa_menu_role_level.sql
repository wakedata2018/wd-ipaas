create table uaa_menu_role_level
(
    id            bigint unsigned auto_increment comment 'id'
        primary key,
    menu_id       varchar(36)            not null comment '菜单id',
    role_level_id bigint                 not null comment '角色类型id',
    update_time   datetime               null comment '更新时间',
    create_time   datetime               null comment '创建时间',
    update_by     varchar(50) default '' null comment '更新人',
    create_by     varchar(50) default '' null comment '创建人'
)
    comment '菜单及角色类型映射表';

create index idx_menu_id
    on uaa_menu_role_level (menu_id);

INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (2, '830282bc-357c-4174-b00d-9706f8d818cb', 7, '2022-04-29 15:52:42', '2022-04-29 15:52:42', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (4, '91f0f520-200b-455d-98d6-744b3ea689fa', 25, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (5, '91f0f520-200b-455d-98d6-744b3ea689fa', 26, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (6, '91f0f520-200b-455d-98d6-744b3ea689fa', 27, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (7, '91f0f520-200b-455d-98d6-744b3ea689fa', 34, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (8, '91f0f520-200b-455d-98d6-744b3ea689fa', 35, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (9, '91f0f520-200b-455d-98d6-744b3ea689fa', 37, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (10, '91f0f520-200b-455d-98d6-744b3ea689fa', 36, '2022-05-18 11:43:03', '2022-05-18 11:43:03', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (14, 'd07e5681-6f5a-4734-b0ba-eb24e55f6218', 34, '2022-05-19 17:15:59', '2022-05-19 17:15:59', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (21, 'f592cf25-7fdb-46f1-8795-a763e7320bd0', 34, '2022-08-11 14:14:14', '2022-08-11 14:14:14', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (28, '1aa63946-f033-4c79-9482-adbc428a6036', 25, '2022-09-19 10:34:25', '2022-09-19 10:34:25', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (29, '1aa63946-f033-4c79-9482-adbc428a6036', 26, '2022-09-19 10:34:25', '2022-09-19 10:34:25', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (30, '1aa63946-f033-4c79-9482-adbc428a6036', 34, '2022-09-19 10:34:25', '2022-09-19 10:34:25', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (31, '1aa63946-f033-4c79-9482-adbc428a6036', 35, '2022-09-19 10:34:25', '2022-09-19 10:34:25', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (32, '1aa63946-f033-4c79-9482-adbc428a6036', 36, '2022-09-19 10:34:25', '2022-09-19 10:34:25', '', '');
INSERT INTO wd_permission.uaa_menu_role_level (id, menu_id, role_level_id, update_time, create_time, update_by, create_by) VALUES (33, '1aa63946-f033-4c79-9482-adbc428a6036', 37, '2022-09-19 10:34:25', '2022-09-19 10:34:25', '', '');
