package com.wakedata.dw.open.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.utils.jsqlparser.*;
import com.wakedata.dw.open.utils.jsqlparser.builder.JSqlParserTableBuilder;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.util.NutMap;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yiyufeng tanzhi
 * @title AbstractJdbcDatasource
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
public abstract class AbstractJdbcDatasource extends AbstractDatasource {

    /**
     * 分号
     */
    protected final static String SEMICOLON = ";";

    private final static String CHAR_TYPE = "char";

    private final static String VARCHAR_TYPE = "varchar";

    private final static String STRING_TYPE = "string";

    private final static String TEXT_TYPE = "text";

    private final static String DATE_TYPE = "date";

    private final static String DATETIME_TYPE = "datetime";

    private final static String TIMESTAMP_TYPE = "timestamp";

    /**
     * 需要将参数用单引号括起来的类型集合
     */
    private final static List<String> CONVERSION_STRING_TYPE = new ArrayList<>();

    static {
        CONVERSION_STRING_TYPE.add(CHAR_TYPE);
        CONVERSION_STRING_TYPE.add(VARCHAR_TYPE);
        CONVERSION_STRING_TYPE.add(STRING_TYPE);
        CONVERSION_STRING_TYPE.add(TEXT_TYPE);
        CONVERSION_STRING_TYPE.add(DATE_TYPE);
        CONVERSION_STRING_TYPE.add(DATETIME_TYPE);
        CONVERSION_STRING_TYPE.add(TIMESTAMP_TYPE);
    }

    /**
     * RDBMS构建druid连接池连接
     *
     * @param dataSourcePo
     * @return
     */
    abstract NutDao buildJdbcDruidNutDao(DataSourcePo dataSourcePo);

    /**
     * 构建数据列对象
     *
     * @param resultMap
     * @return
     */
    abstract DatasourceTableColumnDo structureDatasourceTableColumn(NutMap resultMap);

    private static final String SQL_SEPARATION = ".";

    @Override
    public List<Map<String, Object>> readDatasourceTableData(String tableName, Set<String> selectField, JSONObject conditions, int page, int size, String orderBy) {
        List<Map<String, Object>> result = new ArrayList<>();
        SimpleCriteria criteria = Cnd.cri();
        if (!CollectionUtils.isEmpty(conditions)) {
            conditions.forEach((key, v) -> {
                key = wrapSqlKeyWord(key);
                String value = String.valueOf(v);
                if (value != null) {
                    String[] split = value.split(",");
                    if (split.length > 1) {
                        if (value.contains(",or")) {
                            criteria.where().andIn(key, Arrays.stream(split).filter(s -> !"or".equals(s)).toArray(String[]::new));
                        } else if (value.contains(",like")) {
                            criteria.where().andLike(key, "%" + value + "%");
                        } else if (value.contains(",between")) {
                            criteria.where().andBetween(key, split[0], split[1]);
                        } else if (value.contains(",less")) {
                            criteria.where().andEX(key, "<", split[0]);
                        } else if (value.contains(",grater")) {
                            criteria.where().andEX(key, ">", split[0]);
                        } else if (value.contains(",lt")) {
                            criteria.where().andEX(key, "<=", split[0]);
                        } else if (value.contains(",gt")) {
                            criteria.where().andEX(key, ">=", split[0]);
                        } else if (value.contains(",isnull")) {
                            criteria.where().andIsNull(key);
                        } else if (value.contains(",notnull")) {
                            criteria.where().andNotIsNull(key);
                        } else if (value.contains(",notequal")) {
                            criteria.where().andNotEquals(key, split[0]);
                        }
                    } else {
                        criteria.where().andEquals(key, value);
                    }
                } else {
                    criteria.where().andIsNull(key);
                }
            });
        }
        if (StringUtils.isNotBlank(orderBy)) {
            final String desc = "DESC";
            Arrays.stream(orderBy.split(";")).map(s -> s.split(",")).filter(arr -> arr.length > 1).forEach(arr -> {
                if (desc.equalsIgnoreCase(arr[1])) {
                    criteria.getOrderBy().desc(wrapSqlKeyWord(arr[0]));
                } else {
                    criteria.getOrderBy().asc(wrapSqlKeyWord(arr[0]));
                }
            });
        }
        String fields = CollectionUtils.isEmpty(selectField) ? "*" : selectField.stream()
                .map(this::wrapSqlKeyWord).collect(Collectors.joining(","));
        log.info("ReadDatasourceTableData from table:{}, fields:{}, condition:{}", tableName, fields, criteria);
        getJdbcNutDao().each(wrapSqlKeyWord(tableName), criteria, new Pager(page, size), (i, record, i1) -> result.add(new HashMap<>(record)), fields);
        return result;
    }


