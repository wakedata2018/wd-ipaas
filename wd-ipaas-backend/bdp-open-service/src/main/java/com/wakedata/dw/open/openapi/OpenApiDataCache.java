package com.wakedata.dw.open.openapi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.mapper.api.ApiGroupMapper;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.mapper.api.attr.HttpExternalApiMapper;
import com.wakedata.dw.open.mapper.api.attr.RedisLockConfigAttrMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorApiRequestParamMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorEnvironmentAddressMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorSecretKeyMapper;
import com.wakedata.dw.open.model.api.*;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.model.connector.ConnectorApiRequestParamPo;
import com.wakedata.dw.open.model.connector.ConnectorEnvironmentAddressPo;
import com.wakedata.dw.open.model.connector.ConnectorSecretKeyPo;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import com.wakedata.dw.open.service.impl.api.attr.ApiFlowAttrService;
import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;
import com.wakedata.dw.openapi.service.DwOpenAccessAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据缓存
 *
 * @author luomeng
 * @date 2022/10/8 10:36
 */
@Component
@Slf4j
public class OpenApiDataCache {

    /**
     * 缓存时间
     */
    public static final Long API_CACHE_EXPIRE_TIME = 300L;

    /**
     * cache 前缀
     */
    public static final String CACHE_NAMES = "openapi:cache:data";

    @Resource
    private RedisLockConfigAttrMapper redisLockConfigAttrMapper;

    @Resource
    private DataAssetApiMapper dataAssetApiMapper;

    @Resource
    private ApiConditionMapper apiConditionMapper;

    @Resource
    private ApiResponseParamService apiResponseParamService;

    @Resource
    private ApiFlowAttrService apiFlowAttrService;

    @Resource
    private HttpExternalApiMapper httpExternalApiMapper;

    @Resource
    private ConnectorSecretKeyMapper connectorSecretKeyMapper;

    @Resource
    private ConnectorEnvironmentAddressMapper connectorEnvironmentAddressMapper;

    @Resource
    private DwOpenAccessAuthService dwOpenAccessAuthService;

    @Resource
    private AuthInfoService authInfoService;

    @Resource
    private AppAccessService appAccessService;

    @Resource
    private ConnectorApiRequestParamMapper connectorApiRequestParamMapper;

    @Resource
    private ApiGroupMapper apiGroupMapper;

    /**
     * 获取连接器api参数
     * @param connectorApiId
     * @return
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getConnectorApiRequestParamList_'+#connectorApiId",unless = "#result == null")
    public List<ConnectorApiRequestParamPo> getConnectorApiRequestParamList(Long connectorApiId){
        Example requestParamExample = new Example(ConnectorApiRequestParamPo.class);
        requestParamExample.createCriteria().andEqualTo("connectorApiId", connectorApiId);
        return connectorApiRequestParamMapper.selectByExample(requestParamExample);
    }

    /**
     * 缓存api响应结果信息
     * @param apiId
     * @return
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiResponseParams_'+#apiId",unless = "#result == null")
    public List<ApiRespParamDTO> getApiResponseParams(Integer apiId) {
        return apiResponseParamService.listByApiId(apiId);
    }

    /**
     * 缓存编排返回结果信息
     * @param apiId
     * @return
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiLiteFlowParams_'+#apiId",unless = "#result == null")
    public List<ApiRespParamDTO> getApiLiteFlowParams(Integer apiId) {
        return apiResponseParamService.findLiteflowResult(apiId);
    }


    /**
     * 缓存api调用接口信息
     * @param dataAssetApiId
     * @return
     */
//    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getHttpExternalApiAttr_'+#dataAssetApiId",unless = "#result == null")
    public HttpExternalApiAttr getHttpExternalApiAttr(Integer dataAssetApiId) {
        return httpExternalApiMapper.getExternalApi(dataAssetApiId);
    }

