create table uaa_sso_client
(
    id                    bigint auto_increment comment '主键id'
        primary key,
    client_id             varchar(50)             not null comment 'client id',
    client_secret         varchar(50)             not null comment '密钥',
    client_name           varchar(30)             not null comment '应用名称',
    client_logo           varchar(255)            not null comment '应用logo图片地址',
    client_desc           varchar(255) default '' not null comment '应用描述',
    redirect_url          varchar(255) default '' not null comment '回调url',
    login_url             varchar(255) default '' not null comment '单点登录url',
    id_token_ex_time      int          default 0  not null comment '授权码过期时长，单位：秒',
    access_token_ex_time  int          default 0  not null comment 'access token 过期时长，单位：秒',
    refresh_token_ex_time int          default 0  not null comment 'refresh token 过期时长，单位：秒',
    account_choose        tinyint(1)   default 0  not null comment '账号选择，0 不勾选，1 勾选',
    create_time           datetime                not null comment '创建时间',
    create_by             varchar(64)             not null comment '创建人',
    update_time           datetime                not null comment '更新时间',
    update_by             varchar(64)             not null comment '更新人',
    status                tinyint(1)   default 0  not null comment '状态，状态 0-不启用 1-启用'
);

