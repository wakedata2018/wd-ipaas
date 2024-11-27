create table uaa_component
(
    ID          bigint auto_increment comment '自增主键'
        primary key,
    DOMAIN_ID   bigint      not null comment '域ID',
    SOURCE      tinyint(1)  not null comment '来源（1-WKBPC）',
    INDUSTRY    tinyint(1)  null comment '行业（1-美业，2-零售）',
    NAME        varchar(64) not null comment '组件名',
    TYPE        tinyint     not null comment '组件类型',
    STATUS      tinyint(1)  not null comment '状态',
    UPDATE_TIME datetime    not null comment '更新时间',
    CREATE_TIME datetime    not null comment '创建时间'
)
    row_format = DYNAMIC;

