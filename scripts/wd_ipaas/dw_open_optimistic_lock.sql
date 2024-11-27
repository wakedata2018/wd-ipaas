create table dw_open_optimistic_lock
(
    id          int auto_increment comment '锁ID'
        primary key,
    lock_name   varchar(20) null comment '锁名字',
    ip          varchar(20) null comment '占用的IP',
    lock_status smallint    null comment '锁状态',
    version_no  int         null comment '锁版本号',
    create_time datetime    null comment '创建时间',
    update_time datetime    null comment '更新时间'
)
    comment '乐观锁';

