create table dw_open_api_cond_rule
(
    id               int auto_increment comment '规则ID'
        primary key,
    name             varchar(25)  null comment '规则名称',
    api_id           int          null comment '规则所属节点id',
    next_api_id      int          null comment '规则对应的节点id',
    lessee_id        int          null comment '租户ID',
    create_time      datetime     null comment '创建时间',
    update_time      datetime     null comment '更新时间',
    create_by        varchar(20)  null comment '创建人',
    update_by        varchar(20)  null comment '更新人',
    rule_expression  varchar(60)  null comment '规则表达式',
    from_operator_id varchar(100) null comment '规则所属算子id',
    to_operator_id   varchar(100) null comment '规则对应算子id'
);

