create table dw_open_collect
(
    id                 int auto_increment comment '自增ID'
        primary key,
    username           bigint   null comment '用户名',
    data_asset_api_id  int      null comment '数据资产ID',
    create_time        datetime null comment '创建时间',
    update_time        datetime null comment '更新时间',
    data_access_app_id int      null comment '接入应用id'
)
    comment 'api收藏配置表;1、api收藏记录表';

create index idx_access_app_id_asset_api_id
    on dw_open_collect (data_access_app_id, data_asset_api_id);

