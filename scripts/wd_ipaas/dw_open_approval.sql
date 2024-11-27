create table dw_open_approval
(
    approval_id            int auto_increment comment '自增主键'
        primary key,
    user_identification    varchar(255)  not null comment '创建审批单的用户标识，申请人',
    approval_subject       varchar(255)  not null comment '审批主体',
    approval_business_type tinyint       not null comment '审批的业务',
    approval_body          text          not null comment '审批内容',
    approval_status        tinyint       not null comment '审批状态(0;新建 1 审批中 2 审批通过 3 审批失败)',
    approval_message       varchar(2048) null comment '审批信息',
    update_time            datetime      not null comment '更新时间;',
    create_time            datetime      not null comment '创建时间;',
    data_asset_api_id      int           null comment '数据资产ID',
    data_access_app_id     int           null comment '接入应用ID',
    form_url               varchar(1024) null comment 'K2表单地址',
    lessee_id              int           null comment '租户ID',
    app_auth_status        int default 0 null comment '应用授权状态，0：未授权，1：已授权',
    active_status          int default 0 null comment '有效状态，0：无效，1：有效'
)
    comment 'api审批表;1、api授权审批表，也用于应用绑定授权api';

create index idx_access_app_id_lessee_id
    on dw_open_approval (data_access_app_id, lessee_id);

create index idx_data_asset_api_id
    on dw_open_approval (data_asset_api_id);

INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (27, '13012345678', '15', 3, '授权', 2, null, '2024-07-23 12:01:37', '2024-07-18 18:11:39', 15, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (28, '13012345678', '16', 3, '授权', 2, null, '2024-07-23 12:01:36', '2024-07-22 15:36:56', 16, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (29, '13012345678', '17', 3, '授权', 2, null, '2024-07-23 12:01:34', '2024-07-22 15:36:57', 17, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (30, '13012345678', '17', 3, '授权', 2, null, '2024-07-23 12:01:34', '2024-07-23 11:26:21', 17, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (31, '13012345678', '16', 3, '授权', 2, null, '2024-07-23 12:01:36', '2024-07-23 11:26:22', 16, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (32, '13012345678', '15', 3, '授权', 2, null, '2024-07-23 12:01:37', '2024-07-23 11:26:23', 15, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (33, '13012345678', '17', 3, '授权', 2, null, '2024-07-23 12:01:34', '2024-07-23 11:55:48', 17, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (34, '13012345678', '16', 3, '授权', 2, null, '2024-07-23 12:01:36', '2024-07-23 11:55:49', 16, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (35, '13012345678', '15', 3, '授权', 2, null, '2024-07-23 12:01:37', '2024-07-23 11:55:49', 15, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (36, '13012345678', '20', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-07-23 12:05:43', 20, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (37, '13012345678', '19', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-07-23 12:05:43', 19, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (38, '13012345678', '18', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-07-23 12:05:44', 18, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (39, '13012345678', '20', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-07-24 19:03:43', 20, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (40, '13012345678', '19', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-07-24 19:03:43', 19, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (41, '13012345678', '18', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-07-24 19:03:44', 18, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (42, '13012345678', '20', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-08-12 10:37:55', 20, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (43, '13012345678', '20', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-08-16 11:24:12', 20, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (44, '13012345678', '22', 3, '授权', 2, null, '2024-11-19 14:40:43', '2024-08-30 15:27:52', 22, 3, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (45, '13012345678', '23', 3, '授权', 2, null, '2024-11-19 14:33:00', '2024-11-19 11:44:39', 23, 6, null, 3, 1, 0);
INSERT INTO wd_ipaas.dw_open_approval (approval_id, user_identification, approval_subject, approval_business_type, approval_body, approval_status, approval_message, update_time, create_time, data_asset_api_id, data_access_app_id, form_url, lessee_id, app_auth_status, active_status) VALUES (46, '13012345678', '23', 3, '授权', 2, null, '2024-11-19 14:34:54', '2024-11-19 14:34:54', 23, 6, null, 3, 1, 1);
