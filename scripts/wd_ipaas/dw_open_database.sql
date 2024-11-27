create table dw_open_database
(
    id                int auto_increment comment '自增ID'
        primary key,
    database_name     varchar(64)  null comment '数据库名称',
    driver_class_name varchar(128) null comment '驱动类名',
    create_time       datetime     null comment '创建时间',
    update_time       datetime     null comment '更新时间',
    pic_url           varchar(128) null comment '图片地址',
    default_port      bigint       null comment '默认端口'
)
    comment '数据源管理配置表;1、用于配置当前支持的数据源，如mysql';

INSERT INTO wd_ipaas.dw_open_database (id, database_name, driver_class_name, create_time, update_time, pic_url, default_port) VALUES (2, 'MYSQL', 'com.mysql.cj.jdbc.Driver', '2022-08-09 15:46:23', '2022-08-09 15:46:25', 'mysql', 3306);
