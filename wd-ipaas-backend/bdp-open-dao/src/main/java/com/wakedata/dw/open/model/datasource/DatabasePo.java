package com.wakedata.dw.open.model.datasource;

import com.wakedata.dw.open.model.BasePo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author tanzhi
 * @title DataSourcePo
 * @date 2019/11/18 14:14
 * @projectName bdp-open
 * @descriptor
 */
@Table(name = "DW_OPEN_DATABASE")
@Data
public class DatabasePo extends BasePo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "DATABASE_NAME")
    private String databaseName;
    @Column(name = "DRIVER_CLASS_NAME")
    private String driverClassName;
    @Column(name = "PIC_URL")
    private String picUrl;
    @Column(name = "DEFAULT_PORT")
    private String defaultPort;
}
