create table dw_open_api_graph_attr
(
    id                bigint auto_increment
        primary key,
    data_asset_api_id bigint     not null comment 'data_asset_api_id',
    dag_json          mediumtext null comment '算子逻辑视图：组件、算子、边',
    location_json     text       not null comment '算子物理视图：组件;算子等位置信息'
)
    comment '流程编排信息';

