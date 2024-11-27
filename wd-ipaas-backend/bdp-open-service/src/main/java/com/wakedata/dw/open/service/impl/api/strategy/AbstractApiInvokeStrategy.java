package com.wakedata.dw.open.service.impl.api.strategy;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.gateway.OpenApiAuthParamThreadLocal;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.impl.thirdparty.auth.ThirdPartyAuthHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;

/**
 * @author ZhangXueJun
 * @title AbstractApiInvokeStrategy
 * @date 2021/5/27 17:48
 * @projectName dw-open
 * @description
 */
@Slf4j
public abstract class AbstractApiInvokeStrategy<T> implements ApiInvokeStrategy<T> {

    protected DataAssetEnums.DataApiType apiType;
    protected DataAssetApiPo dataAssetApi;
    protected Integer appId;
    protected JSONObject requestParams;
    protected HashSet<String> accessRuleFields;

    public AbstractApiInvokeStrategy(
            DataAssetEnums.DataApiType apiType,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject requestParams,
            HashSet<String> accessRuleFields) {
        this.apiType = apiType;
        this.dataAssetApi = dataAssetApi;
        this.appId = appId;
        this.requestParams = requestParams;
        this.accessRuleFields = accessRuleFields;
    }

    /**
     * 设置惟客云数据权限参数
     */
    public void setWdCloudTenantParam(){
        ThirdPartyAuthHelper authHelper = GlobalApplicationContext.getBean(ThirdPartyAuthHelper.class);
        this.requestParams = authHelper.addDefaultAuthParam(this.dataAssetApi.getDataAssetApiId(),this.dataAssetApi.getApiType(),appId,this.requestParams);
    }

    /**
     * 设置鉴权上下文参数
     */
    public void setAuthContextParams(){
        JSONObject commonParam = ObjectUtil.defaultIfNull(this.requestParams.getJSONObject(HttpParamKind.COMMON.name()),new JSONObject());
        Map<String, JSONObject> authParams = (Map<String, JSONObject>) commonParam.get(OpenApiAuthParamThreadLocal.AUTH_PARAM_KEY);
        log.info("get context before authParams:{},requestParams:{}",authParams,this.requestParams);
        if(MapUtil.isNotEmpty(authParams)){
            if(ObjectUtil.isNull(this.requestParams)){
                this.requestParams = new JSONObject();
            }
            for(Map.Entry<String,JSONObject> entry : authParams.entrySet()){
                Object json = this.requestParams.get(entry.getKey());
                if(json instanceof JSONArray){
                    continue;
                }
                JSONObject jsonObject = this.requestParams.getJSONObject(entry.getKey());
                if(jsonObject != null){
                    jsonObject.putAll(entry.getValue());
                }else{
                    jsonObject = entry.getValue();
                }
                this.requestParams.put(entry.getKey(),jsonObject);
            }
            log.info("get context after authParams:{},requestParams:{}",authParams,this.requestParams);
        }
    }


}
