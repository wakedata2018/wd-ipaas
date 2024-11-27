package com.wakedata.dw.open.datasource.model;

import lombok.Data;

@Data
/**
 * @author yiyufeng
 * @title JdbcDatasourceConfig
 * @projectName bdp-open
 * @date
 * @description
 */
public class JdbcDatasourceConfig {
    private String host;
    private String port;
    private String dbName;
    private String userName;
    private String password;
}
