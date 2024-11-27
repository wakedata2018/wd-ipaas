create table uaa_scope
(
    ID            bigint auto_increment comment 'ID'
        primary key,
    NAME          varchar(255)         null comment '名称',
    PLATFORM_ID   bigint               null comment '平台Id',
    BUSINESS_TYPE varchar(255)         null comment '业务类型： 行业：industry， 企业： enterprise',
    BUSINESS_KEY  varchar(255)         null comment '业务标识,根据BUSINESS_TYPE',
    RELATION_ID   varchar(64)          null comment '关联ID ,通过BUSINESS_TYPE 来确定具体语义',
    BUSINESS_DESC varchar(255)         null comment '描述字段',
    CREATE_TIME   datetime             null comment '创建时间',
    UPDATE_TIME   datetime             null comment '修改时间',
    STATUS        tinyint(1) default 1 null comment '状态',
    constraint uniq_platformId_businessKey_status
        unique (PLATFORM_ID, BUSINESS_KEY, STATUS) comment '权限唯一标识状态索引'
)
    comment '业务域' row_format = DYNAMIC;

INSERT INTO wd_permission.uaa_scope (ID, NAME, PLATFORM_ID, BUSINESS_TYPE, BUSINESS_KEY, RELATION_ID, BUSINESS_DESC, CREATE_TIME, UPDATE_TIME, STATUS) VALUES (111, '大会员集团', 4, 'industry', 'ump', '112', '大会员集团企业管理', '2021-03-02 19:24:08', '2021-03-02 19:24:08', 1);
INSERT INTO wd_permission.uaa_scope (ID, NAME, PLATFORM_ID, BUSINESS_TYPE, BUSINESS_KEY, RELATION_ID, BUSINESS_DESC, CREATE_TIME, UPDATE_TIME, STATUS) VALUES (126, 'Ipaas菜单业务域', 63, 'industry', 'ipaas_menu', '3', 'Ipaas菜单业务域', '2022-05-13 19:36:22', '2022-05-13 19:36:22', 1);