    /**
     * 根据密钥ID查询密钥信息
     *
     * @param secretKey 密钥ID
     * @return 密钥信息
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES, key = "'getConnectorSecretKeyPo_'+#secretKey", unless = "#result == null")
    public ConnectorSecretKeyPo getConnectorSecretKeyPo(String secretKey) {
        return connectorSecretKeyMapper.selectBySecretKey(secretKey);
    }

    /**
     * 使用主键查询环境信息
     *
     * @param id 主键
     * @return 环境信息
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES, key = "'getConnectorEnvironmentAddressPo_'+#id", unless = "#result == null")
    public ConnectorEnvironmentAddressPo getConnectorEnvironmentAddressPo(Long id) {
        return connectorEnvironmentAddressMapper.selectByPrimaryKey(id);
    }

    /**
     * 缓存编排信息
     * @param dataAssetApiId
     * @return
     */
//    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiFlowAttr_'+#dataAssetApiId",unless = "#result == null")
    public ApiFlowAttr getApiFlowAttr(Integer dataAssetApiId) {
        return apiFlowAttrService.getApiAttr(dataAssetApiId, DataAssetEnums.DataApiType.LITE_FLOW);
    }

    /**
     * 缓存api信息
     * @param method
     * @return
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'gateway_getDataAssetApiPo_'+#method",unless = "#result == null")
    public DataAssetApiPo getDataAssetApiPo(String method) {
        DataAssetApiPo dataAssetApi = new DataAssetApiPo();
        dataAssetApi.setDataAssetApiMethod(method);
        List<DataAssetApiPo> assetApis = dataAssetApiMapper.select(dataAssetApi);
        if (CollectionUtils.isNotEmpty(assetApis)) {
            DataAssetApiPo dataAssetApiPo = assetApis.get(0);
            //显示分组
            ApiGroupPo apiGroupPo = apiGroupMapper.selectByPrimaryKey(dataAssetApiPo.getApiGroupId());
            if(apiGroupPo != null){
                dataAssetApiPo.setApiGroupName(apiGroupPo.getGroupName());
            }
            // 如果API是数据表或者SQL，加载对应的Redis锁配置
            DataAssetEnums.DataApiType apiType = dataAssetApiPo.getApiType();
            if (DataAssetEnums.DataApiType.CUSTOM_SQL == apiType || DataAssetEnums.DataApiType.NORMAL_TABLE == apiType) {
                Example redisConfigExample = new Example(RedisLockConfigAttr.class);
                redisConfigExample.createCriteria().andEqualTo("apiId", dataAssetApiPo.getDataAssetApiId());
                List<RedisLockConfigAttr> redisLockConfigs = redisLockConfigAttrMapper.selectByExample(redisConfigExample);
                dataAssetApiPo.setApiAttr(CollectionUtil.isEmpty(redisLockConfigs) ? null : redisLockConfigs.get(0));
            }
            return dataAssetApiPo;
        }
        return null;
    }


    /**
     * 缓存api第三方授权信息
     * @param dataAssetApiId
     * @return
     */
    @Cacheable(value =  OpenApiDataCache.CACHE_NAMES,key = "'getAuthInfoPos_' + #dataAssetApiId",unless = "#result == null")
    public List<AuthInfoPo> getAuthInfoPos(Integer dataAssetApiId) {
        return authInfoService.queryExternalApplication(dataAssetApiId);
    }

    /**
     * 缓存app信息
     * @param dataAssetAppId
     * @return
     */
    @Cacheable(value =  OpenApiDataCache.CACHE_NAMES,key = "'getAppAccessPo_' + #dataAssetAppId",unless = "#result == null")
    public AppAccessPo getAppAccessPo(Integer dataAssetAppId) {
        return appAccessService.queryAppInfoById(dataAssetAppId);
    }

    /**
     * 缓存app信息
     * @param apiPrefix
     * @return
     */
    @Cacheable(value =  OpenApiDataCache.CACHE_NAMES,key = "'getAppAccessInfoByPrefix_' + #apiPrefix",unless = "#result == null")
    public AppAccessPo getAppAccessInfoByPrefix(String apiPrefix) {
        return appAccessService.getAppAccessInfoByPrefix(apiPrefix);
    }


    /**
     * 移除app缓存信息
     * @param apiPrefix
     * @return
     */
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getAppAccessInfoByPrefix_'+#apiPrefix")
    public boolean removeAppAccessInfoByPrefix(String apiPrefix){

        return true;
    }

    /**
     * 获取app信息
     * @param appKey
     * @return
     */
    @Cacheable(value =  OpenApiDataCache.CACHE_NAMES,key = "'getAppAccessInfo_' + #appKey",unless = "#result == null")
    public AppAccessInfo getAppAccessInfo(String appKey) {
        AppAccessPo appAccessPo = appAccessService.queryAppInfoByKey(appKey);
        return BeanUtil.copy(appAccessPo, AppAccessInfo.class);
    }

