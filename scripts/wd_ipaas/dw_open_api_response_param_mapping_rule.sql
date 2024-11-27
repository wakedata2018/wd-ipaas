create table dw_open_api_response_param_mapping_rule
(
    id                                 int auto_increment comment '主键id'
        primary key,
    lessee_id                          int               not null comment '租户ID',
    resp_param_mapping_rule_name       varchar(100)      not null comment '响应参数映射规则名称',
    resp_param_mapping_rule_jsonschema text              not null comment '参数映射规则结构体，JSONSchema格式',
    resp_param_mapping_rule            text              not null comment '响应体参数映射规则',
    status                             tinyint default 0 not null comment '启用状态；0-未启用，1-已启用',
    description                        varchar(255)      null comment '描述',
    is_default_rule                    tinyint default 0 not null comment '是否是默认的映射规则，0-不是，1-是',
    create_by                          varchar(100)      not null comment '创建人',
    update_by                          varchar(100)      not null comment '更新人',
    create_time                        datetime          not null comment '创建时间',
    update_time                        datetime          not null comment '更新时间;',
    constraint uk_lessee_id_resp_param_mapping_rule_name
        unique (lessee_id, resp_param_mapping_rule_name)
)
    comment 'api响应体参数映射规则表，存储api响应体与ipaas标准响应体之间的参数映射关系';

INSERT INTO wd_ipaas.dw_open_api_response_param_mapping_rule (id, lessee_id, resp_param_mapping_rule_name, resp_param_mapping_rule_jsonschema, resp_param_mapping_rule, status, description, is_default_rule, create_by, update_by, create_time, update_time) VALUES (1, 3, '惟客云参数映射规则', '{
    "root": {
        "type": "object",
        "name": "root",
        "description": "根层级",
        "properties": {
            "field_0.42550507786696357": {
                "name": "code",
                "disabled": false,
                "type": "integer",
                "value": "msg"
            },
            "field_0.001045328631537501": {
                "type": "string",
                "name": "msg",
                "disabled": false,
                "value": "msg"
            },
            "field_0.736996662678169": {
                "name": "data",
                "disabled": false,
                "type": "object",
                "value": "data",
                "properties": {}
            },
            "field_0.22190552939899955": {
                "type": "string",
                "name": "success",
                "disabled": false,
                "value": "success"
            },
            "field_0.9118576750326601": {
                "name": "pageNo",
                "disabled": false,
                "type": "integer",
                "value": "pageNo"
            },
            "field_0.6819363421539193": {
                "name": "pageSize",
                "disabled": false,
                "type": "integer",
                "value": "pageSize"
            },
            "field_0.5478981898414432": {
                "name": "totalCount",
                "disabled": false,
                "type": "long",
                "value": "totalCount"
            }
        }
    }
}', '{
    "code": "",
    "msg": {"code":"$.code","msg":"$.msg"},
    "data": "$.data",
    "success": "$.success",
    "pageNo": "$.pageNo",
    "pageSize": "$.pageSize",
    "totalCount": "$.totalCount"
}', 1, '惟客云响应体参数映射规则', 1, 'testadmin1', 'testadmin1', '2023-03-06 16:03:28', '2023-03-12 16:03:28');
