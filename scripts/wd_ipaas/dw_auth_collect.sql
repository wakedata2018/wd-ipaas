create table dw_auth_collect
(
    id                int auto_increment comment 'id;唯一标识'
        primary key,
    dw_auth_info_id   int         not null comment 'dw_auth_info表id',
    data_asset_api_id int         not null comment '数据资产apiId',
    create_time       datetime    not null comment '创建时间',
    update_time       datetime    not null comment '更新时间',
    create_by         varchar(20) not null comment '创建人'
)
    comment 'api内部鉴权关联表;1、api内部鉴权映射表，用于实现api内部鉴权时使用对应的鉴权模板配置';

create index idx_asset_api_id
    on dw_auth_collect (data_asset_api_id);

