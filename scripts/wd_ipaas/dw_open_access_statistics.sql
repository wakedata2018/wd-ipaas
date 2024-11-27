create table dw_open_access_statistics
(
    id                 int auto_increment comment '自增ID'
        primary key,
    stat_type          int        null comment '统计类型',
    record_data        int        null comment '统计的数据',
    record_entity_id   int        null comment '统计数据主体ID',
    data_time          datetime   null comment '创建时间',
    stat_time          datetime   null comment '统计时间',
    result             mediumtext null comment '统计结果数量',
    lessee_id          int        null comment '租户ID',
    data_access_app_id int        null comment '接入应用id'
)
    comment 'API调用情况统计;1、对api的使用情况进行统计';

create index idx_access_app_id_lessee_id
    on dw_open_access_statistics (data_access_app_id, lessee_id);

