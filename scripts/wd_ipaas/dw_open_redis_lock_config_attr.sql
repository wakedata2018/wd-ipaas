create table dw_open_redis_lock_config_attr
(
    id                bigint auto_increment comment '主键，自增'
        primary key,
    lock_name         varchar(255)  null comment '锁的名称',
    lock_type         varchar(64)   null comment '锁类型，Reentrant：可重入锁、Fair：公平锁、Read：读锁、Write：写锁',
    key_prefix        varchar(255)  null comment '锁前缀',
    key_params        json          null comment '组成锁的请求参数名集合',
    wait_time         int           null comment '获取锁的最长等待时间，单位秒',
    lease_time        int           null comment '上锁后x秒自动解锁',
    description       varchar(1024) null comment '备注',
    lessee_id         bigint        not null comment '租户id',
    create_time       datetime      not null comment '创建时间',
    create_by         varchar(20)   not null comment '创建人',
    update_time       datetime      not null comment '更新时间',
    update_by         varchar(20)   not null comment '更新人',
    data_asset_api_id int           not null comment '关联api id',
    config_type       tinyint       not null comment '配置对应类型，0：数据表API、1：SQL API、2：SQL算子',
    is_enable         tinyint       not null comment '是否启用，0:不启用、1:启用'
);

create index idx_asset_api_id
    on dw_open_redis_lock_config_attr (data_asset_api_id);

