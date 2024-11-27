create table uaa_role_scope_ral
(
    id          bigint auto_increment comment '主键id'
        primary key,
    ROLE_ID     varchar(36) not null comment '角色id',
    scope_id    bigint      not null comment '业务域id',
    create_by   varchar(32) null comment '创建人',
    create_time datetime    null comment '创建时间',
    update_by   varchar(32) null comment '更新人',
    update_time datetime    null comment '更新时间'
)
    comment '角色业务域关联表' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (18, '221', 111, 'admin', '2022-01-27 17:56:02', 'admin', '2022-01-27 17:56:02');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (135, 'c0ad24dc-4617-41de-834c-d7ecf9eb9614', 111, null, '2022-02-28 14:43:58', null, '2022-02-28 14:43:58');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (309, 'dc27f595-12d2-4054-9b88-79148d515406', 126, null, '2022-08-12 18:05:15', null, '2022-08-12 18:05:15');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (310, 'dcf434eb-65b8-406e-80e0-df7758b59501', 126, null, '2022-08-12 18:05:19', null, '2022-08-12 18:05:19');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (421, '210', 111, null, '2023-05-08 16:25:55', null, '2023-05-08 16:25:55');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (428, 'fbfd48ba-6565-4543-bb78-bf2554ead151', 111, null, '2023-05-08 16:26:38', null, '2023-05-08 16:26:38');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (430, 'e2c1ec97-d29a-437c-b3ec-e4bd6c8601c9', 111, null, '2024-08-07 14:55:54', null, '2024-08-07 14:55:54');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (431, 'c09ae1ce-28af-47cc-8f8f-dd3f9b10ac58', 111, null, '2024-08-07 15:18:54', null, '2024-08-07 15:18:54');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (435, 'cb0eadca-4b38-40fd-8326-639ad4727db9', 111, null, '2024-08-08 09:46:32', null, '2024-08-08 09:46:32');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (438, 'c2eb0303-fad5-4b52-b536-3d0b49a6a419', 111, null, '2024-08-20 10:41:45', null, '2024-08-20 10:41:45');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (439, '506d97bf-c2ef-4e6e-8a0e-ab9abc2e4c87', 111, null, '2024-08-27 17:58:03', null, '2024-08-27 17:58:03');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (440, '881a185a-a0df-4587-ac9a-cc9669d57134', 111, null, '2024-09-03 10:03:25', null, '2024-09-03 10:03:25');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (441, 'ab071c7c-f6bc-4244-b5df-d049e8e020dc', 111, null, '2024-09-04 10:10:46', null, '2024-09-04 10:10:46');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (442, 'cce42593-0524-45e4-967a-feb4df277342', 111, null, '2024-09-05 16:46:44', null, '2024-09-05 16:46:44');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (443, 'b27693b3-d67c-4e4a-8ce6-3be958bb57fa', 111, null, '2024-09-05 16:47:25', null, '2024-09-05 16:47:25');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (444, 'eb8ca223-3b32-4144-bbaf-3d7bc6d7ba0c', 111, null, '2024-09-05 17:09:27', null, '2024-09-05 17:09:27');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (445, '6e156678-0379-4909-87e5-4805ce12f121', 111, null, '2024-09-06 09:12:32', null, '2024-09-06 09:12:32');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (446, '49a9be69-7ab6-4785-b6b1-5484a70268ad', 111, null, '2024-09-06 09:12:49', null, '2024-09-06 09:12:49');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (447, '082030f0-746e-435a-b434-2b5ec26bed40', 111, null, '2024-09-13 14:40:34', null, '2024-09-13 14:40:34');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (448, 'e56e5e22-02b4-42a6-8793-4d737f226422', 111, null, '2024-10-18 16:08:17', null, '2024-10-18 16:08:17');
INSERT INTO wd_permission.uaa_role_scope_ral (id, ROLE_ID, scope_id, create_by, create_time, update_by, update_time) VALUES (449, '03b678f2-854d-4566-9c62-f48f8e8132cc', 111, null, '2024-10-30 17:53:24', null, '2024-10-30 17:53:24');
