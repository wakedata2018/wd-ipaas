create table uaa_approval_es_log
(
    ID                   bigint auto_increment comment '自增id'
        primary key,
    EP_ID                bigint       null comment '企业id',
    EP_NAME              varchar(50)  null comment '企业名称',
    APP_ID               bigint       null comment '品牌id',
    APP_NAME             varchar(50)  null comment '品牌名称',
    STORE_ID             bigint       null comment '门店id',
    STORE_NAME           varchar(50)  null comment '门店名称',
    ES_INDEX             varchar(50)  null comment 'es索引',
    ES_ID                varchar(50)  null comment 'es唯一id（代码生成的）',
    ES_TYPE              varchar(50)  null comment 'es文档类型,_doc',
    ES_UPDATE            text         null comment 'es更新数据 ApprovalDTO',
    UPDATE_STATUS        tinyint(1)   null comment 'es数据更新状态 1-成功 0-失败',
    UPDATE_FAILED_REASON varchar(100) null comment 'es数据更新失败原因',
    APPROVAL_ID          bigint       null comment '审批id',
    APPLY_USER_NAME      varchar(50)  null comment '审批提交人用户名称',
    APPLY_USER_ID        bigint       null comment '审批提交人用户ID
',
    NAME                 varchar(255) null comment '通用名称',
    SEND_TIME            datetime     null comment '提交审批时间',
    APPROVER_USER_ID     bigint       null comment '审批人id',
    APPROVER_USER_NAME   varchar(50)  null comment '审批人名称',
    APPROVER_USER_PHONE  varchar(20)  null comment '审批人手机号',
    APPROVER_TIME        datetime     null comment '审批时间',
    APPROVAL_TYPE        varchar(20)  null comment '商品： item, 订单：order 会员： member审批类型',
    APPROVAL_OPERATOR    varchar(20)  null comment '审批操作类型，add , update',
    CREATE_TIME          datetime     null comment '创建时间'
)
    comment '审批es数据操作记录' row_format = DYNAMIC;

