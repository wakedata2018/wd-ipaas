package com.wakedata.dw.open.vo;

import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author tanzhi
 * @title DataSourceConfigReq
 * @date 2019/12/10 15:50
 * @projectName bdp-open
 * @descriptor
 */
@Data
public class DataSourceConfigReq {

    private Integer id;
    @NotNull(message = "connectionName不能为空")
    @Length(max = 64, message = "connectionName最大长度为64")
    private String connectionName;
    @NotNull(message = "dbType不能为空")
    private DatasourceTypeEnum dbType;
    //@NotNull(message = "dbName不能为空")
    @Length(max = 32, message = "dbName最大长度为64")
    private String dbName;
    //@NotNull(message = "dbHost不能为空")
    @Length(max = 256, message = "dbHost最大长度为256")
    private String dbHost;
    //@NotNull(message = "dbPort不能为空")
    private Integer dbPort;
    //@NotNull(message = "dbUsername不能为空")
    @Length(max = 64, message = "dbUsername最大长度为64")
    private String dbUsername;
    //@NotNull(message = "dbPassword不能为空")
    @Length(message = "dbPassword最大长度为64")
    private String dbPassword;
    @Length(max = 256, message = "description最大长度为256")
    private String description;
    @Length(max = 64, message = "zkNode最大长度为64")
    private String zkNode;
    @Length(max = 255,message = "url最大长度256")
    private String url;
}
