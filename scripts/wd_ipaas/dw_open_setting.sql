create table dw_open_setting
(
    id              bigint auto_increment comment '主键id'
        primary key,
    setting_content json         null comment '设置内容',
    info_type       int          null comment '系统设置种类',
    create_by       varchar(100) null comment '创建人',
    update_by       varchar(100) null comment '更新人',
    lessee_id       int          null comment '租户id',
    create_time     datetime     null comment '创建时间',
    update_time     datetime     null comment '更新时间',
    constraint uk_info_type
        unique (info_type)
);

INSERT INTO wd_ipaas.dw_open_setting (id, setting_content, info_type, create_by, update_by, lessee_id, create_time, update_time) VALUES (136, '{"type": "RSA", "publicKey": "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9bl+eGR5NFrlVGIqC0qAwlJEMAOksX5n2uL8CpzIUK+k/Tno7o1jj0fhlorv6QEHiTIw7umqp5j+OXTSQpctWN3OaKWC59jLDsIXdqUOlQb7ltV3A437S6qiY9fMK/0wnCwC+ySsKmjI55fsvgtQrnpv0YaNP1p+BkcUgAbkCUwIDAQAB", "privateKey": "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL1uX54ZHk0WuVUYioLSoDCUkQwA6Sxfmfa4vwKnMhQr6T9OejujWOPR+GWiu/pAQeJMjDu6aqnmP45dNJCly1Y3c5opYLn2MsOwhd2pQ6VBvuW1XcDjftLqqJj18wr/TCcLAL7JKwqaMjnl+y+C1Cuem/Rho0/Wn4GRxSABuQJTAgMBAAECgYArQuS1X3krE9B5naQsYlvRHGzPt/mId8DatrBws6xdLPYUziBeNFzdkptO4s5ZRYmGXAL+tQEdEgdbxO93RVXdUxL2OQx7cJ4AaY5rYbDrin8QsVDyAxfXFZ/Kl19yrJKdWeL40HFv+iLSU7Z17iAonDoOHaYPo03BoHVIv/arcQJBAOcekfK8FwviOD4v+ZzeplAVd31H1OC/KsaFWmCUVWHZnPhFpTG35EP0Ss/JEVk8mnkS7qFUrz7rpG0c6TcsNCkCQQDR0uok17OSu6sdPpiH82b7eCZbf2pkywLR+IOl1zm6byvNhXdMh1e9y3bM1s3ZB4CDwF25QdQR/ddS9b6G07IbAkBGFi0oMQXEIsvvf9jwS0ygCIJ9yWSy6wXrwdDQbOsfi0Bgwa4Az8U3HkP5kv3h09D8WlNnNSkXqZnpRNUsWAlRAkEAg1iVi+SfG65w6kih8RC0GZzX3hT/xAK1TD1WI717/LHU6L2wtW5PxZaVUprwlvg0QcVspiiIGY8eeGcvEnV4mwJBAIM2hgCrWXvYzRQM4rlwUoyYAB9SB1dwccDeTkhLUlNSa8keiw0tZEGrZ7xJWWOw5omXCA74OF+TzA/henwwJLM="}', 2, null, null, null, '2023-04-04 14:58:02', '2023-04-04 14:58:05');
INSERT INTO wd_ipaas.dw_open_setting (id, setting_content, info_type, create_by, update_by, lessee_id, create_time, update_time) VALUES (142, '35', 3, null, null, null, '2023-04-21 11:24:14', '2023-04-21 11:24:17');
INSERT INTO wd_ipaas.dw_open_setting (id, setting_content, info_type, create_by, update_by, lessee_id, create_time, update_time) VALUES (144, '{"logoUrl": "https://open-ipaas.wakedata.com/logo.ico", "systemName": "惟客-昆仑-集成云", "miniLogoUrl": "https://open-ipaas.wakedata.com/logo.ico"}', 1, null, null, null, null, null);
