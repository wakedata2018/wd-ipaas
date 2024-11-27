create table uaa_license_record
(
    id              bigint unsigned auto_increment comment 'id'
        primary key,
    platform_id     bigint       null comment '平台id(根据平台id区分系统)',
    customer_name   varchar(64)  null comment '客户名称',
    license_address varchar(512) null comment 'license文件地址',
    license_info    text         null comment 'license信息，解析文件内容',
    upgrade_status  tinyint      null comment '升级状态 1：待升级 2：已升级 3：升级失败',
    create_by       varchar(64)  null comment '创建人',
    update_by       varchar(64)  null comment '更新人',
    create_time     datetime     null comment '创建时间',
    update_time     datetime     null comment '更新时间'
)
    comment 'license升级记录';

create index idx_platform_id
    on uaa_license_record (platform_id);

