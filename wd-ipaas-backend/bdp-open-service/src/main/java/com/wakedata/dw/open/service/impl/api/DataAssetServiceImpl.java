package com.wakedata.dw.open.service.impl.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.common.redis.lock.exception.RedisLockTimeoutException;
import com.wakedata.common.redis.lock.lock.Lock;
import com.wakedata.common.redis.lock.lock.LockFactory;
import com.wakedata.common.redis.lock.module.LockInfo;
import com.wakedata.dw.helper.SqlBlockAttackInnerHelper;
import com.wakedata.dw.open.datasource.AbstractDatasource;
import com.wakedata.dw.open.datasource.DatasourceHolder;
import com.wakedata.dw.open.datasource.MysqlDatasource;
import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnVO;
import com.wakedata.dw.open.datasource.model.DatasourceTableDo;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import com.wakedata.dw.open.service.impl.utils.ParamBuildUtil;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.ColumnTypeConvertUtil;
import com.wakedata.dw.open.utils.jsqlparser.SqlDynamicParameterParserUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.nutz.dao.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.wakedata.dw.open.service.utils.RequestParamUtils.DEFAULT_PARAMS;

/**
 * @author yiyufeng
 * @title DataAssetServiceImpl
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
public class DataAssetServiceImpl implements DataAssetService {

    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private DatasourceHolder datasourceHolder;

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Override
    public List<DatasourceTableDo> listDataAssetTable(DataSourcePo dataSourcePo) {
        return datasourceHolder.getDataSource(dataSourcePo).listDatasourceTable();
    }

    @Override
    public List<DatasourceTableDo> listDataAssetTable(Integer dataSourceId) {
        DataSourcePo dataSourcePo = dataSourceService.show(dataSourceId);
        if (dataSourcePo == null) {
            throw new OpenException(MsgCodeEnum.w_datasource_is_not_exists);
        }
        List<DatasourceTableDo> datasourceTableDos = listDataAssetTable(dataSourcePo);
        return datasourceTableDos;
    }

    @Override
    public List<DatasourceTableColumnDo> listDataAssetTableColumns(Integer dataSourceId, String tableName) {
        DataSourcePo dataSourcePo = dataSourceService.show(dataSourceId);
        if (dataSourcePo == null) {
            throw new OpenException(MsgCodeEnum.w_datasource_is_not_exists);
        }
        List<DatasourceTableColumnDo> datasourceTableDos = listDataAssetTableColumn(dataSourcePo, tableName);
        return datasourceTableDos;
    }

    @Override
    public DatasourceTableColumnVO listDataAssetTableColumnsV2(Integer dataSourceId, String tableName, DataAssetEnums.DataApiOperationType operationType) {
        DataSourcePo dataSourcePo = dataSourceService.show(dataSourceId);
        if (dataSourcePo == null) {
            throw new OpenException(MsgCodeEnum.w_datasource_is_not_exists);
        }
        List<DatasourceTableColumnDo> datasourceTableDos = listDataAssetTableColumn(dataSourcePo, tableName);
        if (DataAssetEnums.DataApiOperationType.QUERY.equals(operationType)) {
            if (DatasourceTypeEnum.HBASE == dataSourcePo.getDbType()) {
                datasourceTableDos.add(DatasourceTableColumnDo.buildRequestParam(tableName, ApiConditionDefaultColumnEnum.ROW_KEY));
            }else {
                datasourceTableDos.add(DatasourceTableColumnDo.buildRequestParam(tableName, ApiConditionDefaultColumnEnum.PAGE_NO, DataTypeEnum.INTEGER, "1"));
                datasourceTableDos.add(DatasourceTableColumnDo.buildRequestParam(tableName, ApiConditionDefaultColumnEnum.PAGE_SIZE, DataTypeEnum.INTEGER,"10"));
            }
            datasourceTableDos.add(DatasourceTableColumnDo.buildRequestParam(tableName, ApiConditionDefaultColumnEnum.ORDER_BY));
        }
        datasourceTableDos.forEach(n-> {
            n.setDatasourceTableColumnType(ColumnTypeConvertUtil.convertParamType(n.getDatasourceTableColumnType()));
        });
        DatasourceTableColumnVO datasourceTableColumnVO = new DatasourceTableColumnVO();
        datasourceTableColumnVO.setDatasourceTableColumnDoList(datasourceTableDos);
        datasourceTableColumnVO.setBaseResponseParam(ParamBuildUtil.getDefaultPageResultParam(operationType));
        return datasourceTableColumnVO;
    }

    @Override
    public Page<DatasourceTableDo> listDataAssetTablePaged(DataSourcePo dataSourcePo, String keyword, Integer pageNo, Integer pageSize) {
        Page<DatasourceTableDo> datasourceTableDos = datasourceHolder.getDataSource(dataSourcePo).listDatasourceTable(keyword, pageNo, pageSize);
        datasourceTableDos.setTotal(datasourceHolder.getDataSource(dataSourcePo).getDataSourceTableCount(keyword));
        return datasourceTableDos;
    }

    @Override
    public List<DatasourceTableColumnDo> listDataAssetTableColumn(DataSourcePo dataSourcePo, String dataAssetApiName) {
        return datasourceHolder.getDataSource(dataSourcePo).listDatasourceTableColumn(dataAssetApiName);
    }


    @Override
    public Map<String, List<DatasourceTableColumnDo>> listDataAssetTableColumn(DataSourcePo dataSourcePo, List<String> dataAssetApiNameList) {
        return datasourceHolder.getDataSource(dataSourcePo).listDatasourceTableColumn(dataAssetApiNameList);
    }

    @Override
    public <T> T readDataAssetData(DataSourcePo dataSourcePo, String tableName, Set<String> selectField, JSONObject whereCondition,
                                   int page, int size, String orderBy, LockInfo lockInfo) {
        //去除默认参数
        RequestParamUtils.removeDefaultParams(whereCondition, RequestParamUtils.ORDER_BY, RequestParamUtils.ENABLE_LOG);
        Lock lock = null;
        try {
            // 获取锁
            lock = acquireRedisLock(lockInfo);
            List<Map<String, Object>> maps = datasourceHolder.getDataSource(dataSourcePo).readDatasourceTableData(tableName, selectField, whereCondition, page, size, orderBy);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpParamKind.BODY.name(), JSONArray.parseArray(JSON.toJSONString(maps)));
            whereCondition.put(RequestParamUtils.PAGE_NO, page);
            whereCondition.put(RequestParamUtils.PAGE_SIZE, size);
            return (T) jsonObject;
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_table_operator_error, e);
        } finally {
            releaseRedisLock(lock, lockInfo);
        }
    }

    @Override
    public <T> T addDataAssetData(DataSourcePo dataSourcePo, String tableName, JSONObject addCondition, List<ApiConditionPo> apiConditions, LockInfo lockInfo) {
        RequestParamUtils.removeDefaultParams(addCondition, RequestParamUtils.ORDER_BY, RequestParamUtils.ENABLE_LOG);
        Lock lock = null;
        try {
            // 获取锁
            lock = acquireRedisLock(lockInfo);
            Object result = datasourceHolder.getDataSource(dataSourcePo).addDatasourceTableData(tableName, addCondition, apiConditions);
            if (Objects.equals(result, -1)) {
                throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_add_table_operator_error);
            }
            return (T) result;
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_add_table_operator_error, e);
        } finally {
            releaseRedisLock(lock, lockInfo);
        }
    }

    @Override
    public <T> T updateDataAssetData(DataSourcePo dataSourcePo, String tableName, JSONObject updateCondition, JSONObject whereCondition,
                                     List<ApiConditionPo> apiConditions, LockInfo lockInfo) {
        removeDefaultParams(Arrays.asList(updateCondition, whereCondition), RequestParamUtils.ORDER_BY, RequestParamUtils.ENABLE_LOG);
        // 移除默认参数后，如果where参数不存在或where条件的值为null导致全表更新则不允许运行api
        if (whereCondition.size() == 0) {
            throw new OpenException(MsgCodeEnum.w_api_normal_table_update_where_must_not_empty);
        }
        Lock lock = null;
        try {
            // 获取锁
            lock = acquireRedisLock(lockInfo);
            Object result = datasourceHolder.getDataSource(dataSourcePo).updateDatasourceTableData(tableName, updateCondition, whereCondition, apiConditions);
            if (Objects.equals(result, -1)) {
                throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_update_table_operator_error);
            }
            return (T) result;
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_update_table_operator_error, e);
        } finally {
            releaseRedisLock(lock, lockInfo);
        }
    }

    @Override
    public <T> T deleteDataAssetData(DataSourcePo dataSourcePo, String tableName, JSONObject whereCondition, LockInfo lockInfo) {
        RequestParamUtils.removeDefaultParams(whereCondition, RequestParamUtils.ORDER_BY, RequestParamUtils.ENABLE_LOG);
        Lock lock = null;
        try {
            // 获取锁
            lock = acquireRedisLock(lockInfo);
            Object result = datasourceHolder.getDataSource(dataSourcePo).deleteDatasourceTableData(tableName, whereCondition);
            if (Objects.equals(result, -1)) {
                throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_update_table_operator_error);
            }
            return (T) result;
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_update_table_operator_error, e);
        } finally {
            releaseRedisLock(lock, lockInfo);
        }
    }


    @Override
    public int getDataCount(DataSourcePo dataSourcePo, DataAssetApiPo dataAssetApi, JSONObject whereCondition) {
        if (DataAssetEnums.DataApiType.NORMAL_TABLE.equals(dataAssetApi.getApiType())) {
            return datasourceHolder.getDataSource(dataSourcePo).getDataCount(dataAssetApi.getDataAssetName(), whereCondition);
        } else {
            List<ApiConditionPo> apiConditionPoList = openApiDataCache.getApiConditions(dataAssetApi.getDataAssetApiId());
            String transformSql = SqlDynamicParameterParserUtil.transformSql(dataAssetApi.getApiSql(), Boolean.FALSE, convertSqlParam(getDataAssetApiAllowEmptyCondition(apiConditionPoList), whereCondition));
            return datasourceHolder.getDataSource(dataSourcePo).getDataCountBySql(transformSql,getDataAssetApiAllowEmptyCondition(apiConditionPoList), whereCondition);
        }
    }

    @Override
    public int getDataCount(DataSourcePo dataSourcePo, String sql, String tableName, DataAssetEnums.DataApiType dataApiType, List<ApiConditionPo> apiConditionPoList, JSONObject whereCondition) {
        if (DataAssetEnums.DataApiType.NORMAL_TABLE.equals(dataApiType)) {
            return datasourceHolder.getDataSource(dataSourcePo).getDataCount(tableName, whereCondition);
        } else {
            String transformSql = SqlDynamicParameterParserUtil.transformSql(sql, Boolean.FALSE, convertSqlParam(getDataAssetApiAllowEmptyCondition(apiConditionPoList), whereCondition));
            return datasourceHolder.getDataSource(dataSourcePo).getDataCountBySql(transformSql,getDataAssetApiAllowEmptyCondition(apiConditionPoList), whereCondition);
        }
    }

    @Override
    public <T> T readDataAssetDataBySql(DataSourcePo dataSourcePo, String apiSql, HashSet<String> fields, List<ApiConditionPo> apiConditionPoList, JSONObject params,
                                        int page, int size, String orderBy, LockInfo lockInfo) {
        Lock lock = null;
        try {
            AbstractDatasource dataSource = datasourceHolder.getDataSource(dataSourcePo);
            String transformSql = SqlDynamicParameterParserUtil.transformSql(apiSql, Boolean.FALSE, convertSqlParam(getDataAssetApiAllowEmptyCondition(apiConditionPoList), params));
            log.info("transformSql:{}", transformSql);

            // 获取锁
            lock = acquireRedisLock(lockInfo);

            // 目前只支持MySQL使用insert、update、delete语句，其他数据库暂未实现
            if (dataSource instanceof MysqlDatasource) {
                Statement statement = CCJSqlParserUtil.parse(transformSql);
                if (statement instanceof Insert) {
                    Long primaryKey = dataSource.insertBySql(transformSql, null);
                    return (T) primaryKey;
                } else if (statement instanceof Delete) {
                    SqlBlockAttackInnerHelper.parser(statement, SqlDynamicParameterParserUtil.matchParam(apiSql), apiSql);
                    Integer updateCount = dataSource.deleteBySql(transformSql);
                    return (T) updateCount;
                } else if (statement instanceof Update) {
                    SqlBlockAttackInnerHelper.parser(statement, SqlDynamicParameterParserUtil.matchParam(apiSql), apiSql);
                    Integer updateCount = dataSource.updateBySql(transformSql);
                    return (T) updateCount;
                } else if (statement instanceof Select) {
                    List<Map<String, Object>> resultMap = dataSource.readDatasourceTableDataBySql(transformSql, fields, getDataAssetApiAllowEmptyCondition(apiConditionPoList), params, page, size, orderBy);
                    return (T) resultMap;
                }
                throw new OpenException(MsgCodeEnum.w_unsupported_sql_types);
            } else {
                List<Map<String, Object>> resultMap = dataSource.readDatasourceTableDataBySql(transformSql, fields, getDataAssetApiAllowEmptyCondition(apiConditionPoList), params, page, size, orderBy);
                return (T) resultMap;
            }
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
            OpenApiMsgCodeEnum errorEnum = OpenApiMsgCodeEnum.w_api_graph_execute_custom_operator_error;
            throw new OpenException(errorEnum.getCode(), String.format(errorEnum.getDesc(), e.getMessage()));
        } catch (JSQLParserException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_api_sql_analysis_failed);
        } finally {
            // 解锁
            releaseRedisLock(lock, lockInfo);
        }
    }


    /**
     * 转换sql参数
     * @param apiConditionPoList
     * @param params
     * @return
     */
    private JSONObject convertSqlParam(List<ApiConditionPo> apiConditionPoList,JSONObject params){
        JSONObject sqlParam = new JSONObject();
        for(ApiConditionPo apiConditionPo : apiConditionPoList){
            JSONObject value = new JSONObject();
            value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_IS_NULL_KEY, ObjectUtil.defaultIfNull(apiConditionPo.getAllowEmpty(),Boolean.FALSE));
            value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_KEY,params.get(apiConditionPo.getAssetColumns()));
            value.put(SqlDynamicParameterParserUtil.PARAM_VALUE_TYPE_KEY,apiConditionPo.getAssetDatatype());
            sqlParam.put(apiConditionPo.getAssetColumns(),value);
        }
        log.info("execute sql param :{}",sqlParam);
        return sqlParam;
    }


    /**
     * 获取api参数列表
     * @param apiConditionPoList
     * @return
     */
    private List<ApiConditionPo> getDataAssetApiAllowEmptyCondition(List<ApiConditionPo> apiConditionPoList) {
        List<ApiConditionPo> allowEmptyList = new ArrayList<>();
        if(CollUtil.isNotEmpty(apiConditionPoList)) {
            for (ApiConditionPo apiConditionPo : apiConditionPoList) {
                //添加允许动态参数，允许为空
                if (HttpParamKind.QUERY == apiConditionPo.getHttpParamKind()
                        && DataAssetEnums.FiledTypeEnums.PARAMETERS == apiConditionPo.getType()) {
                    allowEmptyList.add(apiConditionPo);
                    continue;
                }
                if (HttpParamKind.BODY == apiConditionPo.getHttpParamKind()) {
                    ApiRespParamDTO respParamDTO = JsonSchemaConvertUtil.convert(apiConditionPo.getJsonSchema());
                    if (CollUtil.isNotEmpty(respParamDTO.getChildApiRespParams())) {
                        respParamDTO.getChildApiRespParams().forEach(param -> {
                            allowEmptyList.add(ApiConditionPo.buildRequestParam(null,param.getAssetColumns(), param.getDescription(),param.getAssetDataType(),null,null,null,null,param.getAllowEmpty() ));
                        });
                    }
                }
            }
        }
        return allowEmptyList;
    }

    /**
     * 移除默认参数
     *
     * @param params    请求参数JSONObject集合
     * @param paramKeys 需要额外移除的keys
     */
    private void removeDefaultParams(List<JSONObject> params, String... paramKeys) {
        params.forEach(param -> RequestParamUtils.removeDefaultParams(param, paramKeys));
    }

    /**
     * 获取Redis锁，并返回Lock对象
     *
     * @param lockInfo Redis锁对象
     */
    private Lock acquireRedisLock(LockInfo lockInfo) {
        if (lockInfo == null) {
            return null;
        }
        Lock lock = GlobalApplicationContext.getBean(LockFactory.class).getLock(lockInfo);
        boolean acquire = lock.acquire();
        // 如果获取锁失败了，则进入失败的处理逻辑
        if (!acquire) {
            String errorMsg = String.format(MsgCodeEnum.w_acquire_redis_lock_timeout.getDesc(), lockInfo.getName(), lockInfo.getWaitTime());
            throw new RedisLockTimeoutException(errorMsg);
        }
        return lock;
    }

    /**
     * 释放Redis锁
     *
     * @param lock     Lock
     * @param lockInfo Redis锁参数对象
     */
    private void releaseRedisLock(Lock lock, LockInfo lockInfo) {
        if (lock == null) {
            return;
        }
        boolean release = lock.release();
        if (!release) {
            log.warn(String.format(MsgCodeEnum.w_release_redis_lock_timeout.getDesc(), lockInfo.getName(), lockInfo.getLeaseTime()));
        }
    }

}