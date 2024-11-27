create table uaa_menu_0419
(
    ID            varchar(36)             not null comment '菜单id(uuid)'
        primary key,
    DOMAIN_ID     bigint                  null comment '域ID',
    SOURCE        tinyint(1)              null comment '源（1-WEB/2-小程序）',
    INDUSTRY      tinyint(1)              null comment '行业（1-美业，2-零售）',
    URL           varchar(256)            null comment '菜单地址',
    NAME          varchar(64)             null comment '菜单名称',
    ICON          varchar(256)            null comment '菜单图标',
    PARENT_ID     varchar(64)             null comment '上级菜单唯一标识',
    LEVEL         tinyint(1) default 0    null comment '层级',
    IS_LEAF       tinyint(1)              null comment '是否叶子节点',
    SEQ           int        default 1000 null comment '序号',
    STATUS        tinyint(1)              null comment '状态',
    UPDATE_TIME   datetime                null comment '最后更新时间',
    CREATE_TIME   datetime                null comment '创建时间',
    SCOPE_ID      bigint                  null comment '业务域Id',
    IDENTIFIER    varchar(255)            null comment '唯一标识符',
    IS_MENU       tinyint(1) default 1    null comment '是否菜单,0-按钮(功能点) 1-菜单',
    BREADCRUMB    int        default 0    null comment '面包屑',
    type_switch   tinyint(1) default 0    not null comment '菜单类型限制开关,默认0不开启',
    menu_group_id varchar(36)             null comment '应用分类ID',
    constraint UNI_DOMAIN_URL_NAME
        unique (DOMAIN_ID, SOURCE, NAME)
);

