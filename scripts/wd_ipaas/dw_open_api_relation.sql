create table dw_open_api_relation
(
    id       bigint auto_increment
        primary key,
    api_id   bigint not null comment 'api;id',
    graph_id bigint not null comment 'graph_id'
)
    comment '流程编排信息';

