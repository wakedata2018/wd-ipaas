create table dw_open_api
(
    data_asset_api_id         int auto_increment comment '自增ID'
        primary key,
    data_asset_name           varchar(255)      null comment '数据资产名称',
    data_asset_description    varchar(255)      null comment '数据资产描述',
    in_charge                 varchar(255)      not null comment '负责人(标识1;标识2,标识3)',
    data_asset_publish_status tinyint           not null comment '数据资产状态',
    data_asset_api_method     varchar(255)      null comment '数据资产API名称',
    update_time               datetime          not null comment '更新时间;',
    create_time               datetime          not null comment '创建时间;',
    icon_url                  varchar(512)      null comment '图标路径',
    api_name                  varchar(255)      not null comment 'API名称',
    update_frequency          int               not null comment '更新频率',
    protocol                  int               not null comment '请求协议',
    secret                    tinyint           null comment '加密方式',
    api_group_id              int               not null comment '分组ID',
    api_description           varchar(1024)     null comment 'API描述',
    response_content_type     varchar(255)      not null comment '返回格式',
    req_method                varchar(255)      not null comment '请求方法',
    datasource_config_id      int               null comment '数据源配置ID',
    publisher                 varchar(64)       null comment '发布者',
    lessee_id                 int               null comment '租户ID',
    api_type                  tinyint           null comment 'API类型;0代表自定义sql,1代表普通的单表API',
    api_sql                   varchar(5000)     null comment '自定义api的SQL',
    operation_type            tinyint default 3 null comment 'api的操作类型：0:增加;1:删除，2:修改，3:查看',
    is_http_subscriber        tinyint default 3 null comment '0:mq和kafka接收地址;1:http接收地址 3，默认之前的逻辑',
    event_center_id           varchar(50)       null comment '事件中心dw_open_event_SUBSCRIBE_ADDRESS表http类型对应的id',
    publish_time              datetime          null comment '发布时间',
    resp_mapping_rule         int     default 0 null comment '响应参数映射规则，0或者null：不映射，1：惟客云，其它数字对应参数映射规则表id',
    constraint uk_data_asset_api_method
        unique (data_asset_api_method)
)
    comment '数据资产API;1、基础api信息，开放平台提供的api';

create index idx_api_group_id_lessee_id
    on dw_open_api (api_group_id, lessee_id);

INSERT INTO wd_ipaas.dw_open_api (data_asset_api_id, data_asset_name, data_asset_description, in_charge, data_asset_publish_status, data_asset_api_method, update_time, create_time, icon_url, api_name, update_frequency, protocol, secret, api_group_id, api_description, response_content_type, req_method, datasource_config_id, publisher, lessee_id, api_type, api_sql, operation_type, is_http_subscriber, event_center_id, publish_time, resp_mapping_rule) VALUES (22, null, null, '13012345678', 0, 'store/wd-app/rpc/business.queryByBusinessList', '2024-11-19 14:40:43', '2024-08-30 15:24:01', null, '查询业务单元集合', 2, 2, 1, 4, '查询业务单元集合', 'application/json', 'POST', null, null, 3, 2, null, 0, null, null, '2024-08-30 15:27:38', 0);
INSERT INTO wd_ipaas.dw_open_api (data_asset_api_id, data_asset_name, data_asset_description, in_charge, data_asset_publish_status, data_asset_api_method, update_time, create_time, icon_url, api_name, update_frequency, protocol, secret, api_group_id, api_description, response_content_type, req_method, datasource_config_id, publisher, lessee_id, api_type, api_sql, operation_type, is_http_subscriber, event_center_id, publish_time, resp_mapping_rule) VALUES (23, null, null, '13012345678', 1, 'loyaltyV2/wd-loyalty/open/member.detail', '2024-11-19 14:34:36', '2024-11-19 11:00:52', null, '会员信息查询', 2, 2, 1, 5, '根据会员账号查询会员详情', 'application/json', 'POST', null, '13012345678', 3, 2, null, 0, null, null, '2024-11-19 14:34:36', 1);
