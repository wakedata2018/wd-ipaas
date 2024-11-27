package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.enums.DbSourceColumnAttributeEnums;
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
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.NutMap;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author yiyufeng
 * @title MysqlDatasource
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
public class MysqlDatasource extends AbstractJdbcDatasource {


    /**
     * 加上正则防注入
     */
    private static final String FILED_PATTERN = "^\\w{1,100}$";

    /**
     * 新增查询主键id
     */
    private static final String KEY_WORD_SQL = "SELECT LAST_INSERT_ID();";

    /**
     * 数据库列递增标识
     */
    private static final String AUTO_INCREMENT = "auto_increment";
    /**
     * 列不允许为空
     */
    private static final String IS_NULLABLE_NO = "NO";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到mysql的驱动类");
        }

    }


    @Override
    public List<Map<String, Object>> readDatasourceTableData(String tableName, Set<String> selectFieldList, JSONObject conditions, int page, int size, String orderBy) {
        String sql = parseStandardSql(tableName, selectFieldList, conditions, orderBy, page, size);
        Sql executeSql = Sqls.create(sql)
                .setCallback(Sqls.callback.maps());
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql);
        log.info("mysql执行的sql: {}", executeSql.toString());
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<Map<String, Object>> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap nutMap : resultMapList) {
            resultList.add(new HashMap<>(nutMap));
        }
        return resultList;
    }

    @Override
    public Long addDatasourceTableData(String tableName, JSONObject conditions, List<ApiConditionPo> apiConditions) {
        String sql = parseInsertSql(tableName, conditions, this.getJdbcNutDao(), apiConditions);
        Long keyWordId;
        try {
            Sql executeSql = Sqls.create(sql);
            Sql generatedKeySql = Sqls.create(KEY_WORD_SQL)
                .setCallback(Sqls.callback.longs());
            NutDao jdbcNutDao = this.getJdbcNutDao();
            jdbcNutDao.execute(executeSql, generatedKeySql);
            keyWordId = generatedKeySql.getLong();
            log.debug("mysql执行的sql: {}, 新增主键id：{}", executeSql.toString(), keyWordId);
        } catch (Exception e) {
            log.error("数据增加失败，请检查sql：" + sql, e);
            return -1L;
        }
        return keyWordId;
    }

    @Override
    public Integer updateDatasourceTableData(String tableName, JSONObject updateConditions, JSONObject whereConditions,
        List<ApiConditionPo> apiConditions) {
        if (updateConditions.isEmpty()) {
            log.warn("不能执行更新，更新参数为空：{}", updateConditions);
            return 0;
        }
        int updateCount;
        String sql = parseUpdateSql(tableName, updateConditions, whereConditions, this.getJdbcNutDao(), apiConditions);
        try {
            Sql executeSql = Sqls.create(sql);
            NutDao jdbcNutDao = this.getJdbcNutDao();
            jdbcNutDao.execute(executeSql);
            updateCount = executeSql.getUpdateCount();
            log.debug("mysql执行的sql: {}, 影响行数：{}", executeSql.toString(),updateCount);
        } catch (Exception e) {
            log.error("数据修改失败，请检查sql：" + sql, e);
            return -1;
        }
        return updateCount;
    }

    @Override
    public Integer deleteDatasourceTableData(String tableName, JSONObject whereConditions) {
        String sql = parseDeleteSql(tableName, whereConditions);
        int updateCount;
        try {
            Sql executeSql = Sqls.create(sql);
            NutDao jdbcNutDao = this.getJdbcNutDao();
            jdbcNutDao.execute(executeSql);
            updateCount = executeSql.getUpdateCount();
            log.debug("mysql执行的sql: {}, 影响行数：{}", executeSql.toString(),updateCount);
        } catch (Exception e) {
            log.error("数据删除失败，请检查sql：" + sql, e);
            return -1;
        }
        return updateCount;
    }

    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT * FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = @dbName")
                .setCallback(Sqls.callback.maps());
        executeSql.params().set("dbName", getDataSourcePo().getDbName());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<DatasourceTableDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("TABLE_NAME"));
            datasourceTableDo.setDatasourceTableDesc(resultMap.getString("TABLE_COMMENT"));
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    @Override
    public List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT * FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = @dbName AND TABLE_NAME = @tableName")
                .setCallback(Sqls.callback.maps());
        executeSql.params()
                .set("dbName", getDataSourcePo().getDbName())
                .set("tableName", tableName);
        jdbcNutDao.execute(executeSql);
        log.info("mysql执行的SQL:{}", executeSql);
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
        Sql executeSql = Sqls.create("SELECT * FROM information_schema.`COLUMNS` $condition");
        Cnd cnd = Cnd.NEW();
        cnd.where().and("TABLE_SCHEMA", "=", getDataSourcePo().getDbName())
                .andInStrList("TABLE_NAME", tableNameList);
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
            PreparedStatement statement = connection.prepareStatement("select 'x'");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return "x".equals(resultSet.getString(1));
            }
        } catch (SQLException e) {
            log.error("mysql连接异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        } finally {
            closeConnection(connection);
        }
        return false;
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
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        String sqlClause = "SELECT * FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = @dbName";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(getDataSourcePo().getDbName(), sqlClause, keyword, pageNo, pageSize, Sqls.callback.maps());
        jdbcNutDao.execute(executeSql);
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        Page<DatasourceTableDo> resultList = new Page<>();
        for (NutMap resultMap : resultMapList) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(resultMap.getString("TABLE_NAME"));
            datasourceTableDo.setDatasourceTableDesc(resultMap.getString("TABLE_COMMENT"));
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    @Override
    public Long getDataSourceTableCount(String keyword) {

        String sqlClause = "SELECT COUNT(*) FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = @dbName";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(getDataSourcePo().getDbName(), sqlClause, keyword, null, null, Sqls.callback.longs());
        jdbcNutDao.execute(executeSql);
        Long total = executeSql.getLong();
        return total;
    }


    @Override
    DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap) {
        DatasourceTableColumnDo datasourceTableColumn = new DatasourceTableColumnDo();
        datasourceTableColumn.setDatasourceTableName(resultMap.getString(DbSourceColumnAttributeEnums.DbSourceColumnAttributeEnum.TABLE_NAME.getValue()));
        datasourceTableColumn.setDatasourceTableColumnName(resultMap.getString(DbSourceColumnAttributeEnums.DbSourceColumnAttributeEnum.COLUMN_NAME.getValue()));
        datasourceTableColumn.setDatasourceTableColumnType(resultMap.getString(DbSourceColumnAttributeEnums.DbSourceColumnAttributeEnum.DATA_TYPE.getValue()));
        datasourceTableColumn.setDatasourceTableColumnDesc(resultMap.getString(DbSourceColumnAttributeEnums.DbSourceColumnAttributeEnum.COLUMN_COMMENT.getValue()));
        //判断如果是设置了递增的列，标记isAutoIncrement属性字段数据为true
        if (AUTO_INCREMENT.equals(resultMap.getString(DbSourceColumnAttributeEnums.DbSourceColumnAttributeEnum.EXTRA.getValue())) ) {
            datasourceTableColumn.setIsAutoIncrement(Boolean.TRUE);
        }else {
            datasourceTableColumn.setIsAutoIncrement(Boolean.FALSE);
        }
        //判断列是否必填
        if (IS_NULLABLE_NO.equals(resultMap.getString(DbSourceColumnAttributeEnums.DbSourceColumnAttributeEnum.IS_NULLABLE.getValue()))) {
            datasourceTableColumn.setRequired(Boolean.TRUE);
        }
        else {
            datasourceTableColumn.setRequired(Boolean.FALSE);
        }

//        datasourceTableColumn.setDatasourceTableColumnLength(resultMap.getString("CHARACTER_MAXIMUM_LENGTH"));
        return datasourceTableColumn;
    }

    @Override
    public Long insertBySql(String sql, JSONObject params) {
        log.info("execute custom insert sql for api , the execute sql is: {}", sql);
        Sql executeSql = Sqls.create(replaceSemicolon(sql));
        Sql generatedKeySql = Sqls.create(KEY_WORD_SQL)
                .setCallback(Sqls.callback.longs());
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql, generatedKeySql);
        return generatedKeySql.getLong();
    }

    @Override
    public Integer updateBySql(String sql) {
        log.info("execute custom update sql for api , the execute sql is: {}", sql);
        Sql executeSql = Sqls.create(replaceSemicolon(sql));
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql);
        return executeSql.getUpdateCount();
    }

    @Override
    public Integer deleteBySql(String sql) {
        log.info("execute custom delete sql for api , the execute sql is: {}", sql);
        Sql executeSql = Sqls.create(replaceSemicolon(sql));
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql);
        return executeSql.getUpdateCount();
    }

    @Override
    public List<Map<String, Object>> readDatasourceTableDataBySql(String apiSql, Set<String> fields,List<ApiConditionPo> apiConditionPoList, JSONObject params,
                                                                  int page, int size, String orderBy) {
        // 先去除分号再拼接order by条件
        apiSql = assembleOrderByCondition(replaceSemicolon(apiSql), orderBy);
        log.info("execute custom select sql for api , the execute sql is: {}", apiSql);
        return super.executeQuerySql(apiSql, fields, page, size);
    }

    private Sql getLikePagedExecuteSql(String dbName, String selectClause, String keyword, Integer pageNo, Integer pageSize, SqlCallback callback) {
        List<String> fields = Arrays.asList("TABLE_COMMENT", "TABLE_NAME");
        StringBuilder builder = new StringBuilder(selectClause);
        if (CollectionUtils.isNotEmpty(fields) && StringUtils.isNotBlank(keyword)) {
            keyword = '%' + keyword + "%";
            builder.append(" and (");
            int index = 0;
            for (String field : fields) {
                boolean matches = Pattern.matches(FILED_PATTERN, field);
                if (matches) {
                    if (index == fields.size() - 1) {
                        builder.append(" ").append(field).append(" like").append("@keyword");
                    } else {
                        builder.append(" ").append(field).append(" like").append("@keyword").append(" OR ");

                    }
                } else {
                    throw new OpenException("检测到SQL注入");
                }
                index++;
            }
            builder.append(" ) ");
        }
        if (pageNo != null && pageSize != null) {
            pageNo = Math.max((pageNo - 1) * pageSize, 0);
            builder.append(" limit @offset,@size");
        }
        Sql executeSql = Sqls.create(builder.toString())
                .setCallback(callback);
        executeSql.params().set("dbName", dbName);
        executeSql.params().set("keyword", keyword);
        if (pageNo != null && pageSize != null) {
            executeSql.params().set("offset", pageNo);
            executeSql.params().set("size", pageSize);
        }
        log.info("NutDao 执行的SQL： {}", executeSql.toString());
        return executeSql;
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
        druidDataSource.setValidationQuery("select 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        return new NutDao(druidDataSource);
    }

    private Connection getConnection() {
        Connection conn = null;
        String url = JdbcNutDaoHolder.buildJdbcUrl(this.dataSourcePo);
        try {
            conn = DriverManager.getConnection(url, this.dataSourcePo.getDbUsername(), getStringEncryptor().decrypt(this.dataSourcePo.getDbPassword()));
        } catch (SQLException e) {
            log.error("获取mysql连接错误:{},{},{}", url, this.dataSourcePo.getDbUsername(), this.dataSourcePo.getDbPassword(),e);
            throw new OpenException("获取mysql连接错误");
        }
        return conn;

    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("关闭mysql连接发生异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        }
    }


}
