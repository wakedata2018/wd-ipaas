create table dw_open_api_rule
(
    id                int auto_increment comment 'id'
        primary key,
    rule_name         varchar(50)        not null comment '规则名称',
    rule_desc         varchar(255)       null comment '规则描述',
    data_asset_api_id int                not null comment '数据资产id;ID',
    day_limit         int                null comment '日调用次数',
    month_limit       int                null comment '月调用次数',
    total_limit       int                null comment '总调用次数',
    create_time       datetime           null comment '创建时间',
    update_time       datetime           null comment '更新时间',
    qps               bigint default 100 null comment '每秒最大访问次数',
    ttl               bigint default 0   null comment 'api缓存失效时间'
)
    comment 'API访问流量控制配置表;1、限制api的调用次数';

