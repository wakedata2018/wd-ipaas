create table dw_open_lessee
(
    id          bigint auto_increment comment 'ID'
        primary key,
    name        varchar(64) null comment '名称',
    app_num     int         null comment '可创建应用数',
    tenant_id   bigint      null comment '关联惟客云租户id',
    create_by   varchar(64) null comment '创建人',
    create_time datetime    null comment '创建时间',
    update_by   varchar(64) null comment '更新人',
    update_time datetime    null comment '更新时间',
    is_deleted  tinyint     null comment '是否删除 0 否 1：是'
)
    comment 'ipaas访问租户配置表;1、主要用来做开放平台内部的数据隔离';

INSERT INTO wd_ipaas.dw_open_lessee (id, name, app_num, tenant_id, create_by, create_time, update_by, update_time, is_deleted) VALUES (3, '演示企业', null, 467, null, '2022-10-26 16:54:46', null, '2022-10-26 16:54:46', 0);
