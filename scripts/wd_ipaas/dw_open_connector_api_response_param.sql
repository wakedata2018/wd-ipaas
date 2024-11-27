create table dw_open_connector_api_response_param
(
    id                 bigint auto_increment comment '主键id'
        primary key,
    lessee_id          int           not null comment '租户id',
    connector_api_id   bigint        not null comment '第三方apiId',
    asset_columns      varchar(50)   not null comment '列名称',
    asset_data_type    varchar(32)   not null comment '列数据类型[如string]',
    description        varchar(1000) null comment '字段描述',
    param_kind         varchar(16)   not null comment '参数位置（head，query，body）',
    create_time        datetime      not null comment '创建时间',
    update_time        datetime      not null comment '最后更新时间',
    create_by          varchar(32)   not null comment '创建者',
    update_by          varchar(32)   not null comment '最后更新者',
    sample             varchar(1000) null comment '示例值',
    response_post_data text          null comment '响应体内容(jsonSchema或xml)'
)
    comment '第三方api响应参数表';

create index idx_dw_open_connector_api_response_param_connector_api_id
    on dw_open_connector_api_response_param (connector_api_id);

