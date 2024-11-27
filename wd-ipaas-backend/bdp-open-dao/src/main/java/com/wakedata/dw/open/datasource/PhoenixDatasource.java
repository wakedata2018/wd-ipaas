package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
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
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.NutMap;

import java.sql.*;
import java.util.*;

/**
 * @author nil
 */
@Slf4j
public class PhoenixDatasource extends AbstractJdbcDatasource {

    static {
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到Phoenix的驱动类");
        }
    }

    @Override
    NutDao buildJdbcDruidNutDao(DataSourcePo dataSourcePo) {
        DruidDataSource source = new DruidDataSource();
        try {
            source.setUrl(JdbcNutDaoHolder.buildJdbcUrl(dataSourcePo));
            assembleSource(source);
            source.setQueryTimeout(60);
            source.setDriverClassName(JdbcConstants.PHOENIX_DRIVER);
            source.init();
            source.setPoolPreparedStatements(false);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("数据库连接错误");
        }
        NutDao dao = new NutDao();
        try {
            dao.setDataSource(source);
            //nutz不支持phoenix，变通特殊处理一下，不知道会不会有bug
            dao.setExpert(Jdbcs.getExpert("hsql", "2.0"));
            dao.meta().setAsDB2();
        } catch (Exception e) {
            source.close();
            log.error("创建JDBC连接失败", e);
            throw new RuntimeException("创建JDBC连接失败");
        }
        return dao;
    }

    @Override
    DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap) {
        return null;
    }

    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        ArrayList<DatasourceTableDo> tablesList = new ArrayList<>();
        try {
            Connection connection = getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            ResultSet rs = databaseMetaData.getTables(null, null, "%", null);

            while (rs.next()) {
                DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
                datasourceTableDo.setDatasourceTableName(rs.getString("TABLE_NAME"));

                tablesList.add(datasourceTableDo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.info(JSON.toJSONString(tablesList));

        return tablesList;
    }

    @Override
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        String sqlClause = "select distinct TABLE_NAME from SYSTEM.\"CATALOG\"";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(sqlClause, keyword, pageNo, pageSize, Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        Page<DatasourceTableDo> resultList = new Page<>();
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("TABLE_NAME"));
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    private Sql getLikePagedExecuteSql(String sqlClause, String keyword, Integer pageNo, Integer pageSize, SqlCallback maps) {
        if (StringUtils.isNotBlank(keyword)) {
            sqlClause = sqlClause + " WHERE TABLE_NAME like '%" + keyword + "%'";
        }
        if (pageNo != null && pageSize != null) {
            sqlClause = sqlClause + " LIMIT " + pageSize + " OFFSET "  + (pageNo - 1)*pageSize;
        }
        Sql executeSql = Sqls.create(sqlClause).setCallback(maps);
        log.info("NutDao 执行的SQL： {}", executeSql);
        return executeSql;
    }


    @Override
    public Long getDataSourceTableCount(String keyword) {
        String sqlClause = "select count(distinct TABLE_NAME) from SYSTEM.\"CATALOG\"";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql( sqlClause, keyword, null, null, Sqls.callback.longs());
        jdbcNutDao.execute(executeSql);
        Long total = executeSql.getLong();
        return total;
    }

    @Override
    public List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName) {
        List<DatasourceTableColumnDo> resultList = new ArrayList<>();
        try {
            Connection connection = getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            ResultSet rs = databaseMetaData.getColumns(null, null, tableName, "%");
            while (rs.next()) {
                DatasourceTableColumnDo datasourceTableColumnDo = new DatasourceTableColumnDo();
                datasourceTableColumnDo.setDatasourceTableName(rs.getString("TABLE_NAME"));
                datasourceTableColumnDo.setDatasourceTableColumnName(rs.getString("COLUMN_NAME"));
                datasourceTableColumnDo.setDatasourceTableColumnType(rs.getString("TYPE_NAME"));

                resultList.add(datasourceTableColumnDo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.info(JSON.toJSONString(resultList));
        return resultList;
    }

    @Override
    public Map<String, List<DatasourceTableColumnDo>> listDatasourceTableColumn(List<String> tableNameList) {
        Map<String, List<DatasourceTableColumnDo>> map = new HashMap<>();
        for (String tableName : tableNameList) {
            List<DatasourceTableColumnDo> datasourceTableColumnDos = listDatasourceTableColumn(tableName);
            map.put(tableName, datasourceTableColumnDos);
        }

        return map;
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
        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
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
    public int getDataCountBySql(String apiSql,List<ApiConditionPo> apiConditionPoList, JSONObject conditions) {
        apiSql = patternMatchSql(apiSql, conditions);
        apiSql = "SELECT COUNT(*) FROM (" + apiSql + ")";
        Sql sql = Sqls.create(apiSql).setCallback(Sqls.callback.longs());
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(sql);
        return (int) sql.getLong();
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

    @Override
    public List<Map<String, Object>> readSqlDatasourceTableData(String executesSql) {
        return null;
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
            log.error("Phoenix连接异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        } finally {
            closeConnection(connection);
        }
        return false;
    }

    private Connection getConnection() {
        Connection conn = null;
        String url = JdbcNutDaoHolder.buildJdbcUrl(this.dataSourcePo);
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            String errorMsg = String.format("获取Phoenix连接错误:{}", url);
            log.error(errorMsg);
            throw new OpenException(errorMsg);
        }

        return conn;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("关闭Phoenix连接发生异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        }
    }

    @Override
    protected String wrapSqlKeyWord(String keyword) {
        //phoenix中如果小写，需要加“”,兼容一下
        if (!keyword.toUpperCase().equals(keyword)) {
            return "\"" + keyword + "\"";
        }
        return keyword;
    }
}
