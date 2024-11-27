create table dw_open_app_access_rule
(
    data_access_rule_id   int auto_increment comment '自增ID'
        primary key,
    data_asset_api_id     int          not null comment '数据资产apiId',
    data_access_app_id    int          not null comment '接入应用id;访问数据客户端标识',
    data_asset_field_name varchar(255) not null comment '所属数据源下表的列名称',
    update_time           datetime     not null comment '更新时间;',
    create_time           datetime     not null comment '创建时间;'
)
    comment '数据访问规则;1、api在应用下的数据访问规则';

create index idx_access_app_id_asset_api_id
    on dw_open_app_access_rule (data_access_app_id, data_asset_api_id);

