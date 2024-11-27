package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.utils.jsqlparser.JSqlParserSelectHelper;
import com.wakedata.dw.open.utils.jsqlparser.JSqlParserWhereHelper;
import com.wakedata.dw.open.utils.jsqlparser.builder.JSqlParserTableBuilder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;

import java.sql.*;
import java.util.*;

/**
 * @author wq
 * @title SqlServerDatasource
 * @date 2020/9/23 11:13
 * @projectName dw-open
 * @description
 */
@Slf4j
public class SqlServerDatasource extends AbstractJdbcDatasource {


    static {
        try {
            Class.forName(JdbcConstants.SQL_SERVER_DRIVER_SQLJDBC4);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到sqlserver的驱动类");
        }

    }

    @Override
    NutDao buildJdbcDruidNutDao(DataSourcePo dataSourcePo) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(JdbcNutDaoHolder.buildJdbcUrl(dataSourcePo));
        druidDataSource.setUsername(dataSourcePo.getDbUsername());
        druidDataSource.setPassword(getStringEncryptor().decrypt(dataSourcePo.getDbPassword()));
        druidDataSource.setQueryTimeout(60);
        druidDataSource.setInitialSize(0);
        druidDataSource.setMaxActive(8);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setUseUnfairLock(false);
        druidDataSource.setValidationQuery("select 'x' ");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        return new NutDao(druidDataSource);
    }

    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT TABLE_SCHEMA,TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ")
                .setCallback(Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("TABLE_SCHEMA") + "." + resultMap.getString("TABLE_NAME"));
            datasourceTableDo.setDatasourceTableDesc("");
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    @Override
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        String tableSql = "SELECT TABLE_SCHEMA,TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ";
        Sql sql = getLikePagedExecuteSql(tableSql,keyword,pageNo,pageSize);
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(sql);
        List<NutMap> resultMapList = sql.getList(NutMap.class);
        Page<DatasourceTableDo> resultList = new Page<>();
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("TABLE_SCHEMA") + "." + resultMap.getString("TABLE_NAME"));
            datasourceTableDo.setDatasourceTableDesc(resultMap.getString(""));
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    private Sql getLikePagedExecuteSql(String tableSql, String keyword, Integer pageNo, Integer pageSize) {
        if (StringUtils.isNotBlank(keyword)) {
            tableSql = tableSql + "WHERE TABLE_NAME LIKE '%"+ keyword +"%'";
        }
        String sql = String .format("select top %d *   from (select row_number() over(order by t.TABLE_NAME asc) as rownumber,* " +
                "from (%s) t) temp_row where rownumber> %d ",pageSize,tableSql,(pageNo - 1) * pageSize);
        Sql executeSql = Sqls.create(sql).setCallback(Sqls.callback.maps());
        log.info("NutDao 执行的SQL： {}", sql);
        return executeSql;
    }

    @Override
    public Long getDataSourceTableCount(String keyword) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        String tableSql = "SELECT TABLE_SCHEMA,TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ";
        if (StringUtils.isNotBlank(keyword)) {
            tableSql = tableSql + "WHERE TABLE_NAME LIKE '%"+ keyword +"%'";
        }
        Sql sql = Sqls.create("SELECT count(*) from (" + tableSql + ") t").setCallback(Sqls.callback.longs());
        log.info(" NutDao 执行的SQL：{}",sql.toString());
        return jdbcNutDao.execute(sql).getLong();

    }

    @Override
    public List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        tableName = tableName.split("\\.")[1];
        Sql executeSql = Sqls.create("SELECT  obj.name AS TABLE_NAME,   " +
                "        col.name AS COLUMN_NAME ,  " +
                "         CONVERT(varchar(200), ISNULL(ep.[value], '')) AS COMMENTS ,  " +
                "        t.name AS DATA_TYPE ,  " +
                "        col.length AS LENGTH " +
                "FROM    dbo.syscolumns col  " +
                "        LEFT  JOIN dbo.systypes t ON col.xtype = t.xusertype  " +
                "        inner JOIN dbo.sysobjects obj ON col.id = obj.id  " +
                "        AND (obj.xtype = 'U'  OR obj.xtype = 'V') AND obj.status >= 0  " +
                "        LEFT  JOIN dbo.syscomments comm ON col.cdefault = comm.id  " +
                "        LEFT  JOIN sys.extended_properties ep ON col.id = ep.major_id  " +
                "        AND col.colid = ep.minor_id  AND ep.name = 'MS_Description'  " +
                "        LEFT  JOIN sys.extended_properties epTwo ON obj.id = epTwo.major_id  " +
                "        AND epTwo.minor_id = 0  AND epTwo.name = 'MS_Description'  " +
                "WHERE   obj.name = @tableName");
        Sql executeSqls = executeSql.setParam("tableName", tableName)
                .setCallback(Sqls.callback.maps());
        jdbcNutDao.execute(executeSqls);
        log.info("sqlserver执行的SQL:{}", executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableColumnDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            resultList.add(this.structureDatasourceTableColumn(resultMap));
        }
        return resultList;
    }

    @Override
    DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap) {
        DatasourceTableColumnDo datasourceTableColumn = new DatasourceTableColumnDo();
        datasourceTableColumn.setDatasourceTableName(resultMap.getString("TABLE_NAME"));
        datasourceTableColumn.setDatasourceTableColumnName(resultMap.getString("COLUMN_NAME"));
        datasourceTableColumn.setDatasourceTableColumnType(resultMap.getString("DATA_TYPE"));
        datasourceTableColumn.setDatasourceTableColumnDesc(resultMap.getString("COMMENTS"));
        datasourceTableColumn.setDatasourceTableColumnLength(resultMap.getString("LENGTH"));
        return datasourceTableColumn;
    }

    @Override
    public Map<String, List<DatasourceTableColumnDo>> listDatasourceTableColumn(List<String> tableNameList) {
        return null;
    }

    @Override
    public Boolean addDatasourceTableData(String tableName, JSONObject conditions, List<ApiConditionPo> apiConditions) {
        return null;
    }

    @Override
    public Boolean updateDatasourceTableData(String tableName, JSONObject updateConditions, JSONObject whereConditions, List<ApiConditionPo> apiConditions) {
        return null;
    }

    @Override
    public Boolean deleteDatasourceTableData(String tableName, JSONObject whereConditions) {
        return null;
    }

    @Override
    public int getDataCount(String tableName, JSONObject whereConditions) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        JSqlParserSelectHelper jSqlParserSelectHelper = new JSqlParserSelectHelper();
        Table table = JSqlParserTableBuilder.builder().table(wrapSqlKeyWord(tableName)).build();
        jSqlParserSelectHelper.setFromTable(table);
        jSqlParserSelectHelper.setCount(" COUNT(*) ");
        JSqlParserWhereHelper whereHelper =  new JSqlParserWhereHelper();
        whereHelper = whereConditions(table, whereConditions, whereHelper);
        jSqlParserSelectHelper.setWhere(whereHelper.getWhere());
        jSqlParserSelectHelper.setHaving(whereHelper.getHaving());
        Sql executeSql = Sqls.create(jSqlParserSelectHelper.toSql())
                .setCallback(Sqls.callback.integer());
        log.info("统计总数的SQL", executeSql.toString());
        jdbcNutDao.execute(executeSql);
        int anInt = executeSql.getInt();
        return anInt;
    }

    @Override
    public List<Map<String, Object>> readSqlDatasourceTableData(String executesSql) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create(executesSql)
                .setCallback(Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<Map<String, Object>> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap nutMap : resultMapList) {
            resultList.add(new HashMap<>(nutMap));
        }
        return resultList;
    }

    @Override
    public boolean testConnection(DataSourcePo dataSourcePo) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("select 'x'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return "x".equals(resultSet.getString(1));
            }
        } catch (SQLException e) {
            log.error("sqlserver连接异常", e);
            throw new OpenException(MsgCodeEnum.w_dao_search_error);
        } finally {
            closeConnection(connection);
        }
        return false;
    }

    @Override
    public Long insertBySql(String sql, JSONObject params) {
        return null;
    }

    @Override
    public Integer updateBySql(String sql) {
        return null;
    }

    @Override
    public Integer deleteBySql(String sql) {
        return null;
    }

    private Connection getConnection() {
        Connection conn = null;
        String url = JdbcNutDaoHolder.buildJdbcUrl(this.dataSourcePo);
        try {
            conn = DriverManager.getConnection(url, this.dataSourcePo.getDbUsername(), getStringEncryptor().decrypt(this.dataSourcePo.getDbPassword()));
        } catch (SQLException e) {
            log.error("获取sqlserver连接错误:{},{},{}", url, this.dataSourcePo.getDbUsername(), this.dataSourcePo.getDbPassword(),e);
            throw new OpenException("获取server连接错误");
        }
        return conn;

    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("关闭sqlserver连接发生异常", e);

        }
    }

}
