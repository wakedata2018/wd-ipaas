create table dw_open_api_warn
(
    id                int auto_increment comment '自增ID'
        primary key,
    warn_name         varchar(50)  not null comment '告警名称',
    warn_desc         varchar(255) null comment '描述',
    rule_second       int          null comment '请求大于多少秒;发告警',
    rule_error_times  int          null comment '请求错误多少次;发告警',
    status            tinyint      not null comment '1代表启用;0表示禁用',
    email             varchar(500) null comment '邮件;多个用逗号分隔',
    phone             varchar(500) null comment '电话;多个用逗号分隔',
    data_asset_api_id int          not null comment '数据资产apiId;id',
    create_time       datetime     null,
    update_time       datetime     null
)
    comment 'api监控告警配置表;1、对api配置监控告警规则';

create index idx_asset_api_id
    on dw_open_api_warn (data_asset_api_id);

