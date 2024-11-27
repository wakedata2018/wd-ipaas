package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.datasource.DataSourceMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.util.NutMap;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * @author tanzhi
 * @title HiveDatasource
 * @date 2019/11/25 11:35
 * @projectName dw-open
 * @descriptor
 */
@Slf4j
public class HiveDatasource extends AbstractJdbcDatasource {


    static {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到hive的驱动类");
        }

    }

    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        String sql = "SELECT `TBL_NAME`,`TBL_TYPE` FROM DBS  LEFT JOIN TBLS ON DBS.`DB_ID` = TBLS.`DB_ID`  WHERE DBS.`NAME`=@dbName";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create(sql)
                .setCallback(Sqls.callback.maps());
        executeSql.params().set("dbName", getDataSourcePo().getDbName());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        Page<DatasourceTableDo> resultList = new Page<>();
        for (NutMap resultMap : resultMapList) {
            String tblName = resultMap.getString("TBL_NAME");
            String tblType = resultMap.getString("TBL_TYPE");
            if (StringUtils.isNotBlank(tblName) && StringUtils.isNotBlank(tblName)) {
                DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
                datasourceTableDo.setDatasourceTableName(tblName);
                datasourceTableDo.setDatasourceTableDesc(tblType);
                resultList.add(datasourceTableDo);
            }

        }
        return resultList;
    }

    @Override
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        String sql = "SELECT `TBL_NAME`,`TBL_TYPE` FROM DBS  LEFT JOIN TBLS ON DBS.`DB_ID` = TBLS.`DB_ID`  WHERE DBS.`NAME` like concat('%',@dbName,'%')";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create(sql)
                .setCallback(Sqls.callback.maps());
        executeSql.params().set("dbName", getDataSourcePo().getDbName());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableDo> list = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("TBL_NAME"));
            datasourceTableDo.setDatasourceTableDesc(resultMap.getString("TBL_TYPE"));
            list.add(datasourceTableDo);
        }
        Page<DatasourceTableDo> resultList = new Page<>();
        resultList.addAll(list.subList(Math.max((pageNo - 1) * pageSize, 0), Math.min(pageNo * pageSize, list.size())));
        return resultList;
    }

    @Override
    public Long getDataSourceTableCount(String keyword) {
        String sql = "SELECT `TBL_NAME`,`TBL_TYPE` FROM DBS  LEFT JOIN TBLS ON DBS.`DB_ID` = TBLS.`DB_ID`  WHERE DBS.`NAME` like concat('%',@dbName,'%')";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create(sql)
                .setCallback(Sqls.callback.maps());
        executeSql.params().set("dbName", getDataSourcePo().getDbName());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        return new Long(resultMapList.size());
    }

    @Override
    public List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName) {
        String sql = "SELECT  `COLUMN_NAME`, `COMMENT` ,`TBL_NAME`,`TYPE_NAME` FROM DBS" +
                " LEFT JOIN TBLS ON DBS.DB_ID = TBLS.DB_ID" +
                " LEFT JOIN COLUMNS_V2 ON TBLS.TBL_ID = CD_ID" +
                " WHERE DBS.`NAME` = @dbName AND TBLS.`TBL_NAME` = @tableName";
        Sql executeSql = Sqls.create(sql)
                .setCallback(Sqls.callback.maps());
        NutDao jdbcNutDao = this.getJdbcNutDao();
        executeSql.params().set("dbName", getDataSourcePo().getDbName());
        executeSql.params().set("tableName", tableName);
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableColumnDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            resultList.add(this.structureDatasourceTableColumn(resultMap));
        }
        return resultList;
    }

    @Override
    public Map<String, List<DatasourceTableColumnDo>> listDatasourceTableColumn(List<String> tableNameList) {
        Map<String, List<DatasourceTableColumnDo>> map = new HashMap<>(tableNameList.size() * 4 / 3);
        for (String tableName : tableNameList) {
            List<DatasourceTableColumnDo> list = listDatasourceTableColumn(tableName);
            map.put(tableName, list);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> readDatasourceTableData(String tableName, Set<String> selectFieldList, JSONObject conditions, int page, int size, String orderBy) {
        String sql = parseStandardSql(tableName, selectFieldList, conditions, orderBy, page, size);
        Connection connection = getConnection();
        PreparedStatement statement = null;
        List<Map<String, Object>> resultList = null;

        try {
            statement = connection.prepareStatement(sql);
            log.info("mysql执行的sql: {}", sql);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            int row = resultSet.getRow();
            resultList = new ArrayList<>(row);
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                getColumn(metaData.getColumnName(i));
            }
            while (resultSet.next()) {
                Map<String, Object> objectMap = new HashMap<>(columnCount);
                for (int i = 0; i < columnCount; i++) {
                    objectMap.put(columnNames.get(i), resultSet.getString(i + 1));
                }
                resultList.add(objectMap);
            }
        } catch (SQLException e) {
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        } finally {
            closeConnection(connection);
        }
        return resultList;
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
//        Connection connection = getConnection();
//        JSqlParserSelectHelper jSqlParserSelectHelper = new JSqlParserSelectHelper();
//        Table table = JSqlParserTableBuilder.builder().table(tableName).build();
//        jSqlParserSelectHelper.setFromTable(table);
//        jSqlParserSelectHelper.setCount(" COUNT(*) ");
//        jSqlParserSelectHelper = whereConditions(table, whereConditions, jSqlParserSelectHelper);
//        String sql = jSqlParserSelectHelper.toSql();
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//            log.info("hive 查询总数的SQL:{}",sql);
//            ResultSet resultSet = statement.executeQuery(sql);
//            while (resultSet.next()) {
//                int aLong = resultSet.getInt(0);
//                return aLong;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            log.error("hive数据库执行异常", e);
//        } finally {
//            closeConnection(connection);
//        }
        return 0;
    }

    @Override
    public List<Map<String, Object>> readSqlDatasourceTableData(String sql) {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        List<Map<String, Object>> resultList = null;
        try {
            statement = connection.prepareStatement(sql);
            log.info("mysql执行的sql: {}", sql);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            int row = resultSet.getRow();
            resultList = new ArrayList<>(row);
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                columnNames.add(getColumn(metaData.getColumnName(i)));
            }
            while (resultSet.next()) {
                Map<String, Object> objectMap = new HashMap<>(columnCount);
                for (int i = 0; i < columnCount; i++) {
                    objectMap.put(columnNames.get(i), resultSet.getString(i + 1));
                }
                resultList.add(objectMap);
            }
        } catch (SQLException e) {
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        } finally {
            closeConnection(connection);
        }

        return resultList;
    }

    @Override
    public boolean testConnection(DataSourcePo dataSourcePo) {
        Connection conn = getConnection();
        try {
            PreparedStatement pstat = conn.prepareStatement("select 'x'");
            ResultSet resultSet = pstat.executeQuery();
            if (resultSet.next()) {
                String string = resultSet.getString(1);
                return "x".equals(string);
            }
        } catch (SQLException e) {
            log.error("hive jdbc 出现异常", e);
        } finally {
            closeConnection(conn);
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
            log.error("获取hive连接错误:{},{},{}", url, this.dataSourcePo.getDbUsername(), this.dataSourcePo.getDbPassword());
            throw new RuntimeException("获取hive连接错误");
        }
        return conn;

    }


    @Override
    public NutDao getJdbcNutDao() {
        DataSourceMapper bean = getApplicationContext().getBean(DataSourceMapper.class);
        DataSourcePo mysqlDatasourcePo = bean.selectByPrimaryKey(this.dataSourcePo.getChildrenId());
        String key = JdbcNutDaoHolder.buildNutDaoKey(dataSourcePo);
        return JdbcNutDaoHolder.getNutDaoCache().computeIfAbsent(key, new Function<String, NutDao>() {
            @Override
            public NutDao apply(String s) {
                return buildJdbcDruidNutDao(mysqlDatasourcePo);
            }
        });
    }

    @Override
    protected NutDao buildJdbcDruidNutDao(DataSourcePo dataSourcePo) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(JdbcNutDaoHolder.buildJdbcUrl(dataSourcePo));
        druidDataSource.setUsername(dataSourcePo.getDbUsername());
        druidDataSource.setPassword(getStringEncryptor().decrypt(dataSourcePo.getDbPassword()));
        druidDataSource.setQueryTimeout(60);
        druidDataSource.setInitialSize(0);
        druidDataSource.setMaxActive(8);
        druidDataSource.setMaxWait(60000);

        druidDataSource.setValidationQuery("select 'x'");
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setUseUnfairLock(false);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        return new NutDao(druidDataSource);
    }

    @Override
    DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap) {
        DatasourceTableColumnDo datasourceTableColumn = new DatasourceTableColumnDo();
        datasourceTableColumn.setDatasourceTableName(resultMap.getString("TBL_NAME"));
        datasourceTableColumn.setDatasourceTableColumnName(resultMap.getString("COLUMN_NAME"));
        datasourceTableColumn.setDatasourceTableColumnType(resultMap.getString("TYPE_NAME"));
        datasourceTableColumn.setDatasourceTableColumnDesc(resultMap.getString("COMMENT"));
        return datasourceTableColumn;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("关闭hive连接发生异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);

        }
    }

    private String getColumn(String columnName) {
        return columnName.substring(columnName.indexOf('.') + 1);
    }
}
