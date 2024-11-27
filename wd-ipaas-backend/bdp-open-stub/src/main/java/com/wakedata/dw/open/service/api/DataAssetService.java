package com.wakedata.dw.open.service.api;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.common.redis.lock.module.LockInfo;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnVO;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yiyufeng
 * @title DataAssetService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface DataAssetService {

    /**
     * 列出资产数据表
     * @param dataSourcePo
     * @return
     */
    List<DatasourceTableDo> listDataAssetTable(DataSourcePo dataSourcePo);

    /**
     * 列出资产数据表
     *
     * @param dataSourceId
     * @return
     */
    List<DatasourceTableDo> listDataAssetTable(Integer dataSourceId);


    /**
     * 支持模糊搜索和分页
     * @param dataSourcePo
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<DatasourceTableDo> listDataAssetTablePaged(DataSourcePo dataSourcePo, String keyword, Integer pageNo, Integer pageSize);

    /**
     * 列出数据资产表的列
     * @param dataSourcePo
     * @param dataAssetApiName
     * @return
     */
    List<DatasourceTableColumnDo> listDataAssetTableColumn(DataSourcePo dataSourcePo, String dataAssetApiName);

    /**
     * 列出数据资产表的列
     *
     * @param dataSourceId
     * @param tableName
     * @return
     */
    List<DatasourceTableColumnDo> listDataAssetTableColumns(Integer dataSourceId, String tableName);

    /**
     * 列出数据资产表的列V2，QUERY类型增加默认分页参数，HBASE数据源类型做了兼容处理
     *
     * @param dataSourceId 数据源id
     * @param tableName 表名
     * @param operationType 数据表能力类型（INSERT、DELETE、UPDATE、QUERY）
     * @return 列信息数组
     */
    DatasourceTableColumnVO listDataAssetTableColumnsV2(Integer dataSourceId, String tableName, DataAssetEnums.DataApiOperationType operationType);

    /**
     * 列出数据资产的列
     * @param dataSourcePo
     * @param dataAssetApiNameList
     * @return
     */
    Map<String, List<DatasourceTableColumnDo>> listDataAssetTableColumn(DataSourcePo dataSourcePo, List<String> dataAssetApiNameList);

    /**
     * 读取数据资产的数据
     *
     * @param dataSourcePo   数据源
     * @param tableName      表名
     * @param selectField    查询字段集合
     * @param whereCondition where条件参数
     * @param page           分页参数，页码
     * @param size           分页参数，每页查询条数
     * @param orderBy        order by条件
     * @param lockInfo       Redis锁参数对象
     * @return 查询结果
     */
    <T> T readDataAssetData(DataSourcePo dataSourcePo, String tableName, Set<String> selectField, JSONObject whereCondition,
                            int page, int size, String orderBy, LockInfo lockInfo);


    /**
     * 增加数据资产的数据
     *
     * @param dataSourcePo  数据源
     * @param tableName     表名
     * @param addCondition  请求参数
     * @param apiConditions 请求参数信息集合
     * @param lockInfo      Redis锁参数对象
     * @return 新增结果
     */
    <T> T addDataAssetData(DataSourcePo dataSourcePo, String tableName, JSONObject addCondition, List<ApiConditionPo> apiConditions, LockInfo lockInfo);

    /**
     * 修改数据资产的数据
     *
     * @param dataSourcePo    数据源
     * @param tableName       表名
     * @param updateCondition set字段参数
     * @param whereCondition  where字段参数
     * @param apiConditions   请求参数信息集合
     * @param lockInfo        Redis锁参数对象
     * @return 修改条数
     */
    <T> T updateDataAssetData(DataSourcePo dataSourcePo, String tableName, JSONObject updateCondition, JSONObject whereCondition,
                              List<ApiConditionPo> apiConditions, LockInfo lockInfo);


    /**
     * 删除数据资产的数据
     *
     * @param dataSourcePo   数据源
     * @param tableName      表名
     * @param whereCondition where条件参数
     * @param lockInfo       Redis锁参数对象
     * @return 删除条数
     */
    <T> T deleteDataAssetData(DataSourcePo dataSourcePo, String tableName, JSONObject whereCondition, LockInfo lockInfo);


    /**
     * 分页时获取记录总数
     * @param dataSourcePo
     * @param dataAssetApi
     * @param whereCondition
     * @return
     */
    int getDataCount(DataSourcePo dataSourcePo, DataAssetApiPo dataAssetApi, JSONObject whereCondition);

    /**
     * 分页时获取记录总数（无API信息）
     * @param dataSourcePo 数据源对象
     * @param sql SQL语句
     * @param tableName 数据表名
     * @param dataApiType api类型
     * @param apiConditionPoList 请求参数列表
     * @param whereCondition where条件
     * @return
     */
    int getDataCount(DataSourcePo dataSourcePo, String sql, String tableName, DataAssetEnums.DataApiType dataApiType, List<ApiConditionPo> apiConditionPoList, JSONObject whereCondition);

    /**
     * 读取sql类型的数据资产的数据
     *
     * @param dataSourcePo       数据源
     * @param apiSql             api sql
     * @param strings            资产列
     * @param apiConditionPoList api参数列表
     * @param params             请求参数列表
     * @param page               分页
     * @param size               分页大小
     * @param orderBy            排序
     * @param lockInfo           Redis锁参数对象
     * @return 数据集
     */
    <T> T readDataAssetDataBySql(DataSourcePo dataSourcePo, String apiSql, HashSet<String> strings, List<ApiConditionPo> apiConditionPoList, JSONObject params,
                                 int page, int size, String orderBy, LockInfo lockInfo);

}

