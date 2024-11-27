create table dw_open_access_auth
(
    id                 bigint auto_increment comment 'ID'
        primary key,
    lessee_id          bigint      null comment '租户id',
    data_access_app_id int         null comment '接入方应用id',
    type               tinyint     null comment '授权类型（惟客云应用，其他）',
    api_auth_config    json        null comment '授权配置，json串形式，如惟客云应用授权{“tenantId”,xxx,"appBuId":xxx}',
    status             tinyint     null comment '授权状态（已授权、已解绑）',
    create_by          varchar(64) null comment '创建人',
    create_time        datetime    null comment '创建时间',
    update_by          varchar(64) null comment '更新人',
    update_time        datetime    null comment '更新时间'
)
    comment '应用下授权绑定的关联信息;1、开放平台应用绑定的授权信息，如惟客云应用授权给开放平台后产生的映射数据';

create index idx_app_id_lessee_id
    on dw_open_access_auth (data_access_app_id, lessee_id);

INSERT INTO wd_ipaas.dw_open_access_auth (id, lessee_id, data_access_app_id, type, api_auth_config, status, create_by, create_time, update_by, update_time) VALUES (3, 3, 3, 0, '{"id": 2063, "appName": "集团", "tenantId": 467}', null, '13012345678', '2024-07-18 18:11:06', '13012345678', '2024-07-18 18:11:06');
INSERT INTO wd_ipaas.dw_open_access_auth (id, lessee_id, data_access_app_id, type, api_auth_config, status, create_by, create_time, update_by, update_time) VALUES (4, 3, 6, 0, '{"id": 2063, "appName": "苏高新会员云", "tenantId": 467}', null, '13012345678', '2024-11-19 10:20:35', '13012345678', '2024-11-19 10:20:35');
