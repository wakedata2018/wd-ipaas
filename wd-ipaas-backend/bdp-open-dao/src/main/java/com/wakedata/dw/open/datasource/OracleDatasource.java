package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author tanzhi
 * @title OracleDatasource
 * @date 2019/11/18 20:33
 * @projectName bdp-open
 * @descriptor
 */
@Slf4j
public class OracleDatasource extends AbstractJdbcDatasource {

    private static final String FILED_PATTERN = "^\\w{1,100}[\\\\.]{0,1}\\w{0,100}$";

    private static final String[] SYS_SCHEMAS = new String[]{
            "SYS", "SYSTEM", "WMSYS", "EXFSYS", "CTXSYS", "XDB", "MDSYS", "OLAPSYS", "ORDSYS", "SYSMAN",
            "APEX_030200", "SCOTT", "ORDDATA", "DBSNMP", "APPQOSSYS", "OUTLN", "FLOWS_FILES", "OWBSYS"
    };


    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到oracle的驱动类");
        }

    }
    @Autowired
    private StringEncryptor encryptor;


    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT * FROM ALL_TAB_COMMENTS $condition ").setCondition(tblCommonCondition())
                .setCallback(Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("OWNER") + "." + resultMap.getString("TABLE_NAME"));
            datasourceTableDo.setDatasourceTableDesc(resultMap.getString("COMMENTS"));
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    @Override
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        String sqlClause = "SELECT T.* FROM ALL_TAB_COMMENTS T " + tblCommonCondition().toSql(null);
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(getDataSourcePo().getDbName(), sqlClause, keyword, pageNo, pageSize, Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        Page<DatasourceTableDo> resultList = new Page<>();
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("OWNER") + "." + resultMap.getString("TABLE_NAME"));
            datasourceTableDo.setDatasourceTableDesc(resultMap.getString("COMMENTS"));
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    @Override
    public Long getDataSourceTableCount(String keyword) {
        String sqlClause = "SELECT COUNT(*) FROM ALL_TAB_COMMENTS T " + tblCommonCondition().toSql(null);
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(getDataSourcePo().getDbName(), sqlClause, keyword, null, null, Sqls.callback.longs());
        jdbcNutDao.execute(executeSql);
        Long total = executeSql.getLong();
        return total;
    }

    @Override
    public List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT T1.TABLE_NAME,T1.COLUMN_NAME,T1.DATA_TYPE, T2.COMMENTS, T1.OWNER FROM ALL_TAB_COLUMNS   T1 " +
                "LEFT JOIN   ALL_COL_COMMENTS   T2 ON T1.OWNER=T2.OWNER AND T1.TABLE_NAME=T2.TABLE_NAME AND T1.COLUMN_NAME=T2.COLUMN_NAME" +
                " WHERE $OwnerCondition AND T1.TABLE_NAME =@tableName")
                .setCallback(Sqls.callback.maps());
        String[] strings = tableName.split("\\.");
        if (strings.length > 1){
            executeSql.setVar("OwnerCondition", " T1.OWNER='"+strings[0] + "'");
            executeSql.setParam("tableName", strings[1]);
        } else {
            executeSql.params()
                    .set("tableName", tableName);
        }
        jdbcNutDao.execute(executeSql);
        log.info("oracle执行的SQL:{}", executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableColumnDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            resultList.add(this.structureDatasourceTableColumn(resultMap));
        }
        return resultList;
    }

    @Override
    public Map<String, List<DatasourceTableColumnDo>> listDatasourceTableColumn(List<String> tableNameList) {

        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT T1.TABLE_NAME,T1.COLUMN_NAME,T1.DATA_TYPE, T2.COMMENTS , T1.OWNER FROM USER_TAB_COLUMNS   T1 " +
                "LEFT JOIN   USER_COL_COMMENTS   T2 ON T1.TABLE_NAME=T2.TABLE_NAME AND T1.COLUMN_NAME=T2.COLUMN_NAME");
        Cnd cnd = Cnd.NEW();
        cnd.where().and("T1.TABLE_SCHEMA", "=", getDataSourcePo().getDbName())
                .andInStrList("T1.TABLE_NAME", tableNameList);
        executeSql.setCondition(cnd).setCallback(Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        Map<String, List<DatasourceTableColumnDo>> tableColumnMap = new HashMap<>(tableNameList.size());
        for (NutMap resultMap : resultMapList) {
            DatasourceTableColumnDo datasourceTableColumn = this.structureDatasourceTableColumn(resultMap);
            List<DatasourceTableColumnDo> datasourceTableColumnList = tableColumnMap.computeIfAbsent(datasourceTableColumn.getDatasourceTableName(), new Function<String, List<DatasourceTableColumnDo>>() {
                @Override
                public List<DatasourceTableColumnDo> apply(String s) {
                    return new ArrayList<>();
                }
            });
            datasourceTableColumnList.add(datasourceTableColumn);
        }
        return tableColumnMap;
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

/*    @Override
    public List<Map<String, Object>> readDatasourceTableData(String tableName, Set<String> selectField, Map<String, String> whereCondition, int page, int size, String orderBy) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        JSqlParserSelectHelper jSqlParserSelectHelper = new JSqlParserSelectHelper();
        jSqlParserSelectHelper.setOracle(true);
        Table table = JSqlParserTableBuilder.builder().table(tableName).build();
        jSqlParserSelectHelper.setFromTable(table);
        if (!org.springframework.util.CollectionUtils.isEmpty(selectField)) {
            for (String field : selectField) {
                jSqlParserSelectHelper.selectItem(table, field);
            }
        }
        jSqlParserSelectHelper = whereConditions(table, whereCondition, jSqlParserSelectHelper);
        //order by 请求参数：   column,asc;column,desc;
        if (StringUtils.isNotBlank(orderBy)) {
            String[] orderByParam = orderBy.split(";");
            if (orderByParam != null && orderByParam.length > 0) {
                List<OrderByElement> list = new ArrayList<>();
                for (int i = 0; i < orderByParam.length; i++) {
                    String[] split = orderByParam[i].split(",");
                    OrderByElement orderByElement = new OrderByElement();
                    orderByElement.setExpression(new Column(split[0]));
                    String order = split[1];
                    if ("DESC".equalsIgnoreCase(order)) {
                        orderByElement.setAsc(false);
                    } else {
                        orderByElement.setAsc(true);
                    }
                    list.add(orderByElement);
                }
                jSqlParserSelectHelper.setOrderBy(list);
            }
        }
        jSqlParserSelectHelper.limit(page, size);
        Sql executeSql = Sqls.create(jSqlParserSelectHelper.toSql())
                .setCallback(Sqls.callback.maps());
        log.info("oracle执行的sql: {}", executeSql.toString());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<Map<String, Object>> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap nutMap : resultMapList) {
            resultList.add(new HashMap<>(nutMap));
        }
        return resultList;
    }*/

    @Override
    public int getDataCount(String tableName, JSONObject whereConditions) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        JSqlParserSelectHelper jSqlParserSelectHelper = new JSqlParserSelectHelper();
        Table table = JSqlParserTableBuilder.builder().table(tableName).build();
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
    public boolean testConnection(DataSourcePo dataSourcePoConfigPo) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("select 'x' from dual");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return "x".equals(resultSet.getString(1));
            }
        } catch (SQLException e) {
            log.error("oracle连接异常", e);
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
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setUseUnfairLock(false);
        druidDataSource.setValidationQuery("select 'x' from dual");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        return new NutDao(druidDataSource);
    }


    @Override
    DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap) {
        DatasourceTableColumnDo datasourceTableColumn = new DatasourceTableColumnDo();
        datasourceTableColumn.setDatasourceTableName(resultMap.getString("OWNER") + "." + resultMap.getString("TABLE_NAME"));
        datasourceTableColumn.setDatasourceTableColumnName(resultMap.getString("COLUMN_NAME"));
        datasourceTableColumn.setDatasourceTableColumnType(resultMap.getString("DATA_TYPE"));
        datasourceTableColumn.setDatasourceTableColumnDesc(resultMap.getString("COMMENTS"));
//        datasourceTableColumn.setDatasourceTableColumnLength(resultMap.getString("CHARACTER_MAXIMUM_LENGTH"));
        return datasourceTableColumn;
    }


    private Sql getLikePagedExecuteSql(String dbName, String selectClause, String keyword, Integer pageNo, Integer pageSize, SqlCallback callback) {
        List<String> fields = Arrays.asList("T.COMMENTS", "T.TABLE_NAME");
        StringBuilder builder = new StringBuilder(selectClause);
        if (CollectionUtils.isNotEmpty(fields) && StringUtils.isNotBlank(keyword)) {
            keyword = '%' + keyword + "%";
            builder.append(" and (");
            int index = 0;
            for (String field : fields) {
                boolean matches = Pattern.matches(FILED_PATTERN, field);
                if (matches) {
                    if (index == fields.size() - 1) {
                        builder.append(" ").append(field).append(" LIKE").append("@keyword");
                    } else {
                        builder.append(" ").append(field).append(" LIKE").append("@keyword").append(" OR ");

                    }
                } else {
                    throw new OpenException("检测到SQL注入");
                }
                index++;
            }
            builder.append(" ) ");
        }
        String sql = builder.toString();

        Integer max = 0;
        Integer offset = 0;
        if (pageNo != null && pageSize != null) {
            offset = Math.max((pageNo - 1) * pageSize, 0);
            max = offset + pageSize;

            if (sql.contains("WHERE")) {
                if (sql.contains("GROUP BY ")) {
                    sql = sql.replace("GROUP BY", " AND  ROWNUM <= " + max + " ") + "GROUP BY ";
                } else if (sql.contains(" ORDER BY ")) {
                    sql = sql.replace("ORDER BY", " AND  ROWNUM <= " + max + " " + "ORDER BY ");
                } else {
                    sql = sql.replace("WHERE", " WHERE  ROWNUM <= " + max + " AND ");
                }
            } else {
                if (sql.contains("GROUP BY ")) {
                    sql = sql.replace("GROUP BY", " WHERE  ROWNUM <= " + max + " ") + "GROUP BY ";
                } else if (sql.contains(" ORDER BY ")) {
                    sql = sql.replace("ORDER BY", " WHERE  ROWNUM <= " + max + " " + "ORDER BY ");
                } else {
                    sql = sql + " WHERE  ROWNUM <= " + max;
                }
            }

            sql = sql.replace("FROM", " ,ROWNUM ROWNO FROM ");
            sql = String.format("SELECT PAGED.* FROM ( %s ) PAGED WHERE PAGED.ROWNO > %s", sql, offset.toString());
        }


        Sql executeSql = Sqls.create(sql).setCallback(callback);
        executeSql.params().set("keyword", keyword);
        log.info("NutDao 执行的SQL： {}", sql);
        return executeSql;
    }

    private Connection getConnection() {
        Connection conn = null;
        String url = JdbcNutDaoHolder.buildJdbcUrl(this.dataSourcePo);
        try {
            conn = DriverManager.getConnection(url, this.dataSourcePo.getDbUsername(), getStringEncryptor().decrypt(this.dataSourcePo.getDbPassword()));
        } catch (SQLException e) {
            log.error("获取oracle连接错误:{},{},{}", url, this.dataSourcePo.getDbUsername(), this.dataSourcePo.getDbPassword(), e);
            throw new OpenException("获取oracle连接错误");
        }
        return conn;

    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("关闭mysql连接发生异常", e);

        }
    }

    private Cnd tblCommonCondition(){
        return Cnd.where("TABLE_TYPE", "in", new String[]{"TABLE", "VIEW"}).andNot("OWNER", "in", SYS_SCHEMAS);
    }
}