    protected JSqlParserWhereHelper whereConditions(
        Table table, JSONObject conditions, JSqlParserWhereHelper sqlParserHelper) {

        if (CollectionUtils.isEmpty(conditions)) {
            return sqlParserHelper;
        }

        for (Map.Entry<String, Object> mapEntry : conditions.entrySet()) {
            String value = String.valueOf(mapEntry.getValue());
            String[] split = value.split(",");
            String column = wrapSqlKeyWord(mapEntry.getKey());

            if (split.length > 1) {
                if (value.contains(",or")) {
                    //请求中如果有aaa=a1,a2这样的，变成or条件
                    List<Expression> list = new ArrayList<>();
                    for (int i = 0; i < split.length - 1; i++) {
                        if (StringUtils.isNotEmpty(split[i])) {
                            EqualsTo equalsTo = new EqualsTo();
                            equalsTo.setLeftExpression(new Column(mapEntry.getKey()));
                            equalsTo.setRightExpression(new StringValue(split[i]));
                            list.add(equalsTo);
                        }
                    }
                    sqlParserHelper.multiOr(list, false);
                } else if (value.contains(",like")) {
                    //如果有",like"，则判定为模糊查询
                    sqlParserHelper.contain(table, column, split[0], false);
                } else if (value.contains(",between")) {
                    sqlParserHelper.between(table, column, split[0], split[1]);
                } else if (value.contains(",less")) {
                    sqlParserHelper.less(table, column, split[0]);
                } else if (value.contains(",grater")) {
                    sqlParserHelper.greater(table, column, split[0]);
                } else if (value.contains(",lt")) {
                    sqlParserHelper.lessorequal(table, column, split[0]);
                } else if (value.contains(",gt")) {
                    sqlParserHelper.greater(table, column, split[0]);
                } else if (value.contains(",isnull")) {
                    sqlParserHelper.isnull(table, column, false);
                } else if (value.contains(",notnull")) {
                    sqlParserHelper.notnull(table, column, false);
                } else if (value.contains(",notequal")) {
                    sqlParserHelper.notequal(table, column, split[0]);
                } else {
                    sqlParserHelper.in(table, column, Arrays.asList(split));
                }
            } else {
                //数组类型直接取第一个值
                Object columnValue = mapEntry.getValue();
                if ((columnValue instanceof List) && (!CollectionUtils.isEmpty((Collection<?>) columnValue))) {
                    value = String.valueOf(((List<?>) columnValue).get(0));
                }
                sqlParserHelper.equal(table, column, value);
            }
        }

        return sqlParserHelper;
    }


    protected Map<String,String> parseJson(JSONObject conditions) {
        Map<String,String> result = new HashMap<>();
        for (Map.Entry<String, Object> mapEntry : conditions.entrySet()) {
            String value = String.valueOf(mapEntry.getValue());
            String column = wrapSqlKeyWord(mapEntry.getKey());
            result.put(column,value);
        }
        return result;
    }


    public NutDao getJdbcNutDao() {
        DataSourcePo dataSourcePo = this.dataSourcePo;
        String key = JdbcNutDaoHolder.buildNutDaoKey(dataSourcePo);
        return JdbcNutDaoHolder.getNutDaoCache().computeIfAbsent(key, s -> buildJdbcDruidNutDao(AbstractJdbcDatasource.this.dataSourcePo));
    }

