package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
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
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.NutSql;
import org.nutz.dao.sql.Sql;
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
public class DremioDatasource extends AbstractJdbcDatasource {

    private static final String QUERY_TABLES = "SELECT * FROM INFORMATION_SCHEMA.`TABLES`";

    private static final String QUERY_TABLE_COLUMNS = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM INFORMATION_SCHEMA.\"COLUMNS\" " +
            "WHERE TABLE_SCHEMA = @dbName AND TABLE_NAME = @tableName";


    /**
     * 加上正则防注入
     */
    private static final String FILED_PATTERN = "^\\w{1,100}$";

    static {
        try {
            Class.forName("com.dremio.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到Dremio的驱动类");
        }
    }

    @Override
    public List<Map<String, Object>> readDatasourceTableData(
            String tableName,
            Set<String> selectFields,
            JSONObject conditions,
            int page,
            int size,
            String orderBy) {
        String sql = parseStandardSql(tableName, selectFields, conditions, orderBy, page, size);
        Sql executeSql = Sqls.create(sql).setCallback(Sqls.callback.maps());
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql);
        log.info("Dremio执行的sql: {}", executeSql.toString());
        List<NutMap> resultMapList = executeSql.getList(NutMap.class);
        List<Map<String, Object>> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap nutMap : resultMapList) {
            resultList.add(new HashMap<>(nutMap));
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
    protected String parseStandardSql(
            String tableName,
            Set<String> selectFieldList,
            JSONObject whereCondition,
            String orderBy,
            Integer pageNo,
            Integer pageSize) {
        JSqlParserSelectHelper helper = new JSqlParserSelectHelper();
        Table tableWithoutDatabase = JSqlParserTableBuilder.builder()
                .table(tableName.split("\\.")[1])
                .build();

        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
        helper.setFromTable(table);
        if (!org.springframework.util.CollectionUtils.isEmpty(selectFieldList)) {
            for (String selectField : selectFieldList) {
                helper.selectItem(tableWithoutDatabase, selectField);
            }
        }
        JSqlParserWhereHelper whereHelper =  new JSqlParserWhereHelper();
        whereHelper = whereConditions(tableWithoutDatabase, whereCondition, whereHelper);
        helper.setWhere(whereHelper.getWhere());
        helper.setHaving(whereHelper.getHaving());
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
                helper.setOrderBy(list);
            }
        }

        String sql = helper.toSql();

        if (pageNo != null && pageSize != null) {
            pageNo = Math.max((pageNo - 1) * pageSize, 0);
            sql += " LIMIT " + pageSize + " OFFSET " + pageNo;
        }
        return sql;
    }

    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = Sqls.create(QUERY_TABLES).setCallback(Sqls.callback.maps());
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
        Sql executeSql = Sqls.create(QUERY_TABLE_COLUMNS).setCallback(Sqls.callback.maps());
        String[] arr = tableName.split("\\.");
        executeSql.params()
                .set("dbName", arr[0])
                .set("tableName", arr[1]);

        Sql select = new NutSql(executeSql.toString());
        List<NutMap> resultMapList = Lists.newArrayList();
        select.setCallback((conn, rs, sql) -> {
            while (rs.next()) {
                NutMap nutMap = new NutMap();
                nutMap.put("TABLE_NAME", rs.getString("TABLE_NAME"));
                nutMap.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
                nutMap.put("DATA_TYPE", rs.getString("DATA_TYPE"));
                nutMap.put("COLUMN_COMMENT", rs.getString("COLUMN_COMMENT"));
                resultMapList.add(nutMap);
            }
            return resultMapList;
        });

        jdbcNutDao.execute(select);
        log.info("Dremio执行的SQL:{}", executeSql);
        List<DatasourceTableColumnDo> resultList = new ArrayList<>(resultMapList.size());
        for (NutMap resultMap : resultMapList) {
            resultList.add(this.structureDatasourceTableColumn(resultMap));
        }
        return resultList;
    }

    @Override
    public Map<String, List<DatasourceTableColumnDo>> listDatasourceTableColumn(List<String> tableNameList) {
        NutDao jdbcNutDao = getJdbcNutDao();
        Sql executeSql = Sqls.create("SELECT * FROM INFORMATION_SCHEMA.`COLUMNS` $condition");
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
        Sql executeSql = Sqls.create(executesSql).setCallback(Sqls.callback.maps());
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
            log.error("Dremio连接异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        } finally {
            closeConnection(connection);
        }
        return false;
    }


    @Override
    public int getDataCount(String tableName, JSONObject whereConditions) {
        NutDao jdbcNutDao = this.getJdbcNutDao();
        JSqlParserSelectHelper select = new JSqlParserSelectHelper();
        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
        select.setFromTable(table);
        select.setCount(" COUNT(*) ");
        JSqlParserWhereHelper whereHelper =  new JSqlParserWhereHelper();
        whereHelper = whereConditions(table, whereConditions, whereHelper);
        select.setWhere(whereHelper.getWhere());
        select.setHaving(whereHelper.getHaving());


        Sql executeSql = Sqls.create(select.toSql()).setCallback(Sqls.callback.integer());
        log.info("统计总数的SQL", executeSql.toString());
        jdbcNutDao.execute(executeSql);
        int anInt = executeSql.getInt();
        return anInt;

    }

    @Override
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        String sqlClause = "SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_COMMENT FROM " + getDataSourcePo().getDbName() + ".INFORMATION_SCHEMA.\"TABLES\" where 1=1";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(getDataSourcePo().getDbName(), sqlClause, keyword, pageNo, pageSize);
        Sql select = new NutSql(executeSql.toString());
        List<NutMap> resultMapList = Lists.newArrayList();
        select.setCallback((conn, rs, sql) -> {
            while (rs.next()) {
                NutMap nutMap = new NutMap();
                nutMap.put("TABLE_NAME", rs.getString("TABLE_SCHEMA")+"."+rs.getString("TABLE_NAME"));
                nutMap.put("TABLE_COMMENT", rs.getString("TABLE_COMMENT"));
                resultMapList.add(nutMap);
            }
            return resultMapList;
        });
        jdbcNutDao.execute(select);
        Page<DatasourceTableDo> resultList = new Page<>();
        if (CollectionUtils.isEmpty(resultMapList)) {
            return resultList;
        }
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
        String sqlClause = "SELECT COUNT(*) FROM " + getDataSourcePo().getDbName() + ".INFORMATION_SCHEMA.\"TABLES\" where 1=1";
        NutDao jdbcNutDao = this.getJdbcNutDao();
        Sql executeSql = getLikePagedExecuteSql(getDataSourcePo().getDbName(), sqlClause, keyword, null, null);
        Sql sql = new NutSql(executeSql.toString());
        sql.setCallback(Sqls.callback.longs());
        jdbcNutDao.execute(sql);
        Long total = sql.getLong();
        return total;
    }


    @Override
    DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap) {
        DatasourceTableColumnDo datasourceTableColumn = new DatasourceTableColumnDo();
        datasourceTableColumn.setDatasourceTableName(resultMap.getString("TABLE_NAME"));
        datasourceTableColumn.setDatasourceTableColumnName(resultMap.getString("COLUMN_NAME"));
        datasourceTableColumn.setDatasourceTableColumnType(resultMap.getString("DATA_TYPE"));
        datasourceTableColumn.setDatasourceTableColumnDesc(resultMap.getString("COLUMN_COMMENT"));
//        datasourceTableColumn.setDatasourceTableColumnLength(resultMap.getString("CHARACTER_MAXIMUM_LENGTH"));
        return datasourceTableColumn;
    }

    private Sql getLikePagedExecuteSql(String dbName, String selectClause, String keyword, Integer pageNo, Integer pageSize) {
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
            builder.append(" LIMIT @size OFFSET @offset");
        }
        Sql executeSql = Sqls.create(builder.toString());
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
        druidDataSource.setValidationQuery("select 1 as t");
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
            log.error("获取Dremio连接错误:{},{},{}", url, this.dataSourcePo.getDbUsername(), this.dataSourcePo.getDbPassword(), e);
            throw new OpenException("获取Dremio连接错误");
        }
        return conn;

    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("关闭Dremio连接发生异常", e);
            throw new OpenException(MsgCodeEnum.w_sql_clause_error);
        }
    }

    @Override
    public List<Map<String, Object>> readDatasourceTableDataBySql(
            String apiSql,
            Set<String> fields,
            List<ApiConditionPo> apiConditionPoList,
            JSONObject params,
            int page,
            int size,
            String orderBy) {
        apiSql = patternMatchSql(apiSql, params);
        List<String> orders = new ArrayList<>();
        if (StringUtils.isNotBlank(orderBy)) {
            Arrays.stream(orderBy.split(";")).map(s -> s.split(","))
                    .filter(arr -> arr.length > 1).map(arr -> arr[0] + " " + arr[1]).forEach(orders::add);
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(orders)) {
            apiSql = apiSql + " ORDER BY " + StringUtils.join(orders, ",");
        }
        log.info("execute custom sql for api , the execute sql is: " + apiSql);

        List<Map<String, Object>> result = new ArrayList<>();

        int offset = (page - 1) * size;
        apiSql = apiSql + " \n" + "LIMIT  " + size + " OFFSET " + offset;

        Sql executeSql = Sqls.create(apiSql).setCallback((conn, rs, sql) -> {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (String field : fields) {
                    //如果包含“.”(eg : a."id"),就不能替换,如果不会包含(eg : "id"),就需要替换，不然取不到数
                    if (!field.contains(".")) {
                        field = field.replaceAll("\"", "");
                    }
                    Object fieldValue = rs.getObject(field);
                    map.put(field, fieldValue);
                }
                result.add(map);
            }
            return result;
        });
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql);
        return result;
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

    public static void main(String[] args) {
        String JDBC_DRIVER = "com.dremio.jdbc.Driver";
        String DB_URL = "jdbc:dremio:direct=172.31.3.91:31010;schema=hive";//浏览器访问端口号9047 ，但是api端口号是31010
        String USER = "dremio";
        String PASS = "dremio123";

//        String sql = "  select a.ID, a.TASK_NAME, b.pay_id, b.paycode \n" +
//                "from hive.\"zh_fish_shop_standard_template\".\"ods_fish_pay\" b\n" +
//                "left join \"DW_STREAMING_WHX\".\"DW_STREAMING_51_1219\".\"T_STREAM_TASK\" a on a.ID=b.pay_id ";

        String sql = "SELECT * FROM INFORMATION_SCHEMA.\"TABLES\" LIMIT 10 OFFSET 0";

        try {
            Class.forName(JDBC_DRIVER);
            Connection cc = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement ps = cc.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                System.out.println("------------------");
                System.out.println(resultSet.getString(1));
                System.out.println(resultSet.getString(2));
                System.out.println(resultSet.getString(3));
                System.out.println(resultSet.getString(4));
                System.out.println(resultSet.getString(5));
            }
            resultSet.close();
            ps.close();
            cc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
