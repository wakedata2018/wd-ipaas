create table dw_open_api_operator_attribute
(
    id           int auto_increment
        primary key,
    class_name   varchar(128)      not null comment '算子实现类路径',
    layer        varchar(128)      not null comment '算子层级',
    default_name varchar(128)      not null comment '算子层级',
    unique_name  varchar(128)      not null comment '算子层级',
    description  varchar(128)      not null comment '算子描述',
    order_field  int               null comment '排序号',
    used         tinyint default 1 null comment '是否使用中',
    template     text              null comment '算子模板'
)
    comment '任务拖拽组件算子附加属性';