    /**
     * 缓存app绑定惟客云信息
     * @param appAccessPo
     * @return
     */
    @Cacheable(value =  OpenApiDataCache.CACHE_NAMES,key = "'getWdAuthAppInfo_' + #appAccessPo.lesseeId+'_'+#appAccessPo.dataAccessAppId",unless = "#result == null")
    public AppAccessAuthConfig getWdAuthAppInfo(AppAccessPo appAccessPo) {
        //通过应用授权记录绑定
        AppAccessAuthConfig accessAuthConfig = dwOpenAccessAuthService.getAppAuthConfigByAppId(appAccessPo.getLesseeId(),appAccessPo.getDataAccessAppId(),DataAssetEnums.AppAuthType.WAKE_CLOUD.getValue());
        if(ObjectUtil.isNull(accessAuthConfig)){
            return null;
        }
        accessAuthConfig.setAppBuId(accessAuthConfig.getId());
        return accessAuthConfig;
    }


    /**
     * 获取该api有权限的列
     *
     * @param dataAssetApi
     * @param appId
     * @return
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getAppApiAssetColumns_' + #dataAssetApi.dataAssetApiId +'_' + #appId",unless = "#result == null")
    public HashSet<String> getAppApiAssetColumns(DataAssetApiPo dataAssetApi, Integer appId) {
        //todo 未做列授权，默认返回所有列
        List<ApiConditionPo> conditionPoList = apiConditionMapper.listByDataAssetId(Collections.singleton(dataAssetApi.getDataAssetApiId()), appId, false);
        if (ObjectUtil.isEmpty(conditionPoList)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_no_apply_column_or_already_have_permit);
        }
        return (HashSet<String>) conditionPoList.stream().map(ApiConditionPo::getAssetColumns).collect(Collectors.toSet());
    }

    /**
     * 获取api配置的参数
     *
     * @param apiId
     * @return
     */
    @Cacheable(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiConditions_' + #apiId",unless = "#result == null")
    public List<ApiConditionPo> getApiConditions(Integer apiId) {
        ApiConditionPo apiCondition = new ApiConditionPo();
        apiCondition.setDataAssetId(apiId);
        return apiConditionMapper.select(apiCondition);
    }

    /**
     * 移除api缓存
     *
     * @param method
     */
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'gateway_getDataAssetApiPo_'+#method")
    public boolean removeApiCache(String method) {
        return true;
    }

    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiConditions_' + #dataAssetApiId")
    public boolean removeApiConditions(Integer dataAssetApiId){
        return true;
    }
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getHttpExternalApiAttr_'+#dataAssetApiId")
    public boolean removeHttpExternalApiAttr(Integer dataAssetApiId){
        return true;
    }


    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiResponseParams_'+#dataAssetApiId")
    public boolean removeApiResponseParams(Integer dataAssetApiId){
        return true;
    }

    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiLiteFlowParams_'+#dataAssetApiId")
    public boolean removeApiLiteFlowParams(Integer dataAssetApiId){
        return true;
    }

    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES,key = "'getApiFlowAttr_'+#dataAssetApiId")
    public boolean removeApiFlowAttr(Integer dataAssetApiId){
        return true;
    }

    /**
     * 清除密钥信息缓存
     *
     * @param secretKey 密钥ID
     * @return Boolean
     */
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES, key = "'getConnectorSecretKeyPo_'+#secretKey")
    public boolean removeConnectorSecretKeyPo(String secretKey) {
        return true;
    }

    /**
     * 清除环境信息缓存
     *
     * @param id 缓存信息
     * @return Boolean
     */
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES, key = "'getConnectorEnvironmentAddressPo_'+#id")
    public boolean removeConnectorEnvironmentAddressPo(Long id) {
        return true;
    }

    /**
     * 移除api有权限的列缓存
     *
     * @param dataAssetApiId api id
     * @param appId          应用id
     * @return Boolean
     */
    @CacheEvict(value = OpenApiDataCache.CACHE_NAMES, key = "'getAppApiAssetColumns_' + #dataAssetApiId +'_' + #appId")
    public boolean removeAppApiAssetColumns(Integer dataAssetApiId, Integer appId) {
        return true;
    }

}
