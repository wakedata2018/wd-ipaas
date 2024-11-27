create table uaa_scope_default_service
(
    ID            bigint auto_increment comment '自增id'
        primary key,
    SCOPE_ID      bigint               not null comment '业务域id',
    SERVICE_ID    bigint               not null comment '默认模块id',
    CREATE_TIME   datetime             not null comment '创建时间',
    BUSINESS_TYPE tinyint(1) default 1 null comment '入驻商家类型 1-品牌 2-自营品牌 3-供应商 4-分站点'
)
    comment '每个业务域的默认模块' row_format = DYNAMIC;

