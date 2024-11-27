create table uaa_role_permission_field_ral
(
    ID            bigint auto_increment comment '自增Id'
        primary key,
    APP_BU_ID     bigint      null comment '应用业务单元ID',
    ROLE_ID       varchar(36) not null comment '角色id',
    MENU_ID       varchar(36) not null comment '菜单id',
    PERMISSION_ID varchar(36) not null comment '权限(接口)id',
    FIELD_ID      bigint      not null comment '字段Id',
    HIDE_TYPE     int         not null comment '隐藏类型',
    CREATE_TIME   datetime    not null comment '创建时间',
    UPDATE_TIME   datetime    not null comment '更新时间'
)
    comment '角色与接口权限字段关联表' row_format = DYNAMIC;

create index idx_field
    on uaa_role_permission_field_ral (FIELD_ID)
    comment '字段索引';

create index idx_hide_type
    on uaa_role_permission_field_ral (HIDE_TYPE)
    comment '隐藏类型索引';

create index idx_menu
    on uaa_role_permission_field_ral (MENU_ID)
    comment '菜单索引';

create index idx_role
    on uaa_role_permission_field_ral (ROLE_ID)
    comment '角色索引';

