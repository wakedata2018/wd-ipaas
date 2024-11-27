create table dw_open_connector_params
(
    id            bigint auto_increment comment '主键id'
        primary key,
    lessee_id     int               not null comment '租户id',
    connector_id  bigint            not null comment '平台id',
    param_name    varchar(50)       not null comment '参数名称',
    param_type    varchar(32)       not null comment '参数类型',
    is_required   tinyint default 0 null comment '是否必须',
    hidden_type   tinyint default 0 null comment '展示脱敏类型 0：不脱敏 1：中间部分脱敏',
    default_value varchar(1000)     null comment '示例值',
    description   varchar(1000)     null comment '描述',
    create_by     varchar(100)      not null comment '创建人',
    create_time   datetime          not null comment '创建时间',
    update_by     varchar(100)      not null comment '更新人',
    update_time   datetime          not null comment '更新时间',
    constraint uk_dw_open_connector_params_connector_id_param_name
        unique (connector_id, param_name)
)
    comment '鉴权字段表';

