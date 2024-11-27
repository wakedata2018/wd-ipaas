package com.wakedata.dw.open.datasource;

import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import org.springframework.context.ApplicationContext;

/**
 * @author tanzhi
 * @title DataSourceFactory
 * @date 2019/11/18 15:11
 * @projectName bdp-open
 * @descriptor
 */
public class DataSourceFactory {

    public static AbstractDatasource produce(DataSourcePo dataSource, ApplicationContext context) {
        AbstractDatasource datasource = null;
        DatasourceTypeEnum dbType = dataSource.getDbType();
        if (DatasourceTypeEnum.MYSQL.equals(dbType)) {
            datasource = new MysqlDatasource();
        } else if (DatasourceTypeEnum.ORACLE.equals(dbType)) {
            datasource = new OracleDatasource();
        } else if (DatasourceTypeEnum.HBASE.equals(dbType)) {
            datasource = new HbaseDatasource();
        } else if (DatasourceTypeEnum.HIVE.equals(dbType)) {
            datasource = new HiveDatasource();
        } else if (DatasourceTypeEnum.SQLSERVER.equals(dbType)) {
            datasource = new SqlServerDatasource();
        } else if (DatasourceTypeEnum.PHOENIX.equals(dbType)) {
            datasource = new PhoenixDatasource();
        } else if (DatasourceTypeEnum.DREMIO.equals(dbType)) {
            datasource = new DremioDatasource();
        }
        if (datasource == null) {
            throw new RuntimeException("该数据库还未实现");
        }
        datasource.setApplicationContext(context);
        datasource.setDataSourcePo(dataSource);
        return datasource;
    }
}