    protected String wrapSqlKeyWord(String keyword) {
        if (keyword.contains(DwOpenConstant.FILTER_PREFIX)) {
            keyword = keyword.replace(DwOpenConstant.FILTER_PREFIX, "");
        }
        if (keyword.contains(SQL_SEPARATION)) {
            String[] keyArgs = keyword.split("\\.");
            return Arrays.stream(keyArgs).map(s -> getJdbcNutDao().getJdbcExpert().wrapKeywork(s, true))
                    .collect(Collectors.joining(SQL_SEPARATION));
        }
        return this.getJdbcNutDao().getJdbcExpert().wrapKeywork(keyword, true);
    }


    protected String parseStandardSql(
            String tableName,
            Set<String> selectFieldList,
            JSONObject whereCondition,
            String orderBy,
            Integer pageNo,
            Integer pageSize) {
        JSqlParserSelectHelper jSqlParserSelectHelper = new JSqlParserSelectHelper();
        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
        jSqlParserSelectHelper.setFromTable(table);
        if (!org.springframework.util.CollectionUtils.isEmpty(selectFieldList)) {
            for (String selectField : selectFieldList) {
                jSqlParserSelectHelper.selectItem(table, selectField);
            }
        }
        JSqlParserWhereHelper whereHelper = new JSqlParserWhereHelper();
        whereHelper = whereConditions(table, whereCondition, whereHelper);
        jSqlParserSelectHelper.setWhere(whereHelper.getWhere());
        jSqlParserSelectHelper.setHaving(whereHelper.getHaving());
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
        jSqlParserSelectHelper.limit(pageNo, pageSize);
        return jSqlParserSelectHelper.toSql();

    }


    protected String parseInsertSql(
            String tableName,
            JSONObject addCondition,NutDao nutDao,
            List<ApiConditionPo> apiConditions) {
        JSqlParserInsertHelper jSqlParserInsertHelper = new JSqlParserInsertHelper();
        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
        jSqlParserInsertHelper.setFromTable(table);

        Map<String, String> paramType = new HashMap<>();
        for (ApiConditionPo apiCondition : apiConditions) {
            paramType.put(apiCondition.getAssetColumns(),apiCondition.getAssetDatatype());
        }
        jSqlParserInsertHelper.buildColumnsAndExpression(addCondition, nutDao, paramType);
        return jSqlParserInsertHelper.toSql();
    }

    protected String parseUpdateSql(
            String tableName,
            JSONObject updateConditions,JSONObject whereConditions,NutDao nutDao, List<ApiConditionPo> apiConditions) {
        JSqlParserUpdataHelper jSqlParserUpdataHelper = new JSqlParserUpdataHelper();
        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
        jSqlParserUpdataHelper.setFromTable(table);
        Map<String, String> paramType = new HashMap<>();
        for (ApiConditionPo apiCondition : apiConditions) {
            paramType.put(apiCondition.getAssetColumns(),apiCondition.getAssetDatatype());
        }
        jSqlParserUpdataHelper.buildColumnsAndExpression(updateConditions, nutDao, paramType);
        JSqlParserWhereHelper whereHelper =  new JSqlParserWhereHelper();
        whereHelper = whereConditions(table, whereConditions, whereHelper);
        jSqlParserUpdataHelper.setWhere(whereHelper.getWhere());
        return jSqlParserUpdataHelper.toSql();
    }

    protected String parseDeleteSql(
            String tableName,JSONObject whereConditions) {
        JSqlParserDeleteHelper jSqlParserDeleteHelper = new JSqlParserDeleteHelper();
        Table table = JSqlParserTableBuilder.builder()
                .table(tableName)
                .build();
        jSqlParserDeleteHelper.setFromTable(table);
        JSqlParserWhereHelper whereHelper =  new JSqlParserWhereHelper();
        whereHelper = whereConditions(table, whereConditions, whereHelper);
        jSqlParserDeleteHelper.setWhere(whereHelper.getWhere());
        return jSqlParserDeleteHelper.toSql();
    }

    /**
     * 检查sql语句是否存在分号，存在则去掉
     * @param sql 原sql
     * @return 替换后sql
     */
    protected String replaceSemicolon(String sql) {
        return sql.indexOf(SEMICOLON) > 0 ? sql.replace(SEMICOLON, "") : sql;
    }

