create table dw_open_custom_function
(
    id            bigint unsigned auto_increment comment 'id'
        primary key,
    lessee_id     bigint                       null comment '租户id',
    func_name     varchar(32)                  null comment '函数名称(以字母开头，只能包含字母加数字，租户唯一)',
    func_language varchar(32) default 'groovy' null comment '函数代码编写语言，默认groovy',
    func_desc     varchar(256)                 null comment '函数说明',
    status        tinyint                      null comment '状态 1：草稿 2：上线 3：下线',
    func_param    text                         null comment '函数参数 ,json数组串 [{name:参数名称,type:参数类型,desc:参数说明}]',
    func_return   varchar(30)                  null comment '函数返回类型',
    func_code     text                         null comment '函数代码',
    debug_status  tinyint                      null comment '测试状态 1：未测试 2：测试通过 3：测试失败 （只有测试通过的函数才能上线）',
    create_by     varchar(64)                  null comment '创建人',
    create_time   datetime                     null comment '创建时间',
    update_by     varchar(64)                  null comment '更新人',
    update_time   datetime                     null comment '更新时间',
    constraint uk_func_name_lessee_id
        unique (func_name, lessee_id)
)
    comment '自定义函数配置表(添加租户自定义函数)';

