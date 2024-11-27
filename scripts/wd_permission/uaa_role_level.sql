create table uaa_role_level
(
    ID          bigint auto_increment comment 'ID'
        primary key,
    SCOPE_ID    bigint               null comment '业务域id',
    NAME        varchar(255)         not null comment '角色名称',
    LEVEL       int                  not null comment '角色级别',
    CREATE_TIME datetime             null comment '创建时间',
    UPDATE_TIME datetime             null comment '更新时间',
    STATUS      tinyint(1) default 1 null comment '状态'
)
    comment '角色级别' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_role_level (ID, SCOPE_ID, NAME, LEVEL, CREATE_TIME, UPDATE_TIME, STATUS) VALUES (7, 111, '管理员', 150, '2020-05-22 15:08:56', '2020-05-22 15:08:56', 1);
INSERT INTO wd_permission.uaa_role_level (ID, SCOPE_ID, NAME, LEVEL, CREATE_TIME, UPDATE_TIME, STATUS) VALUES (8, 111, '运营', 200, '2020-05-22 15:08:56', '2020-05-22 15:08:56', 1);
INSERT INTO wd_permission.uaa_role_level (ID, SCOPE_ID, NAME, LEVEL, CREATE_TIME, UPDATE_TIME, STATUS) VALUES (9, 111, '财务', 300, '2020-05-22 15:08:56', '2020-05-22 15:08:56', 1);
