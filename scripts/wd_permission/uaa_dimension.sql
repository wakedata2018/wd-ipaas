create table uaa_dimension
(
    ID               bigint auto_increment comment 'ID'
        primary key,
    NAME             varchar(255)         null comment '维度名称',
    DESCRIPTION      varchar(255)         null comment '维度描述',
    IDENTIFIER       varchar(255)         null comment '维度唯一标识',
    PLATFORM_ID      bigint               null comment '平台域ID',
    DIMENSION_COLUMN varchar(255)         null comment '数据权限字段',
    CREATE_TIME      datetime             null comment '创建时间',
    UPDATE_TIME      datetime             null comment '修改时间',
    STATUS           tinyint(1) default 1 null comment '状态'
)
    comment '维度表' row_format = DYNAMIC;

