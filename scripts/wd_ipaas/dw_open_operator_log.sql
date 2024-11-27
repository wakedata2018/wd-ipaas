create table dw_open_operator_log
(
    id                  int auto_increment comment '自增主键'
        primary key,
    description         bigint        null,
    class_name          varchar(128)  null comment '调用类',
    method_name         varchar(64)   null comment '调用方法',
    user_identification bigint        null comment '用户',
    parameter           varchar(2048) null comment '请求参数',
    create_time         datetime      null comment '创建时间',
    update_time         datetime      null comment '修改时间',
    response            varchar(2048) null comment '返回结果'
);

