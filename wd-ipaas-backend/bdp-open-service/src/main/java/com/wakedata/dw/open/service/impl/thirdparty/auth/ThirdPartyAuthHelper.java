package com.wakedata.dw.open.service.impl.thirdparty.auth;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.ThirdAuthConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 第三方授权信息认证
 * @author luomeng
 * @date 2022/8/9 12:45
 */
@Component
@Slf4j
public class ThirdPartyAuthHelper {

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;


    /**
     * 未添加授权应用时绑定默认的授权信息
     * @param dataAssetApiId apiId
     * @param apiType api类型
     * @param dataAssetAppId 应用id
     * @param originalParamJson api请求参数
     * @return
     */
    public JSONObject addDefaultAuthParam(Integer dataAssetApiId, DataAssetEnums.DataApiType apiType, Integer dataAssetAppId, JSONObject originalParamJson){
        log.info("添加默认授权参数信息before: id = thread-{},param = {}",Thread.currentThread().getId(),originalParamJson);
        // 查询当前api是否有绑定第三方应用,未绑定则返回原请求参数
        List<AuthInfoPo> authInfoPos = openApiDataCache.getAuthInfoPos(dataAssetApiId);
        if(ObjectUtil.isNotEmpty(authInfoPos)){
            return originalParamJson;
        }
        AppAccessPo appAccessPo = openApiDataCache.getAppAccessPo(dataAssetAppId);
        if(ObjectUtil.isEmpty(appAccessPo)){
            throw new OpenException(OpenApiMsgCodeEnum.w_data_app_id_not_found);
        }
        //获取授权惟客云信息
        AppAccessAuthConfig accessAuthConfig = openApiDataCache.getWdAuthAppInfo(appAccessPo);
        if(accessAuthConfig == null) {
//            throw new OpenException(OpenApiMsgCodeEnum.w_app_is_not_authorized);
            log.info("添加默认授权参数信息break:未绑定惟客云应用信息, id = thread-{},param = {}",Thread.currentThread().getId(),originalParamJson);
            return originalParamJson;
        }
        Boolean isApiTest = Boolean.FALSE;
        if(appAccessPo.getDataAccessKey().equals(dwOpenCommonConfig.getPlatformAdminAppKey())){
            isApiTest = Boolean.TRUE;
        }
        Map<String,Object> param = new HashMap<>();
        switch (apiType){
            case NORMAL_TABLE:
                param.put(ThirdAuthConstant.WdAuthParamEnum.SQL_TENANT_ID.getCode(),accessAuthConfig.getTenantId());
                param.put(ThirdAuthConstant.WdAuthParamEnum.SQL_APP_BU_ID.getCode(),accessAuthConfig.getAppBuId());
                break;
            case CUSTOM_SQL:
            case EXTERNAL_HTTP:
                param.put(ThirdAuthConstant.WdAuthParamEnum.COMMON_TENANT_ID.getCode(),accessAuthConfig.getTenantId());
                param.put(ThirdAuthConstant.WdAuthParamEnum.COMMON_APP_BU_ID.getCode(),accessAuthConfig.getAppBuId());
                break;
            default:
                break;
        }
        originalJsonPut(param, HttpParamKind.QUERY.name(),originalParamJson,accessAuthConfig,isApiTest);
        originalJsonPut(param, HttpParamKind.FILTER.name(),originalParamJson,accessAuthConfig,isApiTest);
        originalJsonPut(param, HttpParamKind.BODY.name(),originalParamJson,accessAuthConfig,isApiTest);
        log.info("添加默认授权参数信息after: id = thread-{},param = {}",Thread.currentThread().getId(),originalParamJson);
        return originalParamJson;
    }

    /**
     * 根据不同位置赋值JSON
     * @param locationMap
     * @param type
     * @param originalParamJson
     * @param isApiTest
     */
    private void originalJsonPut(Map<String, Object> locationMap, String type, JSONObject originalParamJson,AppAccessAuthConfig authConfig,Boolean isApiTest) {

        JSON originalLocationObject = getOriginalLocationParam(type, originalParamJson);
        if(originalLocationObject instanceof JSONArray){
            return;
        }
        JSONObject locationObject = (JSONObject)originalLocationObject;
        for(Map.Entry<String,Object> entry : locationMap.entrySet()){
            Set<String> keys = locationObject.keySet();
            Object value = locationObject.get(entry.getKey());
            //填入的和绑定的不符，需要抛异常
            if(keys.contains(entry.getKey()) && ObjectUtil.isNotEmpty(value) && !String.valueOf(entry.getValue()).equals(String.valueOf(value))){
                //如果是在进行api测试，则提示出测试对应的租户id和应用id
                if(isApiTest){
                    throw new OpenException(OpenApiMsgCodeEnum.w_app_api_asset_test_not_authorized.getCode(),String.format(OpenApiMsgCodeEnum.w_app_api_asset_test_not_authorized.getDesc(),authConfig.getTenantId(),authConfig.getAppBuId()));
                }else{
                    throw new OpenException(OpenApiMsgCodeEnum.w_app_api_asset_not_authorized);
                }

            }
            //table模式下会拼接sql，有些表没有app_bu_id或者tenant_id，不能强制写入
            if((!keys.contains(entry.getKey()) && ThirdAuthConstant.WdAuthParamEnum.SQL_APP_BU_ID.getCode().equals(entry.getKey()) ||
                    (!keys.contains(entry.getKey()) && ThirdAuthConstant.WdAuthParamEnum.SQL_TENANT_ID.getCode().equals(entry.getKey())))){
                continue;
            }
            locationObject.put(entry.getKey(),entry.getValue());
        }
        originalParamJson.put(type,locationObject);
    }

    private JSON getOriginalLocationParam(String type, JSONObject originalParamJson) {
        Object res = originalParamJson.get(type);
        if(ObjectUtil.isNotEmpty(res)){
            if(res instanceof String){
                String result = (String) res;
                if(result.startsWith(DwOpenConstant.JSON_IDENTIFIER_LEFT) && result.endsWith(DwOpenConstant.JSON_IDENTIFIER_RIGHT)) {
                    return JSONObject.parseObject(result);
                }else if (result.startsWith(DwOpenConstant.MIDDLE_BRACKET_LEFT) && result.endsWith(DwOpenConstant.MIDDLE_BRACKET_RIGHT)){
                    return JSONObject.parseArray(result);
                }
            }else if(res instanceof JSONObject){
                return (JSONObject)res;
            }else if(res instanceof JSONArray){
                return (JSONArray)res;
            }
        }
        return new JSONObject();
    }


}
