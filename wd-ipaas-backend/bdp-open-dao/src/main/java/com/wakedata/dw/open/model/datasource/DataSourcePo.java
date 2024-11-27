package com.wakedata.dw.open.model.datasource;

import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.model.BaseLesseePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author tanzhi
 * @title DataSourceConfigPo
 * @date 2019/11/18 14:13
 * @projectName bdp-open
 * @descriptor
 */
@Table(name = "DW_OPEN_DATASOURCE_CONFIG")
@Data
public class DataSourcePo extends BaseLesseePo {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "CONNECTION_NAME")
    private String connectionName;
    @Column(name = "DB_TYPE")
    private DatasourceTypeEnum dbType;
    @Column(name = "DB_NAME")
    private String dbName;
    @Column(name = "DB_HOST")
    private String dbHost;
    @Column(name = "DB_PORT")
    private Integer dbPort;
    @Column(name = "DB_USERNAME")
    private String dbUsername;
    @Column(name = "DB_PASSWORD")
    private String dbPassword;
    @Column(name = "ZK_NODE")
    private String zkNode;
    @Column(name = "DB_DESCRIPTION")
    private String description;
    @Column(name = "CHILDREN_ID")
    private Integer childrenId;
    @Column(name = "PARENT_ID")
    private Integer parentId;
    @Column(name = "URL")
    private String url;
}
