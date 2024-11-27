package com.wakedata.dw.open.service.impl.api;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.ApiRulePo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.api.DataApiAccessService;
import com.wakedata.dw.open.service.api.DataAssetApiRuleService;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import com.wakedata.dw.open.service.impl.api.attr.ApiFlowAttrService;
import com.wakedata.dw.open.service.impl.api.strategy.ApiInvokeStrategy;
import com.wakedata.dw.open.service.impl.api.strategy.ApiInvokeStrategyFactory;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wakedata.dw.open.service.utils.RequestParamUtils.DEFAULT_PARAMS;


/**
 * @author tanzhi
 * @title DataApiAccessServiceImpl
 * @projectName bdp-open
 * @date 2019/8/12 10:46
 */
@Service
@Slf4j
public class DataApiAccessServiceImpl implements DataApiAccessService {

    @Autowired
    @Getter
    private ApiResponseParamService apiResponseParamService;

    @Autowired
    private DataAssetApiRuleService dataAssetApiRuleService;

    @Autowired
    @Getter
    private DataAssetService dataAssetService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    @Getter
    private ExternalApiInvokeService externalApiInvokeService;

    @Autowired
    @Getter
    private ApiFlowAttrService apiFlowAttrService;

    @Resource
    private ApiConditionMapper apiConditionMapper;

    @Resource
    private OpenApiDataCache openApiDataCache;

    private <T> RedisCacheResult<T> getRedisCacheResult(
            Integer apiId,
            int page,
            int size,
            String orderBy,
            JSONObject params) {
        String redisKey = RedisCacheResult.assembleRedisKey(apiId, page, size, orderBy, params);
        return getRedisCacheResult(redisKey, apiId);
    }

    private <T> RedisCacheResult<T> getRedisCacheResult(
            Integer apiId,
            JSONObject params) {
        String redisKey = RedisCacheResult.assembleRedisKey(apiId, params);
        return getRedisCacheResult(redisKey, apiId);
    }

    private <T> RedisCacheResult<T> getRedisCacheResult(String redisKey, int apiId) {
        ApiRulePo apiRule = new ApiRulePo();
        apiRule.setDataAssetApiId(apiId);
        List<ApiRulePo> apiRules = dataAssetApiRuleService.find(apiRule);
        if (CollectionUtils.isNotEmpty(apiRules)) {
            apiRule = apiRules.iterator().next();
            if (apiRule.getTtl() < 1) {
                return RedisCacheResult.DUMMY;
            }

            T results = (T) redisTemplate.opsForValue().get(redisKey);
            if (results != null) {
                log.info("hit redis cache[{}]:" + redisKey);
            }
            return RedisCacheResult.of(apiRule, redisKey, results);
        } else {
            return RedisCacheResult.DUMMY;
        }
    }

    /**
     * TODO 策略模式重构拆解
     *
     * @param dataAssetApi
     * @param appId
     * @param params
     * @param page
     * @param size
     * @param orderBy
     * @param <T>
     * @return
     */
    @Override
    public <T> T readDataApiData(
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            int page,
            int size,
            String orderBy) {
        // 过滤掉请求过来的，该资源没有的字段
        RedisCacheResult<List<Map<String, Object>>> redisCacheResult =
                getRedisCacheResult(dataAssetApi.getDataAssetApiId(), page, size, orderBy, params);
        if (redisCacheResult.results != null) {
            return (T) redisCacheResult.results;
        }

        HashSet<String> accessRuleFields = getAppApiAssetColumns(dataAssetApi, appId);

        DataSourcePo dataSource = dataSourceService.show(dataAssetApi.getDataSourceId());

        if (dataAssetApi.getApiType() == DataAssetEnums.DataApiType.LITE_FLOW) {
            ApiFlowAttr apiFlowAttr = apiFlowAttrService.getApiAttr(dataAssetApi.getDataAssetApiId(), DataAssetEnums.DataApiType.LITE_FLOW);
            dataAssetApi.setApiAttr(apiFlowAttr);
            apiFlowAttrService.saveLiteFlowIfNecessary(apiFlowAttr, false);
        }
        List<ApiConditionPo> apiConditionPoList = openApiDataCache.getApiConditions(dataAssetApi.getDataAssetApiId());
        ApiInvokeStrategy<T> apiInvokeStrategy = ApiInvokeStrategyFactory.getApiInvokeStrategy(
                this,
                dataAssetApi.getApiType(),
                dataAssetApi.getOperationType(),
                dataSource,
                dataAssetApi,
                appId,
                params,
                accessRuleFields,
                apiConditionPoList
        );

        // 根据请求规则取数
        T results = apiInvokeStrategy.invoke();

        if (redisCacheResult.apiRule != null && Objects.nonNull(results)) {
            redisTemplate.opsForValue().set(
                    redisCacheResult.cacheKey,
                    results,
                    redisCacheResult.apiRule.getTtl(),
                    TimeUnit.SECONDS
            );
        }
        return results;
    }


