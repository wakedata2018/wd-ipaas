create table uaa_sso_client_mapping
(
    id            bigint auto_increment comment '主键id'
        primary key,
    sso_client_id bigint       not null comment '应用id，对应 uaa_sso_client 表 id 字段',
    param_name    varchar(50)  not null comment '属性名称',
    param_type    tinyint(1)   not null comment '属性类型，0 string 1 number',
    mapping_value varchar(100) not null comment '映射脚本',
    create_time   datetime     not null comment '创建时间',
    create_by     varchar(64)  not null comment '创建人',
    update_time   datetime     not null comment '更新时间',
    update_by     varchar(64)  not null comment '更新人'
);

