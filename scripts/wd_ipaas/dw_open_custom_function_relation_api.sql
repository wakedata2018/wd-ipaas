create table dw_open_custom_function_relation_api
(
    id                bigint unsigned auto_increment comment 'id'
        primary key,
    lessee_id         bigint      null comment '租户id',
    custom_func_name  varchar(32) null comment '自定义函数名称',
    data_asset_api_id bigint      null comment '关联apiId',
    create_by         varchar(64) null comment '创建人',
    create_time       datetime    null comment '创建时间',
    update_by         varchar(64) null comment '更新人',
    update_time       datetime    null comment '更新时间'
)
    comment '自定义函数关联api记录表';

create index idx_func_name_lessee_id
    on dw_open_custom_function_relation_api (custom_func_name, lessee_id);

