create table dw_open_api_liteflow_chain
(
    id                bigint unsigned auto_increment comment 'id'
        primary key,
    data_asset_api_id bigint   not null comment '数据apiId',
    chain             text     null comment '执行链',
    create_time       datetime null comment '创建时间',
    update_time       datetime null comment '更新时间',
    constraint uk_idx_data_asset_api_id
        unique (data_asset_api_id)
)
    comment 'api编排执行链';

