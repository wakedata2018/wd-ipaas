create table dw_open_api_http_attr
(
    id                    int auto_increment comment '自增ID'
        primary key,
    data_asset_api_id     int              null comment '数据资产apiId;主键',
    host                  varchar(128)     null comment 'api host;API host',
    path                  varchar(255)     null comment 'API PATH;API PATH',
    timeout               int default 3000 not null comment '超时时间,单位毫秒;单位毫秒',
    result_example        text             null comment 'API 返回结果示例;API 返回结果示例',
    error_example         text             null comment 'API 返回编码示例;API 返回编码示例',
    error_definition_json text             null comment '错误码定义;错误码定义',
    ws_method             varchar(64)      null comment 'WebService请求方法',
    ws_namespace_uri      varchar(64)      null comment 'WebService命名空间'
)
    comment 'HTTP外部注册API;1、开放平台调用的内部api';

create index idx_asset_api_id
    on dw_open_api_http_attr (data_asset_api_id);

INSERT INTO wd_ipaas.dw_open_api_http_attr (id, data_asset_api_id, host, path, timeout, result_example, error_example, error_definition_json, ws_method, ws_namespace_uri) VALUES (22, 22, 'http://wd-app.wakecloud.svc.cluster.local:8080', 'wd-app/rpc/business/queryByBusinessList', 3000, '{
	"code":0,
	"data":[
		{
			"attrCode":"string",
			"buName":"string",
			"buStatus":0,
			"buType":0,
			"createBy":"string",
			"createTime":"2024-08-30 15:22:24",
			"id":0,
			"internalEnterpriseStatus":0,
			"merchantStatus":0,
			"nodeCode":"string",
			"nodeId":0,
			"nodeLeaf":0,
			"nodeParentId":0,
			"nodeParentIdList":[
				{}
			],
			"orgNodeInfoDTOList":[
				{
					"nodeId":0,
					"orgNodeParentIds":"string"
				}
			],
			"parentId":0,
			"seq":0,
			"syncOrg":0,
			"tenantId":0,
			"updateBy":"string",
			"updateTime":"2024-08-30 15:22:24"
		}
	],
	"msg":"string",
	"success":true
}', '{
	"errorCode":500,
	"errorMessage":"系统异常",
	"success":false
}', '[{"errorCode":"201","errorMsg":"Created","solution":null},{"errorCode":"401","errorMsg":"Unauthorized","solution":null},{"errorCode":"403","errorMsg":"Forbidden","solution":null},{"errorCode":"404","errorMsg":"Not Found","solution":null}]', null, null);
INSERT INTO wd_ipaas.dw_open_api_http_attr (id, data_asset_api_id, host, path, timeout, result_example, error_example, error_definition_json, ws_method, ws_namespace_uri) VALUES (23, 23, 'http://wd-loyalty.wakecloud.svc.cluster.local:8080', 'wd-loyalty/open/member/detail', 60000, null, null, null, null, null);