    /**
     * 组装order by条件
     *
     * @param sql     查询语句
     * @param orderBy order by条件
     * @return 拼接order by条件后的sql语句
     */
    protected String assembleOrderByCondition(String sql, String orderBy) {
        List<String> orders = new ArrayList<>();
        if (StringUtils.isNotBlank(orderBy)) {
            Arrays.stream(orderBy.split(";")).map(s -> s.split(","))
                    .filter(arr -> arr.length > 1).map(arr -> arr[0] + " " + arr[1]).forEach(orders::add);
        }
        if (!CollectionUtils.isEmpty(orders)) {
            return sql + " ORDER BY " + StringUtils.join(orders, ",");
        }
        return sql;
    }

    /**
     * 执行查询SQL并返回结果集
     *
     * @param apiSql 执行的sql语句
     * @param fields sql返回字段集合
     * @param page   分页参数，页码
     * @param size   分页参数，每页条数
     * @return sql执行结果
     */
    protected List<Map<String, Object>> executeQuerySql(String apiSql, Set<String> fields, int page, int size) {
        List<Map<String, Object>> result = new ArrayList<>();
        Sql executeSql = Sqls.create(apiSql).setCallback((conn, rs, sql) -> {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>(16);
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

        executeSql.setPager(new Pager(page, size));
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(executeSql);
        return result;
    }

    static void assembleSource(DruidDataSource source) throws SQLException {
        source.setMaxActive(30);
        source.setConnectionErrorRetryAttempts(3);
        source.setInitialSize(1);
        source.setMaxWait(120000);
        source.setMinIdle(1);
        source.setTimeBetweenEvictionRunsMillis(60000);
        source.setMinEvictableIdleTimeMillis(60000);
        source.setValidationQuery("select 'x'");
        source.setTestOnBorrow(true);
        source.setValidConnectionChecker(new MySqlValidConnectionChecker());
        source.setTestOnReturn(true);
        source.setTestWhileIdle(true);
        source.setPoolPreparedStatements(true);
        source.setMaxOpenPreparedStatements(20);
        source.setQueryTimeout(30);
        source.setFilters("stat");
        Properties p = new Properties();
        p.put("keepAlive", "true");
        source.setConnectProperties(p);
    }

    @Override
    public List<Map<String, Object>> readDatasourceTableDataBySql(String apiSql, Set<String> fields,List<ApiConditionPo> apiConditionPoList, JSONObject params,
                                                                  int page, int size, String orderBy) {
        apiSql = patternMatchSql(replaceSemicolon(apiSql), params);
        apiSql = assembleOrderByCondition(apiSql, orderBy);
        log.info("execute custom sql for api , the execute sql is: " + apiSql);
        return executeQuerySql(apiSql, fields, page, size);
    }

    @Override
    public int getDataCountBySql(String apiSql,List<ApiConditionPo> apiConditionPoList, JSONObject conditions) {
        // 检查sql语句是否存在分号，存在则去掉
        if (apiSql.indexOf(SEMICOLON) > 0) {
            apiSql = apiSql.replace(SEMICOLON, "");
        }
        apiSql = patternMatchSql(apiSql,conditions);
        apiSql = "SELECT COUNT(*) FROM (" + apiSql + ") a";
        Sql sql = Sqls.create(apiSql).setCallback(Sqls.callback.longs());
        NutDao jdbcNutDao = this.getJdbcNutDao();
        jdbcNutDao.execute(sql);
        return (int) sql.getLong();
    }

    protected String patternMatchSql(String apiSql, JSONObject params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String regex = "\\$\\{" + entry.getKey() + "\\S+}";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(apiSql);
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String[] group = m.group().replaceAll("(\\$\\{)|(})", "").split(":");
                if (group.length != 2) {
                    throw new OpenException(MsgCodeEnum.w_api_sql_param_error);
                }
                String columnType = group[1];
                m.appendReplacement(sb, wrapColumnValue(Sqls.escapeSqlFieldValue(String.valueOf(entry.getValue())).toString(), columnType));
            }
            m.appendTail(sb);
            apiSql = sb.toString();
        }
        return apiSql;
    }

    private String wrapColumnValue(String value, String columnType) {
        if (CONVERSION_STRING_TYPE.contains(columnType.toLowerCase())) {
            return String.format("'%s'", value);
        }
        return value;
    }

}
