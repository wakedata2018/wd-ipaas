create table dw_open_connector_api_request_param
(
    id               bigint auto_increment comment '主键id'
        primary key,
    lessee_id        int               not null comment '租户id',
    connector_api_id bigint            not null comment '第三方apiId',
    asset_columns    varchar(50)       not null comment '参数名称',
    asset_data_type  varchar(32)       not null comment '数据类型',
    description      varchar(1000)     null comment '说明',
    required         tinyint default 0 null comment '是否必须',
    sample           varchar(2048)     null comment '示例值',
    create_by        varchar(100)      not null comment '创建人',
    create_time      datetime          not null comment '创建时间',
    update_by        varchar(100)      not null comment '更新人',
    update_time      datetime          not null comment '更新时间',
    http_param_kind  varchar(16)       not null comment '参数位置（head，query，body）',
    json_schema      text              null comment 'jsonschema类型数据样例'
)
    comment '第三方API请求参数';

create index idx_connector_api_id
    on dw_open_connector_api_request_param (connector_api_id);

