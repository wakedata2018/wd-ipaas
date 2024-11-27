create table dw_open_api_warn_log
(
    id                int auto_increment comment 'id'
        primary key,
    data_asset_api_id int          null comment '数据资产apiId;id',
    warn_user         varchar(500) null comment '发送地址',
    warn_type         varchar(20)  null comment '告警类型',
    content           varchar(255) null comment '告警内容',
    warn_classify     tinyint      null comment '1为超时告警;2为错误次数超限告警',
    create_time       datetime     null comment '告警时间'
)
    comment 'api监控告警日志记录表;1、对达到api监控告警上限通知后产生的记录';

create index idx_asset_api_id
    on dw_open_api_warn_log (data_asset_api_id);

