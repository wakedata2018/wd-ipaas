create table dw_open_app_access
(
    data_access_app_id      int auto_increment comment '自增主键'
        primary key,
    data_access_app_name    varchar(255)      not null comment '数据访问客户端',
    data_access_key         varchar(255)      null comment '数据访问key;',
    data_access_secret      varchar(255)      null comment '数据访问密钥',
    data_access_description varchar(255)      null comment '接入端描述',
    update_time             datetime          not null comment '更新时间;',
    create_time             datetime          not null comment '创建时间;',
    in_charge               bigint            null comment '负责人',
    username                bigint            null comment '用户名',
    status                  int     default 0 null comment '状态',
    approval_message        varchar(255)      null comment '审批意见',
    data_access_auth_type   int               null comment '鉴权方式 0：app鉴权 1：token鉴权 2：连接器鉴权',
    lessee_id               int               null comment '租户ID',
    app_type                tinyint default 0 null comment '应用类型[0-ipaas应用;;1-低代码应用]',
    low_code_logo           varchar(256)      null comment '低代码应用logo',
    publish_status          int               null comment '应用发布状态 0:上线，1：下线',
    connector_auth_id       bigint            null comment '鉴权配置id',
    data_access_prefix      varchar(64)       null comment '应用访问前缀,自定义输入部分 +"."+ 租户id(租户id使用hashids编码)',
    constraint uk_lessee_id_access_prefix
        unique (lessee_id, data_access_prefix)
)
    comment '接入应用配置表;1、开放平台应用配置表，主要给外部系统调用api时使用';

create index idx_lessee_id
    on dw_open_app_access (lessee_id);

INSERT INTO wd_ipaas.dw_open_app_access (data_access_app_id, data_access_app_name, data_access_key, data_access_secret, data_access_description, update_time, create_time, in_charge, username, status, approval_message, data_access_auth_type, lessee_id, app_type, low_code_logo, publish_status, connector_auth_id, data_access_prefix) VALUES (4, '默认应用', 'f2c4340a220647519f0e2432b6719906', 'd041c1e0027f4730a8c3315b7efe2d00', '', '2024-07-18 18:42:47', '2022-08-09 15:22:30', null, null, 1, '管理员添加，审批通过', 1, 3, 0, null, 0, 1, 'p9vlR4.lp9vlR48');
