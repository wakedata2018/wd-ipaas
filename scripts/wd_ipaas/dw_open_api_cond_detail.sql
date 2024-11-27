create table dw_open_api_cond_detail
(
    id                      int auto_increment comment '主键id'
        primary key,
    rule_id                 int         null comment '规则id',
    condition_number        int         null comment '条件编号',
    date_source_id          varchar(60) null comment '数据来源节点',
    date_source_expression  varchar(60) null comment '数据来源表达式',
    sign_type               int         null comment '判断符号',
    value_type              int         null comment '取值类型',
    value_source_id         varchar(60) null comment '取值来源节点',
    value_source_expression varchar(60) null comment '取值来源表达式',
    create_time             datetime    null comment '创建时间',
    update_time             datetime    null comment '更新时间',
    lessee_id               bigint      null comment '租户id',
    create_by               varchar(20) null comment '创建人',
    update_by               varchar(20) null comment '更新人',
    constant_value          varchar(60) null comment '常量值'
);

