create table dw_open_api_generate_auth_token
(
    id                        bigint auto_increment comment 'ID'
        primary key,
    lessee_id                 int         null comment '租户id',
    data_access_app_id        int         null comment '接入方应用id',
    access_token              varchar(64) null comment '生成的token令牌',
    token_expire_time         datetime    null comment 'token有效期',
    refresh_token             varchar(64) null comment '刷新token的令牌',
    refresh_token_expire_time datetime    null comment '刷新令牌有效期',
    refresh_num               int         null comment '刷新次数',
    status                    tinyint     null comment '状态（有效、作废）',
    create_by                 varchar(64) null comment '创建人',
    create_time               datetime    null comment '创建时间',
    update_by                 varchar(64) null comment '更新人',
    update_time               datetime    null comment '更新时间'
)
    comment '开放平台生成的授权访问api令牌;1、开放平台应用访问api前生成的accessToken';

create index idx_access_app_id_lessee_id
    on dw_open_api_generate_auth_token (data_access_app_id, lessee_id);

INSERT INTO wd_ipaas.dw_open_api_generate_auth_token (id, lessee_id, data_access_app_id, access_token, token_expire_time, refresh_token, refresh_token_expire_time, refresh_num, status, create_by, create_time, update_by, update_time) VALUES (2, 3, 3, 'atD82819072EA32AA63ECC59FCB91BD322', '2024-10-27 13:04:27', 'rtD82819072EA32AA63ECC59FCB91BD322', '2024-11-26 11:04:27', 0, null, null, '2024-07-23 09:20:22', null, '2024-10-27 11:04:27');
