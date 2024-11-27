create table uaa_role_business_rel
(
    id               bigint auto_increment
        primary key,
    domain_id        bigint      null comment '域ID',
    ROLE_ID          varchar(36) not null comment '角色id',
    business_unit_id varchar(16) not null comment '业务单元id',
    create_by        varchar(20) null comment '创建人',
    create_time      datetime    null comment '创建时间',
    update_by        varchar(20) null comment '更新人',
    update_time      datetime    null comment '更新时间'
)
    comment '角色和业务单元关联关系表' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (1, 1, 'e2c1ec97-d29a-437c-b3ec-e4bd6c8601c9', '2063', null, '2024-08-07 14:55:54', null, '2024-08-07 14:55:54');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (2, 1, 'c09ae1ce-28af-47cc-8f8f-dd3f9b10ac58', '2063', null, '2024-08-07 15:18:54', null, '2024-08-07 15:18:54');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (3, 1, '506d97bf-c2ef-4e6e-8a0e-ab9abc2e4c87', '2063', null, '2024-08-27 17:58:03', null, '2024-08-27 17:58:03');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (4, 1, '881a185a-a0df-4587-ac9a-cc9669d57134', '2063', null, '2024-09-03 10:03:25', null, '2024-09-03 10:03:25');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (5, 1, 'ab071c7c-f6bc-4244-b5df-d049e8e020dc', '2063', null, '2024-09-04 10:10:46', null, '2024-09-04 10:10:46');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (6, 1, 'cce42593-0524-45e4-967a-feb4df277342', '2063', null, '2024-09-05 16:46:44', null, '2024-09-05 16:46:44');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (7, 1, 'b27693b3-d67c-4e4a-8ce6-3be958bb57fa', '2063', null, '2024-09-05 16:47:25', null, '2024-09-05 16:47:25');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (8, 1, 'eb8ca223-3b32-4144-bbaf-3d7bc6d7ba0c', '2063', null, '2024-09-05 17:09:27', null, '2024-09-05 17:09:27');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (9, 1, '6e156678-0379-4909-87e5-4805ce12f121', '2063', null, '2024-09-06 09:12:32', null, '2024-09-06 09:12:32');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (10, 1, '49a9be69-7ab6-4785-b6b1-5484a70268ad', '2063', null, '2024-09-06 09:12:49', null, '2024-09-06 09:12:49');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (11, 1, '082030f0-746e-435a-b434-2b5ec26bed40', '2063', null, '2024-09-13 14:40:34', null, '2024-09-13 14:40:34');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (12, 1, 'e56e5e22-02b4-42a6-8793-4d737f226422', '2063', null, '2024-10-18 16:08:17', null, '2024-10-18 16:08:17');
INSERT INTO wd_permission.uaa_role_business_rel (id, domain_id, ROLE_ID, business_unit_id, create_by, create_time, update_by, update_time) VALUES (13, 1, '03b678f2-854d-4566-9c62-f48f8e8132cc', '2063', null, '2024-10-30 17:53:24', null, '2024-10-30 17:53:24');
