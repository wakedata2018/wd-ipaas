package com.wakedata.dw.open.service.impl.xxljob;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.config.XxlJobTaskConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.XxlJobConstant;
import com.wakedata.dw.open.datasource.DatasourceHolder;
import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.xxljob.XxlJobTaskScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.nutz.dao.DaoException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author WangChenSheng
 * @descriptor 执行xxlJob调度中心相关操作
 * @title XxlJobTaskScheduleController
 * @date 2022/10/21 19:03
 */
@Service
@Slf4j
public class XxlJobTaskScheduleServiceImpl implements XxlJobTaskScheduleService {

    /**
     * xxlJob.xxl_job_info表
     */
    private static final String XXL_JOB_TABLE = "xxl_job_info";

    /**
     * xxl_job_info字段信息
     */
    private static final HashMap<String,String> XXJ_JOB_TABLE_CONDITIONS = new HashMap<>();

    private static final Set<String> SELECT_FILED = new HashSet<>();

    static {
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.JOB_GROUP,"int");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.JOB_CRON,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.JOB_DESC,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.ADD_TIME,"datetime");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.UPDATE_TIME,"datetime");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.AUTHOR,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.ALARM_EMAIL,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.EXECUTOR_ROUTE_STRATEGY,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.EXECUTOR_HANDLER,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.EXECUTOR_PARAM,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.EXECUTOR_BLOCK_STRATEGY,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.EXECUTOR_TIMEOUT,"int");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.EXECUTOR_FAIL_RETRY_COUNT,"int");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.GLUE_TYPE,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.GLUE_SOURCE,"mediumtext");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.GLUE_REMARK,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.GLUE_UPDATE_TIME,"datetime");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.CHILD_JOB_ID,"varchar");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.TRIGGER_STATUS,"tinyint");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.TRIGGER_LAST_TIME,"bigint");
        XXJ_JOB_TABLE_CONDITIONS.put(XxlJobConstant.TRIGGER_NEXT_TIME,"bigint");

        SELECT_FILED.add(XxlJobConstant.ID);
        SELECT_FILED.add(XxlJobConstant.EXECUTOR_PARAM);
    }

    @Resource
    private DatasourceHolder datasourceHolder;

    @Resource
    private XxlJobTaskConfig xxlJobTaskConfig;

    @Resource
    private StringEncryptor encryptor;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T queryXxlJobTaskScheduleInfo(Map<String, Object> queryParams) {

        DataSourcePo dataSourcePo = init();
        JSONObject whereCondition = new JSONObject(queryParams);
        List<Map<String, Object>> maps;
        try {
            maps = datasourceHolder.getDataSource(dataSourcePo).readDatasourceTableData(XXL_JOB_TABLE, SELECT_FILED, whereCondition, PageQuery.DEFAULT_PAGE_NO, PageQuery.DEFAULT_PAGE_SIZE, null);
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
            throw new OpenException("查询任务调度中心任务信息失败");
        }
        return (T) maps;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T addXxlJobTaskScheduleInfo(Map<String,Object> addParams) {
        DataSourcePo dataSourcePo = init();
        List<ApiConditionPo> apiConditionList = buildXxlJobColumns();

        JSONObject jsonObject = new JSONObject(addParams);

        Object result = datasourceHolder.getDataSource(dataSourcePo).addDatasourceTableData(XXL_JOB_TABLE, jsonObject,apiConditionList);
        if (Objects.equals(result,-1)){
            throw new OpenException("新增调度中心任务信息失败");
        }
        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T updateXxlJobTaskScheduleInfo(Map<String, Object> updateParams) {
        DataSourcePo dataSourcePo = init();
        List<ApiConditionPo> apiConditionList = buildXxlJobColumns();

        JSONObject requestParams = requestDetails(updateParams);
        JSONObject queryParams = requestParams.getJSONObject(HttpParamKind.QUERY.name());
        JSONObject whereParams = requestParams.getJSONObject(HttpParamKind.FILTER.name());

        Object result = datasourceHolder.getDataSource(dataSourcePo).updateDatasourceTableData(XXL_JOB_TABLE, queryParams,whereParams,apiConditionList);
        if (Objects.equals(result,-1)){
            throw new OpenException("更新调度中心任务信息失败");
        }
        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deleteXxlJobTaskScheduleInfo(Map<String,Object> deleteParams) {
        DataSourcePo dataSourcePo = init();
        JSONObject jsonObject = new JSONObject(deleteParams);

        Object result = datasourceHolder.getDataSource(dataSourcePo).deleteDatasourceTableData(XXL_JOB_TABLE, jsonObject);
        if (Objects.equals(result,-1)){
            throw new OpenException("删除调度中心任务信息失败");
        }
        return (T) result;
    }

    /**
     * 构建xxl-job-info字段
     */
    private List<ApiConditionPo> buildXxlJobColumns() {
        ApiConditionPo apiCondition = new ApiConditionPo();
        List<ApiConditionPo> apiConditionList = new ArrayList<>();
        XXJ_JOB_TABLE_CONDITIONS.forEach((key, value) -> {
            apiCondition.setAssetColumns(key);
            apiCondition.setAssetDatatype(value);
            apiConditionList.add(apiCondition);
        });
        return apiConditionList;
    }

    /**
     * 初始化xxlJob数据库相关参数
     */
    private DataSourcePo init(){
        DataSourcePo dataSourcePo = new DataSourcePo();
        dataSourcePo.setDbHost(xxlJobTaskConfig.getHost());
        dataSourcePo.setDbName(xxlJobTaskConfig.getDbName());
        dataSourcePo.setDbUsername(xxlJobTaskConfig.getUserName());
        dataSourcePo.setDbPassword(encryptor.encrypt(xxlJobTaskConfig.getPassword()));
        dataSourcePo.setDbPort(xxlJobTaskConfig.getPort());
        dataSourcePo.setDbType(DatasourceTypeEnum.MYSQL);

        return dataSourcePo;
    }

    /**
     * 构建相对应位置的参数
     */
    private JSONObject requestDetails(Map<String, Object> updateParams){
        JSONObject requestParams = new JSONObject();
        Map<String,Object> queryParams = new HashMap<>();
        Map<String,Object> whereParams = new HashMap<>();

        updateParams.forEach((key, value) -> {
            if (StringUtils.containsIgnoreCase(key, DwOpenConstant.FILTER_PREFIX)) {
                whereParams.put(key, value);
            } else {
                queryParams.put(key, value);
            }
        });

        requestParams.put(HttpParamKind.QUERY.name(), queryParams);
        requestParams.put(HttpParamKind.FILTER.name(), whereParams);
        return requestParams;
    }

}
