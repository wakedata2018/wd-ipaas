create table uaa_ip_black_white_list
(
    ID          bigint auto_increment comment '自增ID'
        primary key,
    TENANT_ID   bigint      null comment '租户ID',
    APP_BU_ID   bigint      null comment '应用业务单元ID',
    RULE_NAME   varchar(40) not null comment '规则名',
    FROM_IP     varchar(15) not null comment '起始IP',
    TO_IP       varchar(15) not null comment '结束IP',
    IP_TYPE     tinyint     not null comment 'IP类型 0黑名单 1白名单',
    IP_STATUS   tinyint     not null comment 'IP状态 0禁用 1启用',
    MODIFIED_BY varchar(25) not null comment '修改人',
    CREATE_TIME datetime    not null comment '创建时间',
    UPDATE_TIME datetime    not null comment '更新时间'
)
    comment 'ip黑白名单配置表' row_format = DYNAMIC;

create index IDX_TENANT_APP_BU_ID
    on uaa_ip_black_white_list (TENANT_ID, APP_BU_ID);

