package com.wakedata.dw.open.datasource;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Stream;

//import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;

/**
 * @author tanzhi
 * @title HbaseDatasource
 * @date 2019/11/20 14:57
 * @projectName dw-open
 * @descriptor
 */
@Slf4j
public class HbaseDatasource extends AbstractDatasource {

    static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<Integer>();


    private static final String COLUMN_CHAR = ":";
    private static final String DEFAULT_CHARSET = "utf8";
    public static final String ROW_KEY = "rowkey";

    Map<String, Configuration> cacheMap = new WeakHashMap<>();


    @Override
    public List<DatasourceTableDo> listDatasourceTable() {
        Connection connection = getConnectionFromDatasourceConfig();
        try {
            Admin admin = connection.getAdmin();
            TableName[] tableNames = admin.listTableNames();
            return toDataSourceTableDoList(tableNames);
        } catch (IOException e) {
        } finally {
            closeConnection(connection);
        }
        return null;
    }

    @Override
    public Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize) {
        List<DatasourceTableDo> datasourceTableDos = listDatasourceTable();
        Stream<DatasourceTableDo> datasourceTableDoStream = datasourceTableDos.stream().filter(a -> {
            if (StringUtils.isNotEmpty(keyword)) {
                return a.getDatasourceTableName().contains(keyword);
            }
            return true;
        });
        final List<DatasourceTableDo> result = new ArrayList<>();
        try {
            datasourceTableDoStream.forEach(e -> result.add(e));
        } catch (Exception e) {
            e.printStackTrace();
            throw new OpenException("发生了异常");
        }
        Page<DatasourceTableDo> datasourceTableDos1 = new Page();
        datasourceTableDos1.addAll(result.subList(Math.max((pageNo - 1) * pageSize, 0), Math.min(pageNo * pageSize, result.size())));
        datasourceTableDos1.setTotal(result.size());
        return datasourceTableDos1;
    }

    @Override
    public Long getDataSourceTableCount(String keyword) {
        Integer count = 0;
        List<DatasourceTableDo> datasourceTableDos = listDatasourceTable();
        if (StringUtils.isNotBlank(keyword)) {
            for (DatasourceTableDo datasourceTableDo : datasourceTableDos) {
                if (datasourceTableDo.getDatasourceTableDesc().contains(keyword) || datasourceTableDo.getDatasourceTableName().contains(keyword)) {
                    count++;
                }
            }
            return new Long(count);
        } else {
            return new Long(datasourceTableDos.size());
        }
    }

    @Override
    public List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName) {
        List<DatasourceTableColumnDo> list = null;
        Connection connection = getConnectionFromDatasourceConfig();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            TableDescriptor descriptor = table.getDescriptor();
            ColumnFamilyDescriptor[] columnFamilies = descriptor.getColumnFamilies();
            list = new ArrayList<>(columnFamilies.length);
            for (ColumnFamilyDescriptor columnFamily : columnFamilies) {
                ResultScanner scanner = table.getScanner(columnFamily.getName());
                Iterator<Result> iterator = scanner.iterator();
                while (iterator.hasNext()) {
                    Result next = iterator.next();
                    Cell[] cells = next.rawCells();
                    for (Cell cell : cells) {
                        DatasourceTableColumnDo tableColumnDo = new DatasourceTableColumnDo();
                        tableColumnDo.setDatasourceTableName(tableName);
                        String columnQualifier = new String(CellUtil.cloneQualifier(cell), DEFAULT_CHARSET);
                        String columnFamilyName = new String(CellUtil.cloneFamily(cell), DEFAULT_CHARSET);
                        tableColumnDo.setDatasourceTableColumnName(buildColumnString(columnFamilyName, columnQualifier));
                        tableColumnDo.setDatasourceTableColumnType("byte");
                        list.add(tableColumnDo);
                    }
                    break;
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
        }
        return null;
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
    public List<Map<String, Object>> readDatasourceTableData(String tableName, Set<String> selectFields, JSONObject conditions, int page, int size, String orderBy) {
        String rowkey = conditions.getString(ROW_KEY);
        Get get = null;

        if (StringUtils.isBlank(rowkey)) {
//            throw new OpenException("hbase只允许rowkey查询数据");
        } else {
            get = new Get(Bytes.toBytes(rowkey));
            conditions.remove(ROW_KEY);
        }
        Connection connection = getConnectionFromDatasourceConfig();
        List<Map<String, Object>> list = null;
        try {
            list = new ArrayList<>(size);
            Table table = connection.getTable(TableName.valueOf(tableName));
            if (get == null) {
                //只允许rowkey查询
                Scan scan = new Scan();
                scan.setFilter(getFilterList(conditions));
                ResultScanner scanner = table.getScanner(scan);
                Iterator<Result> it = scanner.iterator();
                while (it.hasNext() && size > 0) {
                    Map<String, Object> resultMap = getResultMap(it.next(), selectFields);
                    if (resultMap == null) {
                        continue;
                    }
                    list.add(resultMap);
                    size--;
                }
            } else {
                list = new ArrayList<>(1);
                Result result = table.get(get);
                Map<String, Object> resultMap = getResultMap(result, selectFields);
                if (result != null) {
                    list.add(resultMap);
                }
            }
        } catch (
                IOException e) {
            log.error("读取HBASE出错", e);
            throw new OpenException("读取HBASE出错");
        } finally {
            closeConnection(connection);
        }
        THREAD_LOCAL.set(list.size());
        return list;
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
        //将上次查询的结果保存到这里，不需要再查了
        //用rowkey,一般结果不是1就是0
        Integer integer = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        return integer.intValue();
    }

    @Override
    public List<Map<String, Object>> readSqlDatasourceTableData(String executesSql) {
        return null;
    }

    @Override
    public boolean testConnection(DataSourcePo dataSourcePo) {
        Connection connection = getConnectionFromDatasourceConfig();
        if (connection == null) {
            return false;
        }
        NamespaceDescriptor[] namespaceDescriptors = null;
        try {
            namespaceDescriptors = connection.getAdmin().listNamespaceDescriptors();
            Set<String> set = new HashSet();
            for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
                set.add(namespaceDescriptor.getName());
            }
            boolean isExist = set.contains(dataSourcePo.getDbName());
            if (!isExist) {
                throw new OpenException("数据库" + dataSourcePo.getDbName() + "不存在");
            }
            return true;
        } catch (IOException e) {
            log.error("hbase无法连接", e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public List<Map<String, Object>> readDatasourceTableDataBySql(String apiSql, Set<String> fields,List<ApiConditionPo> apiConditionPoList, JSONObject params, int page, int size, String orderBy) {
        return null;
    }

    @Override
    public int getDataCountBySql(String apiSql,List<ApiConditionPo> apiConditionPoList, JSONObject conditions) {
        return 0;
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


    /**
     * 获取连接配置
     *
     * @return
     */
    private Connection getConnectionFromDatasourceConfig() {
        String key = JdbcNutDaoHolder.buildNutDaoKey(dataSourcePo);
        Configuration configuration = cacheMap.get(key);
        if (configuration == null) {
            configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", dataSourcePo.getDbHost());
            configuration.set("hbase.zookeeper.property.clientPort", String.valueOf(dataSourcePo.getDbPort()));
            configuration.set("zookeeper.znode.parent", dataSourcePo.getZkNode());
            configuration.set("hbase.client.retries.number", "10");
//            configuration.set("hbase.hconnection.threads.max","5");
            cacheMap.put(key, configuration);
        }
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            log.error(dataSourcePo.toString());
            log.error("连接HBASE异常", e);
            return null;
        }
        return connection;

    }

    private Configuration getHbaseConfiguration() {
        String key = JdbcNutDaoHolder.buildNutDaoKey(dataSourcePo);
        Configuration configuration = cacheMap.get(key);
        return configuration;
    }

    /**
     * 表集合
     *
     * @param tableNames
     * @return
     */
    private List<DatasourceTableDo> toDataSourceTableDoList(TableName[] tableNames) {
        if (tableNames == null) {
            return null;
        }
        List<DatasourceTableDo> resultList = new ArrayList<>(tableNames.length);
        for (TableName tableName : tableNames) {
            DatasourceTableDo datasourceTableDo = new DatasourceTableDo();
            datasourceTableDo.setDatasourceTableName(tableName.getNameAsString());
            datasourceTableDo.setDatasourceTableDesc(tableName.getQualifierAsString());
            resultList.add(datasourceTableDo);
        }
        return resultList;
    }

    /**
     * 返回结果集
     *
     * @param result
     * @param fields
     * @return
     * @throws UnsupportedEncodingException
     */
    private Map<String, Object> getResultMap(Result result, Set<String> fields) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
        String rowKey = Bytes.toString(result.getRow());
        for (Cell cell : result.rawCells()) {
            String columnFamily = new String(CellUtil.cloneFamily(cell), DEFAULT_CHARSET);
            String columnQualifier = new String(CellUtil.cloneQualifier(cell), DEFAULT_CHARSET);
            String columnString = buildColumnString(columnFamily, columnQualifier);
            if (!fields.contains(columnString)) {
                continue;
            }
            String value = new String(CellUtil.cloneValue(cell), DEFAULT_CHARSET);
            map.put(columnString, value);
        }
        if (map.keySet().size() == 0) {
            return null;
        }
        map.put(ROW_KEY, rowKey);
        return map;
    }

    /**
     * 设置过滤器
     *
     * @param whereCondition
     * @return
     */
    private FilterList getFilterList(JSONObject whereCondition) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        Iterator<Map.Entry<String, Object>> iterator = whereCondition.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            String[] split = key.split(COLUMN_CHAR);
            String value = String.valueOf(next.getValue());
            SingleColumnValueFilter nameFilter = new SingleColumnValueFilter(Bytes.toBytes(split[0]), Bytes.toBytes(split[1]),
                    CompareFilter.CompareOp.EQUAL, Bytes.toBytes(value));
            filterList.addFilter(nameFilter);
        }
        return filterList;
    }

    /**
     * 构建列名
     *
     * @param columnFamily
     * @param columnQualifier
     * @return
     */
    private String buildColumnString(String columnFamily, String columnQualifier) {
        return columnFamily + COLUMN_CHAR + columnQualifier;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.error("关闭连接池发生异常", e);
            e.printStackTrace();
        }
    }
}
