package com.wakedata.dw.open.datasource;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yiyufeng
 * @title AbstractDatasource
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
@Data
public abstract class AbstractDatasource {

    protected DataSourcePo dataSourcePo;

    private ApplicationContext applicationContext;

    private StringEncryptor stringEncryptor;

    /**
     * 列出当前数据源下所有的表
     *
     * @return
     */
    public abstract List<DatasourceTableDo> listDatasourceTable();

    /**
     * 列出当前数据源下所有的表
     *
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    public abstract Page<DatasourceTableDo> listDatasourceTable(String keyword, Integer pageNo, Integer pageSize);

    /**
     * 模糊查找匹配的表
     *
     * @param keyword
     * @return
     */
    public abstract Long getDataSourceTableCount(String keyword);

    /**
     * 找出表下面对应的列
     *
     * @param tableName
     * @return
     */
    public abstract List<DatasourceTableColumnDo> listDatasourceTableColumn(String tableName);

    /**
     * 找出数据源对应表列表下面所有的列
     *
     * @param tableNameList
     * @return
     */
    public abstract Map<String, List<DatasourceTableColumnDo>> listDatasourceTableColumn(List<String> tableNameList);

    /**
     * 读取数据源表的数据
     *
     * @param tableName
     * @param selectField
     * @param conditions
     * @param page
     * @param size
     * @param orderBy
     * @return
     */
    public abstract List<Map<String, Object>> readDatasourceTableData(
            String tableName, Set<String> selectField,
            JSONObject conditions,
            int page,
            int size,
            String orderBy
    );

    /**
     * 增加数据源表的数据
     * @param tableName
     * @param conditions
     */
    public abstract Object addDatasourceTableData(
            String tableName,
            JSONObject conditions,
            List<ApiConditionPo> apiConditions);


    /**
     * 修改数据源表的数据
     * @param tableName
     * @param updateConditions
     * @param whereConditions
     */
    public abstract Object updateDatasourceTableData(
            String tableName,
            JSONObject updateConditions,
            JSONObject whereConditions,
            List<ApiConditionPo> apiConditions);


    /**
     * 删除数据源表的数据
     * @param tableName
     * @param whereConditions
     */
    public abstract Object deleteDatasourceTableData(
            String tableName,
            JSONObject whereConditions);


    /**
     * 分页时统计符合条件数据的总籹
     *
     * @param tableName
     * @param whereConditions
     * @return
     */
    public abstract int getDataCount(String tableName, JSONObject whereConditions);

    /**
     * 传入SQL查询数据
     *
     * @param executesSql
     * @return
     */
    public abstract List<Map<String, Object>> readSqlDatasourceTableData(String executesSql);

    /**
     * 测试数据源连通性
     *
     * @param dataSourcePo
     * @return
     */
    public abstract boolean testConnection(DataSourcePo dataSourcePo);


    /**
     * 通过自定义sql拿数据集
     * @param apiSql api的自定义sql
     * @param fields 资产列
     * @param params 请求参数列表
     * @param page 分页
     * @param size 取数大小
     * @param orderBy 排序
     * @return 数据集合
     */
    public abstract List<Map<String, Object>> readDatasourceTableDataBySql(
            String apiSql,
            Set<String> fields,
            List<ApiConditionPo> apiConditionPoList,
            JSONObject params,
            int page,
            int size,
            String orderBy);

    /**
     * 查询记录数，便于分页
     * @param apiSql 自定义sql
     * @param conditions where数据
     * @return 记录数
     */
    public abstract int getDataCountBySql(String apiSql,List<ApiConditionPo> apiConditionPoList, JSONObject conditions);

    /**
     * 执行自定义insert sql
     *
     * @param sql    sql语句
     * @param params 参数列表
     * @return 新增数据主键
     */
    public abstract Long insertBySql(String sql, JSONObject params);

    /**
     * 执行自定义update sql
     *
     * @param sql sql语句
     * @return 更新影响行数
     */
    public abstract Integer updateBySql(String sql);

    /**
     * 执行自定义delete sql
     *
     * @param sql sql语句
     * @return 删除影响行数
     */
    public abstract Integer deleteBySql(String sql);

}