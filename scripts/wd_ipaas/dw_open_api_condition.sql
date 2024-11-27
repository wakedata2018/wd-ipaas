create table dw_open_api_condition
(
    id                   int auto_increment comment '自增ID'
        primary key,
    data_asset_api_id    int                         null comment '数据资产ID',
    asset_columns        varchar(100)                null comment '对应列',
    asset_datatype       varchar(32)                 null comment '数据类型',
    asset_columns_length int                         null comment '长度',
    descriptions         varchar(255)                null comment '描述',
    required             tinyint     default 0       null comment '是否必须',
    sample               text                        null comment '样例',
    create_time          datetime                    null comment '创建时间',
    update_time          datetime                    null comment '更新时间',
    type                 int                         null comment '数据类型',
    multi_value          tinyint     default 0       null comment '是否支持多值',
    param_kind           varchar(16) default 'QUERY' null comment '参数类型',
    type_attr            tinyint     default 0       null comment '入参属性;分为1:filter和0:operator',
    json_schema          text                        null comment 'jsonSchema字符串',
    is_schema            tinyint     default 0       null comment '是否为schema格式：0不是 1是',
    allow_empty          tinyint     default 0       null comment '是否允许为空 0否 1是'
)
    comment 'API访问参数;1、api信息对应的访问参数配置';

create index idx_asset_api_id
    on dw_open_api_condition (data_asset_api_id);

INSERT INTO wd_ipaas.dw_open_api_condition (id, data_asset_api_id, asset_columns, asset_datatype, asset_columns_length, descriptions, required, sample, create_time, update_time, type, multi_value, param_kind, type_attr, json_schema, is_schema, allow_empty) VALUES (80, 22, 'body', 'json', null, '请求体参数', 0, '{"appBuId":467,"buType":"1","pageNo":1,"pageSize":10,"parentId":2066,"tenantId":267}', '2024-08-30 15:27:15', '2024-08-30 15:27:15', 1, null, 'BODY', 0, '{"root":{"type":"object","properties":{"appBuId":{"type":"integer","format":"int64","description":"应用id","name":"appBuId","value":"467"},"buType":{"type":"string","description":"业务单元类型 0-应用,1-店铺","name":"buType","value":"1"},"pageNo":{"type":"integer","format":"int32","example":1,"description":"页码(不能为空)","name":"pageNo","value":"1"},"pageSize":{"type":"integer","format":"int32","example":10,"description":"每页数量(不能为空)","maximum":200,"exclusiveMaximum":false,"name":"pageSize","value":"10"},"parentId":{"type":"integer","format":"int64","description":"所属项目ID，如南京华贸测试2066","name":"parentId","value":"2066"},"tenantId":{"type":"integer","format":"int64","description":"租户id，如南京华贸测试267","name":"tenantId","value":"267"}},"title":"BusinessUnitQuery","description":"业务单元分页查询","father_node":["BusinessUnitQuery"],"father_node_name":"BusinessUnitQuery","name":"root"}}', null, null);
INSERT INTO wd_ipaas.dw_open_api_condition (id, data_asset_api_id, asset_columns, asset_datatype, asset_columns_length, descriptions, required, sample, create_time, update_time, type, multi_value, param_kind, type_attr, json_schema, is_schema, allow_empty) VALUES (81, 22, '__ALL__', null, null, '所有信息', 0, null, '2024-08-30 15:27:15', '2024-08-30 15:27:15', 2, 0, 'QUERY', null, null, null, null);
INSERT INTO wd_ipaas.dw_open_api_condition (id, data_asset_api_id, asset_columns, asset_datatype, asset_columns_length, descriptions, required, sample, create_time, update_time, type, multi_value, param_kind, type_attr, json_schema, is_schema, allow_empty) VALUES (102, 23, 'body', 'json', null, '请求体参数', 0, '{"uniqueAccountId":"2411181929427747757","clubId":2}', '2024-11-19 14:34:12', '2024-11-19 14:34:12', 1, null, 'BODY', 0, '{"root":{"type":"object","name":"root","description":"根层级","properties":{"field_0.4449577397091755":{"type":"string","name":"uniqueAccountId","description":"会员一账通id","value":"2411181929427747757"},"field_0.21396020291029183":{"name":"clubId","type":"integer","description":"俱乐部id","value":"2"}},"rootRequired":false}}', null, null);
INSERT INTO wd_ipaas.dw_open_api_condition (id, data_asset_api_id, asset_columns, asset_datatype, asset_columns_length, descriptions, required, sample, create_time, update_time, type, multi_value, param_kind, type_attr, json_schema, is_schema, allow_empty) VALUES (103, 23, '__ALL__', null, null, '所有信息', 0, null, '2024-11-19 14:34:12', '2024-11-19 14:34:12', 2, 0, 'QUERY', null, null, null, null);
