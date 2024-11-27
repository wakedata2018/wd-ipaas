package com.wakedata.dw.open.service.impl.api.strategy.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.redis.lock.module.LockInfo;
import com.wakedata.dw.helper.RedisLockHelper;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.impl.api.strategy.DatasourceApiInvokeStrategy;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据表执行策略实现
 *
 * @author ZhangXueJun
 * @title TableApiInvokeStrategy
 * @date 2021/5/27 17:42
 * @projectName dw-open
 * @description
 */
public class TableApiInvokeStrategy<T> extends DatasourceApiInvokeStrategy<T> {
    protected DataAssetEnums.DataApiOperationType operationType;
    protected List<ApiConditionPo> apiConditions;
    public TableApiInvokeStrategy(
            DataAssetService dataAssetService,
            DataAssetEnums.DataApiType apiKind,
            DataAssetEnums.DataApiOperationType  operationType,
            DataSourcePo dataSource,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            HashSet<String> accessRuleFields,
            List<ApiConditionPo> apiConditions) {
        super(dataAssetService, apiKind, dataSource, dataAssetApi, appId, params, accessRuleFields);
        this.operationType = operationType;
        this.apiConditions = apiConditions;
    }


    @Override
    public T invoke() {
        super.setWdCloudTenantParam();
        //根据API请求方式，判断请求参数位置，取参
        JSONObject param;
        if (DataAssetEnums.ReqMethod.POST.equals(this.dataAssetApi.getReqMethod())) {
            param = requestParams.getJSONObject(HttpParamKind.BODY.name());
            // requestParams中的BODY中数据如果是String类型，下一步操作清理空值参数不会影响到requestParams里面的数据，导致最外层查询总条数时出错，所以需要将类型转换后重新赋值进去
            requestParams.put(HttpParamKind.BODY.name(), param);
        } else {
            param = requestParams.getJSONObject(HttpParamKind.QUERY.name());
        }
        this.clearNullParameter(param);

        LockInfo lockInfo;
        switch (operationType) {
            case INSERT:
                lockInfo = RedisLockHelper.buildLockInfoFromApi(param, ((RedisLockConfigAttr) dataAssetApi.getApiAttr()));
                return dataAssetService.addDataAssetData(dataSource, dataAssetApi.getDataAssetName(), param, apiConditions, lockInfo);
            case DELETE:
                lockInfo = RedisLockHelper.buildLockInfoFromApi(param, ((RedisLockConfigAttr) dataAssetApi.getApiAttr()));
                return dataAssetService.deleteDataAssetData(dataSource, dataAssetApi.getDataAssetName(), param, lockInfo);
            case UPDATE:
                //post请求下，过滤条件参数信息存放在condition表的JSONSchema中，在这个JSONSchema中分作两个对象存储请求参数和过滤条件
                if (DataAssetEnums.ReqMethod.POST.equals(this.dataAssetApi.getReqMethod())) {
                    JSONObject updateParam = param.getJSONObject(DwOpenConstant.NORMAL_TABLE_POST_PARAMETERS);
                    //清理请求参数中的空值参数
                    this.clearNullParameter(updateParam);
                    JSONObject updateWhereParam = param.getJSONObject(DwOpenConstant.NORMAL_TABLE_POST_FILTERS);
                    //清理过滤条件中的空值条件
                    this.clearNullParameter(updateWhereParam);
                    lockInfo = RedisLockHelper.buildLockInfoFromApi(Arrays.asList(updateParam, updateWhereParam), ((RedisLockConfigAttr) dataAssetApi.getApiAttr()));
                    return dataAssetService.updateDataAssetData(dataSource, dataAssetApi.getDataAssetName(), updateParam, updateWhereParam, apiConditions, lockInfo);
                }
                //GET请求下，过滤条件和以前一样，还是存库的时候会在字段名加上“filter_”前缀，在运行的时候会通过这个参数中的字段“httParamKind”过滤出过滤条件存放在requestParams中
                JSONObject updateWhereParam = requestParams.getJSONObject(HttpParamKind.FILTER.name());
                lockInfo = RedisLockHelper.buildLockInfoFromApi(Arrays.asList(param, updateWhereParam), ((RedisLockConfigAttr) dataAssetApi.getApiAttr()));
                return dataAssetService.updateDataAssetData(dataSource, dataAssetApi.getDataAssetName(), param, updateWhereParam, apiConditions, lockInfo);
            default:
                lockInfo = RedisLockHelper.buildLockInfoFromApi(param, ((RedisLockConfigAttr) dataAssetApi.getApiAttr()));
                return dataAssetService.readDataAssetData(
                        dataSource,
                        dataAssetApi.getDataAssetName(),
                        accessRuleFields,
                        param,
                        JsonUtil.getPageParam(param, RequestParamUtils.PAGE_NO, PageQuery.DEFAULT_PAGE_NO),
                        JsonUtil.getPageParam(param, RequestParamUtils.PAGE_SIZE, PageQuery.DEFAULT_PAGE_SIZE),
                        JsonUtil.getParam(param, RequestParamUtils.ORDER_BY, StringUtils.EMPTY),
                        lockInfo
                );
        }
    }

    private <T> T wrapResult(T t){
        JSONObject tmpJsonObject = new JSONObject();
        tmpJsonObject.put("result",t);
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put(HttpParamKind.BODY.name(),tmpJsonObject);
        return (T)resultJsonObject;
    }

    private <T> T wrapSelectResult(T t) {
        JSONObject tmpJsonObject = new JSONObject();
        tmpJsonObject.put("data", t);

        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put(HttpParamKind.BODY.name(), tmpJsonObject);
        return (T) resultJsonObject;
    }

    private void clearNullParameter(JSONObject param) {
        //使用局部变量获取whereCondition的值，去除value为空值的情况（选择在这里处理是因为需要兼容下面获取分页参数的逻辑，存在请求参数有pageNo，pageSize，orderBy，但是值是null的情况）
        JSONObject whereConditionBuiltIn = new JSONObject();
        whereConditionBuiltIn.putAll(param);
        Set<String> set = param.keySet();
        for (String key : set) {
            if (ObjectUtil.isEmpty(whereConditionBuiltIn.get(key)) || StringUtils.isBlank(whereConditionBuiltIn.get(key).toString())) {
                whereConditionBuiltIn.remove(key);
            }
        }
        //最外层构造最终返回结果时，需要用到whereCondition去查询totalcount（数据总条数），所以不改动whereCondition的内存地址，改变它的值
        param.clear();
        param.putAll(whereConditionBuiltIn);
    }
}
