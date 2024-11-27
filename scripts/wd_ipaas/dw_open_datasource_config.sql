create table dw_open_datasource_config
(
    id              int auto_increment comment '自增ID'
        primary key,
    connection_name varchar(64)  null,
    db_type         int          null comment '数据库类型ID',
    db_name         varchar(64)  null comment '数据库名称',
    db_host         varchar(256) null comment '数据库地址',
    db_port         int          null comment '数据库端口',
    db_username     varchar(64)  null comment '数据库用户名',
    db_password     varchar(64)  null comment '数据库密码',
    zk_node         varchar(64)  null comment 'ZOOKEEPER节点',
    db_description  varchar(256) null comment '数据源描述',
    create_time     datetime     null comment '创建时间',
    update_time     datetime     null comment '更新时间',
    parent_id       int          null comment '父配置ID',
    children_id     int          null comment '子配置ID',
    lessee_id       int          null comment '租户ID',
    url             varchar(256) null comment '数据源为phoenix的时候url'
)
    comment '已配置数据源管理表;1、已配置的数据源';

create index idx_lessee_id
    on dw_open_datasource_config (lessee_id);

