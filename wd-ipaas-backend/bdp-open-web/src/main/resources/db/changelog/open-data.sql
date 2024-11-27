INSERT INTO DW_OPEN_OPTIMISTIC_LOCK (ID, LOCK_NAME, IP, LOCK_STATUS, VERSION_NO, CREATE_TIME, UPDATE_TIME)
VALUES (1, 'API调用情况统计-每天', '', 0, 1, NOW(), NOW());
INSERT INTO DW_OPEN_OPTIMISTIC_LOCK (ID, LOCK_NAME, IP, LOCK_STATUS, VERSION_NO, CREATE_TIME, UPDATE_TIME)
VALUES (2, 'API调用情况统计-每刻', '', 0, 1, NOW(), NOW());
INSERT INTO DW_OPEN_OPTIMISTIC_LOCK (ID, LOCK_NAME, IP, LOCK_STATUS, VERSION_NO, CREATE_TIME, UPDATE_TIME)
VALUES (3, '登陆总数', '', 0, 1, NOW(), NOW());

INSERT INTO `DW_OPEN_USER_ROLE` (`ID`, `ROLE_NAME`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`, `ROLE_TYPE`) VALUES ('1', 'Administrator', now(), 'SYSTEM', now(), 'SYSTEM', '1');
INSERT INTO `DW_OPEN_USER_ROLE` (`ID`, `ROLE_NAME`, `CREATE_TIME`, `CREATE_USER`, `UPDATE_TIME`, `UPDATE_USER`, `ROLE_TYPE`) VALUES ('2', 'Normal', now(), 'SYSTEM', now(), 'SYSTEM', '2');



insert into DW_OPEN_USER values (1, "wakedata", "wakedata", "00000001", "", "", 1, now(), now());
insert into DW_OPEN_USER values (2, "hongyu", "wakedata", "00000001", "", "", 1, now(), now());
insert into DW_OPEN_USER values (3, "tanzhi", "wakedata", "00000001", "", "", 1, now(), now());
insert into DW_OPEN_USER values (4, "yuexiudemo", "wakedata", "00000001", "", "", 1, now(), now());
insert into DW_OPEN_USER values (5, "chengan", "wakedata", "00000001", "", "", 2, now(), now());
insert into DW_OPEN_USER values (6, "thx", "thx", "00000001", "", "", 1, now(), now());


##INSERT INTO DW_OPEN_USER_INFO (zempl, znachn, zorgname, zpostname, zuspnid, zusrid, zgesch, znaen)
##select user_id,
##       user_name,
##       department,
##       position,
##       phone,
##       account_number,
##       sex,
##       english_name
##from user_info;


##--- 开发环境
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (1, "集成开发平台", "开发工具", null, "https://dtflow.countrygarden.com.cn",
#         "集成开发平台是集成WebIDE、调度系统和元数据系统于一体，提供一个可让开发人员/懂sql的产品人员编写sql、测试、查看数据并配置调度任务的平台，实现产品平台化，提高开发效率，减少临时提数需求。", NOW(),
#         NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (2, "流平台", "开发工具", null, "http://10.10.198.67",
#         "惟客流可提供数据采集器（agent）将服务器日志文件等数据实时采集，并将数据存入kafka，业务方可选择通用Flink SQL脚本或自定义Flink SQL脚本消费数据，并继续入库至hive、hbase、mysql，ES实现数据流的实时处理能力。",
#         NOW(), NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (3, "数据治理平台", "开发工具", null, "https://dtdg.countrygarden.com.cn/bdpdg/index.html",
#         "数据治理平台是元数据管理产品域的一个应用，通过以集中式管理模式管理元数据，统一全局元数据，并以此作为全局元数据的统一发布源。集中统一管理元数据、数据标准及数据质量，提供元数据、数据标准创建、维护、查询，数据质量监控，实现对元数据的管理、维护操作，对数据质量跟踪，以及对元数据进行应用分析。",
#         NOW(), NOW());
##--- 测试环境

##INSERT INTO DW_OPEN_DEV_TOOL
##VALUES (1, "集成开发平台", "开发工具", null, "https://dtflow.countrygarden.com.cn/index.html",
##        "集成开发平台是集成WebIDE、调度系统和元数据系统于一体，提供一个可让开发人员/懂sql的产品人员编写sql、测试、查看数据并配置调度任务的平台，实现产品平台化，提高开发效率，减少临时提数需求。", NOW(),
##        NOW());
##INSERT INTO DW_OPEN_DEV_TOOL
##VALUES (2, "流平台", "开发工具", null, "http://10.10.198.67",
##        "惟客流可提供数据采集器（agent）将服务器日志文件等数据实时采集，并将数据存入kafka，业务方可选择通用Flink SQL脚本或自定义Flink SQL脚本消费数据，并继续入库至hive、hbase、mysql，ES实现数据流的实时处理能力。",
##        NOW(), NOW());
##INSERT INTO DW_OPEN_DEV_TOOL
##VALUES (3, "数据治理平台", "开发工具", null, "https://dtdg.countrygarden.com.cn/bdpdg/index.html",
##        "数据治理平台是元数据管理产品域的一个应用，通过以集中式管理模式管理元数据，统一全局元数据，并以此作为全局元数据的统一发布源。集中统一管理元数据、数据标准及数据质量，提供元数据、数据标准创建、维护、查询，数据质量监控，实现对元数据的管理、维护操作，对数据质量跟踪，以及对元数据进行应用分析。",
##        NOW(), NOW());
####--- 生产环境

# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (1, "集成开发平台", "开发工具", null, "https://dtflow.countrygarden.com.cn/",
#        "集成开发平台是集成WebIDE、调度系统和元数据系统于一体，提供一个可让开发人员/懂sql的产品人员编写sql、测试、查看数据并配置调度任务的平台，实现产品平台化，提高开发效率，减少临时提数需求。", NOW(),
#        NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (2, "流平台", "开发工具", "imgStream", "http://10.10.198.67/",
#        "惟客流可提供数据采集器（agent）将服务器日志文件等数据实时采集，并将数据存入kafka，业务方可选择通用Flink SQL脚本或自定义Flink SQL脚本消费数据，并继续入库至hive、hbase、mysql，ES实现数据流的实时处理能力。",
#        NOW(), NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (3, "数据治理平台", "开发工具", null, "https://dtdg.countrygarden.com.cn/bdpdg",
#        "数据治理平台是元数据管理产品域的一个应用，通过以集中式管理模式管理元数据，统一全局元数据，并以此作为全局元数据的统一发布源。集中统一管理元数据、数据标准及数据质量，提供元数据、数据标准创建、维护、查询，数据质量监控，实现对元数据的管理、维护操作，对数据质量跟踪，以及对元数据进行应用分析。",
#        NOW(), NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (4, "通用权限系统", "开发工具", null, "https://dtdg.countrygarden.com.cn/bdpdg/index.html",
#         "通用权限管理提供对应用系统的所有对象资源和数据资源进行权限控制，比如应用系统的功能菜单、各个界面的按钮、数据显示的列以及各种行级数据进行权限的操控。",
#         NOW(), NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (5, "算法平台", "开发工具", null, "http://algo.countrygarden.com.cn/machine-learn.html",
#         "用户可在没有编程基础的情况下，通过拖拽的方式进行操作，将数据输入输出、数据预处理、挖掘建模、模型评估等环节通过流程化的方式进行连接，使用户可以理解数据，并设计数据挖掘流程和可重用组件，以达到数据分析挖掘的目的。",
#         NOW(), NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (6, "数据开放服务平台", "开发工具", null, "https://dt.countrygarden.com.cn",
#         "数据服务平台旨在为企业搭建统一的数据服务总线，帮助企业统一管理对内对外的API服务。数据服务为您提供快速将数据表生成数据API的能力并统一管理和发布。",
#         NOW(), NOW());
# INSERT INTO DW_OPEN_DEV_TOOL
# VALUES (7, "集群管理", "开发工具", null, "http://10.10.181.234:8080",
#         "集群管理平台是建立集成化的资源运行状态监控管理，实现平台运行状态和信息化资源的统一化、可视化、可控化管理。基于资源的统一化管理，实现监控子系统和故障处理流程子系统的一体化部署，全面支撑信息技术服务管理，实现面向基础资源和面向业务应用两个层面的监控，全面展示所有服务的运行状况和处理性能的管理平台。",
#         NOW(), NOW());

##-- wakedata
##
INSERT INTO DW_OPEN_DEV_TOOL
VALUES (1, "集成开发平台", "开发工具", null, "https://flow.wakedata.com/",
       "集成开发平台是集成WebIDE、调度系统和元数据系统于一体，提供一个可让开发人员/懂sql的产品人员编写sql、测试、查看数据并配置调度任务的平台，实现产品平台化，提高开发效率，减少临时提数需求。", NOW(),
       NOW());
INSERT INTO DW_OPEN_DEV_TOOL
VALUES (2, "流平台", "开发工具", null, "http://streaming.wakedata.com/",
       "惟客流可提供数据采集器（agent）将服务器日志文件等数据实时采集，并将数据存入kafka，业务方可选择通用Flink SQL脚本或自定义Flink SQL脚本消费数据，并继续入库至hive、hbase、mysql，ES实现数据流的实时处理能力。",
       NOW(), NOW());
INSERT INTO DW_OPEN_DEV_TOOL
VALUES (3, "数据治理平台", "开发工具", null, "http://govern.wakedata.com/dgp/",
       "数据治理平台是元数据管理产品域的一个应用，通过以集中式管理模式管理元数据，统一全局元数据，并以此作为全局元数据的统一发布源。集中统一管理元数据、数据标准及数据质量，提供元数据、数据标准创建、维护、查询，数据质量监控，实现对元数据的管理、维护操作，对数据质量跟踪，以及对元数据进行应用分析。",
       NOW(), NOW());
INSERT INTO DW_OPEN_DEV_TOOL
VALUES (4, "通用权限平台", "开发工具", null, "https://auth.wakedata.com/",
       "通用权限系统是基于惟客大数据所有子系统的一站式权限管理系统，系统可为各个子系统支撑以团队、组织、角色、个人等进行各个类别的权限管理，提供安全，简便的权限管控流程。",
       NOW(), NOW());




INSERT INTO DW_OPEN_DATASOURCE_CONFIG  (`ID`, `CONNECTION_NAME`, `DB_TYPE`, `DB_NAME`, `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `CREATE_TIME`, `UPDATE_TIME`, `ZK_NODE`, `PARENT_ID`, `CHILDREN_ID`) VALUES ('1', 'mysql开发数据库', '1', 'DW_OPEN', 'localhost', '3306', 'root', '123456', '2019-11-21 14:47:41', '2019-11-21 14:47:41', NULL, NULL, NULL);
INSERT INTO DW_OPEN_DATASOURCE_CONFIG  (`ID`, `CONNECTION_NAME`, `DB_TYPE`, `DB_NAME`, `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `CREATE_TIME`, `UPDATE_TIME`, `ZK_NODE`, `PARENT_ID`, `CHILDREN_ID`) VALUES ('2', 'oracle公司内部数据库', '2', 'orcl', '172.26.59.70', '1521', 'RMSPRD', 'wakedata', '2019-11-21 14:47:41', '2019-11-21 14:47:41', NULL, NULL, NULL);
INSERT INTO DW_OPEN_DATASOURCE_CONFIG  (`ID`, `CONNECTION_NAME`, `DB_TYPE`, `DB_NAME`, `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `CREATE_TIME`, `UPDATE_TIME`, `ZK_NODE`, `PARENT_ID`, `CHILDREN_ID`) VALUES ('3', '公司内部hbase数据库', '3', 'default', 'hd-59-57.wakedata.com,hd-59-56.wakedata.com,hd-59-28.wakedata.com', '2181', NULL, NULL, '2019-11-21 14:47:41', '2019-11-21 14:47:41', '/hbase-unsecure', NULL, NULL);
# INSERT INTO `DW_OPEN`.`DW_OPEN_DATASOURCE_CONFIG` (`ID`, `CONNECTION_NAME`, `DB_TYPE`, `DB_NAME`, `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `CREATE_TIME`, `UPDATE_TIME`, `ZK_NODE`, `PARENT_ID`, `CHILDREN_ID`) VALUES ('4', '公司内部hive', '4', 'wakedata3', 'hd-node-3-24.wakedata.com,hd-node-3-41.wakedata.com,hd-node-3-25.wakedata.com', '2181', 'hive', NULL, '2019-11-25 15:23:24', '2019-11-25 15:23:24', 'hiveserver2', NULL, '5');
# INSERT INTO `DW_OPEN`.`DW_OPEN_DATASOURCE_CONFIG` (`ID`, `CONNECTION_NAME`, `DB_TYPE`, `DB_NAME`, `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `CREATE_TIME`, `UPDATE_TIME`, `ZK_NODE`, `PARENT_ID`, `CHILDREN_ID`) VALUES ('5', '公司内部hive', '1', 'hive2', 'hd-node-3-25.wakedata.com', '3306', 'hive', '2ZnwdVE_b', '2019-11-26 10:35:28', '2019-11-26 10:35:28', NULL, '4', NULL);



insert into DW_OPEN_DATABASE values(1,'MYSQL','com.mysql.cj.jdbc.Driver',now(),now(),'mysql','3306');
insert into DW_OPEN_DATABASE values(2,'ORACLE','oracle.jdbc.driver.OracleDriver',now(),now(),'oracle','1521');
insert into DW_OPEN_DATABASE values(3,'HBASE','',now(),now(),'hbase','2181');
# insert into DW_OPEN_DATABASE values(4,'HIVE','org.apache.hive.jdbc.HiveDriver',now(),now(),'hive','10000');
# insert into DW_OPEN_DATABASE values(5,'POSTGRESQL','org.postgresql.Driver',now(),now(),null);
# insert into DW_OPEN_DATABASE values(6,'ELASTICSEARCH','',now(),now(),'es','');
# insert into DW_OPEN_DATABASE values(7,'H2','',now(),now(),null);
# insert into DW_OPEN_DATABASE values(8,'KYLIN','org.apache.kylin.jdbc.Driver',now(),now(),null);