    @Override
    public <T> T dmlDataApiData(DataAssetApiPo dataAssetApi, Integer appId, JSONObject params, List<ApiConditionPo> apiConditions) {
        // 校验appId的访问是否合法
        HashSet<String> accessRuleFields = getAppApiAssetColumns(dataAssetApi, appId);

        DataSourcePo dataSource = dataSourceService.show(dataAssetApi.getDataSourceId());
        if (dataAssetApi.getApiType() == DataAssetEnums.DataApiType.LITE_FLOW) {
            ApiFlowAttr apiFlowAttr = apiFlowAttrService.getApiAttr(dataAssetApi.getDataAssetApiId(), DataAssetEnums.DataApiType.LITE_FLOW);
            dataAssetApi.setApiAttr(apiFlowAttr);
            apiFlowAttrService.saveLiteFlowIfNecessary(apiFlowAttr, false);
        }
        ApiInvokeStrategy<T> apiInvokeStrategy = ApiInvokeStrategyFactory.getApiInvokeStrategy(
                this,
                dataAssetApi.getApiType(),
                dataAssetApi.getOperationType(),
                dataSource,
                dataAssetApi,
                appId,
                params,
                accessRuleFields, apiConditions
        );
        // 根据请求规则取数
        T results = apiInvokeStrategy.invoke();
        return results;
    }

    /**
     * 获取该api有权限的列
     *
     * @param dataAssetApi
     * @param appId
     * @return
     */
    private HashSet<String> getAppApiAssetColumns(DataAssetApiPo dataAssetApi, Integer appId) {
        //todo 未做列授权，默认返回所有列
//        AppAccessRuleDo accessRule = appAccessRuleService.getAppAccessRule(dataAssetApi.getDataAssetApiId(), appId);
//        HashSet<String> accessRuleFields = new HashSet<>(accessRule.getDataAccessRuleFieldList());
        List<ApiConditionPo> conditionPoList = apiConditionMapper.listByDataAssetId(Collections.singleton(dataAssetApi.getDataAssetApiId()), appId, false);
        if (ObjectUtil.isEmpty(conditionPoList)) {
            throw new OpenException(MsgCodeEnum.w_no_apply_column_or_already_have_permit);
        }
        return (HashSet<String>) conditionPoList.stream().map(ApiConditionPo::getAssetColumns).collect(Collectors.toSet());
    }

    @Override
    public int getDataCount(DataAssetApiPo dataAssetApi, JSONObject params) {
        RedisCacheResult<Integer> redisCacheResult = getRedisCacheResult(dataAssetApi.getDataAssetApiId(), params);
        if (redisCacheResult.results != null) {
            return redisCacheResult.results;
        } else {
            // TODO 降級到通用层，防止API无端报错
            params.remove(RequestParamUtils.__SQL__);
            params.remove(HttpParamKind.BODY);
        }
        DataSourcePo dataSource = dataSourceService.show(dataAssetApi.getDataSourceId());
        JSONObject whereCondition = DataAssetEnums.ReqMethod.POST == dataAssetApi.getReqMethod() ?
                params.getJSONObject(HttpParamKind.BODY.name()) : params.getJSONObject(HttpParamKind.QUERY.name());
        //去除默认参数
        RequestParamUtils.removeDefaultParams(whereCondition, RequestParamUtils.ORDER_BY, RequestParamUtils.ENABLE_LOG);
        int count = dataAssetService.getDataCount(dataSource, dataAssetApi, whereCondition);

        if (redisCacheResult.apiRule != null && count > 0) {
            redisTemplate.opsForValue().set(
                    redisCacheResult.cacheKey,
                    count,
                    redisCacheResult.apiRule.getTtl(),
                    TimeUnit.SECONDS
            );
        }
        return count;
    }
}