package com.wakedata.dw.open.service.impl.thirdparty.auth;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.lowcode.service.LowCodeAccountService;
import com.wakedata.dw.open.constant.ThirdAuthConstant;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.AuthType;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import com.wakedata.dw.open.service.thirdparty.auth.ThirdPartyAuthService;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.threadlocal.LiteFlowStartParamThreadLocal;
import com.wakedata.dw.open.utils.threadlocal.RequstHeaderThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 第三方认证服务实现类
 * @author 佟蕊
 */
@Service
@Slf4j
public class ThirdPartyAuthServiceImpl implements ThirdPartyAuthService {

    @Autowired
    @Qualifier("lbRestTemplate")
    private RestTemplate restTemplate;

    /**
     * 组装微服务接口的URL模板（使用需要自己填充%s中的参数）
     */
    private static final String OAUTH_URL_TEMPLATE = "http://%s%s";

    /**
     * 第三方应用服务接口
     */
    @Autowired
    private AuthInfoService authInfoService;

    /**
     * 普通及低代码应用服务接口
     */
    @Autowired
    private AppAccessService appAccessService;

    @Autowired
    private  LowCodeAccountService lowCodeAccountService;


    /**
     * 第三方认证执行方法
     * @param dataAssetApi api封装信息
     * @param originalParamJson 原执行参数
     * @return 认证后的执行参数
     */
    @Override
    public JSONObject invokeAuth(DataAssetApiPo dataAssetApi, JSONObject originalParamJson) {

        Integer dataAssetApiId = dataAssetApi.getDataAssetApiId();
        String accessAppId = dataAssetApi.getAccessAppId();
        if(ObjectUtil.isEmpty(accessAppId)){
            throw new OpenException("应用不存在!");
        }
        // 查询当前api是否有绑定低代码应用
        AppAccessPo appAccess = appAccessService.getAppAccess(accessAppId);
        DataAssetEnums.AppType appType = appAccess.getAppType();
        if (appType.equals(DataAssetEnums.AppType.LOW_CODE_APP)){
            String startParamtl = LiteFlowStartParamThreadLocal.getStartParam();
            String lowCodeType = getLowCodeType(StringUtils.isBlank(startParamtl)?originalParamJson.toJSONString():startParamtl);
            return lowCodeOperate(originalParamJson, accessAppId, lowCodeType);
        }

        // 查询当前api是否有绑定第三方应用,未绑定则返回原请求参数
        List<AuthInfoPo> authInfoPos = authInfoService.queryExternalApplication(dataAssetApiId);
        if (ObjectUtil.isEmpty(authInfoPos)){
            return originalParamJson;
        }
        try {
            AuthInfoPo authInfoPo = getAuthInfoPo(originalParamJson, authInfoPos);

            // 获取前端传过来的参数信息
            String paramContentJson = authInfoPo.getConfigs();

            String authType = authInfoPo.getAppType();
            // 自定义方式
            if (AuthType.CUSTOM.getValue().equals(authType)) {
                return parseCustomMethodJson(paramContentJson, originalParamJson);
            }

            // 获取注册至nacos的元数据信息
            String authorizationApiJson = authInfoPo.getAuthorizationApi();

            // 调用invoke服务从而调用第三方认证服务
            JSONObject toInvokeJson = parseToInvokeJson(paramContentJson, authorizationApiJson, originalParamJson.toJSONString());
            ResultDTO resultDTO = operate(toInvokeJson, originalParamJson);
            if (!resultDTO.isSuccess()) {
                return originalParamJson;
            }
            Map resultData = (LinkedHashMap) resultDTO.getData();
            // 根据相应将认证信息添加到相应的位置
            JSONObject parseAuthResult = parseAuthResult(resultData, originalParamJson);
            return parseAuthResult;

        }catch (Throwable throwable){
            log.error(this.getClass()+".invokeAuth:"+ throwable.toString());
            return originalParamJson;
        }
    }

    /**
     * 获取低代码类型
     * @param jsonStr 原有参数json字符串或编排透传json字符串
     * @return 低代码类型
     */
    private String getLowCodeType(String jsonStr) {
        JSONObject jsonObject = (JSONObject)JSONObject.parse(jsonStr);
        JSONObject queryObj = (JSONObject) jsonObject.get(HttpParamKind.QUERY);
        String lowCodeType = queryObj.get(ThirdAuthConstant.LOW_CODE_TYPE).toString();
        return lowCodeType;
    }

    /**
     * 低代码预览发布操作
     * @param originalParamJson 原有需要调用外部接口的JSON
     * @param accessAppId 应用id
     * @param lowCodeType 低代码类型（预览/发布）
     * @return
     */
    private JSONObject lowCodeOperate(JSONObject originalParamJson, String accessAppId, String lowCodeType) {
        // 预览
        if (lowCodeType.equals(DataAssetEnums.LowCodeAccessType.LOW_CODE_PREVIEW)){
            Set<Integer> appIds = new HashSet<Integer>();
            appIds.add(Integer.valueOf(accessAppId));
            LowCodeAccountPo lowCodeAccountPo = lowCodeAccountService.listByAppIds(appIds).get(0);
            String username=lowCodeAccountPo.getUserName();
            String pwd=lowCodeAccountPo.getPwd();
            ResultDTO<?> resultDTO = wakeCloudLogin(username, pwd, originalParamJson);
            if (!resultDTO.isSuccess()) {
                return originalParamJson;
            }
            Map resultData = (LinkedHashMap) resultDTO.getData();
            JSONObject parseAuthResult = parseAuthResult(resultData, originalParamJson);
            return parseAuthResult;
        }

        // 发布
        Map<String,Object> headMap = RequstHeaderThreadLocal.getHeadParam();
        originalJsonPut(headMap, HttpParamKind.HEAD.name(),originalParamJson);
        return originalParamJson;
    }

    private void originalJsonPut(Map<String, Object> locationMap, String type, JSONObject originalParamJson) {
        Object res = originalParamJson.get(type);
        JSONObject originalLocationObject = null;
        if(ObjectUtil.isEmpty(res)){
            originalLocationObject = new JSONObject();
        }else{
            originalLocationObject = (JSONObject) res;
        }
        locationMap.forEach(originalLocationObject::put);
        originalParamJson.put(type,originalLocationObject);
    }

    /**
     * 惟客云登录
     * @param userName 惟客云用户名
     * @param password 惟客云密码
     * @param originalParamJson 原有需要调用外部接口的JSON
     * @return
     */
    private ResultDTO<?> wakeCloudLogin(String userName, String password, JSONObject originalParamJson) {
        String applicationName = ThirdAuthConstant.WK_LOGIN_APPLICATION_NAME;
        String path = ThirdAuthConstant.WK_LOGIN_CONTENT_PATH+ThirdAuthConstant.WK_LOGIN_METHOD_URL;
        JSONObject params = new JSONObject();
        params.put(ThirdAuthConstant.WK_LOGIN_USERNAME,userName);
        params.put(ThirdAuthConstant.WK_LOGIN_PWD,password);
        String url = getUrlTemplate(applicationName, path, params);
        HttpHeaders headers = new HttpHeaders();
        headers.add(ThirdAuthConstant.PARAM_OBJECT, originalParamJson.toJSONString());
        HttpEntity<JSONObject> entity = new HttpEntity<>(headers);
        ResponseEntity<ResultDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, ResultDTO.class);
        return responseEntity.getBody();
    }

    /**
     * 请求第三方认证并获取认证需要的参数
     * @param param             第三方认证接口所需要的参数
     * @param originalParamJson 原请求参数信息JSONObject
     * @return 第三方认证需要设置的返回值
     */
    private ResultDTO<?> operate(JSONObject param, JSONObject originalParamJson) {
        String applicationName = param.getString(ThirdAuthConstant.INVOKE_APPLICATION_NAME);
        String requestType = param.getString(ThirdAuthConstant.INVOKE_REQUESTTYPE);
        String requestUrl = param.getString(ThirdAuthConstant.INVOKE_REQUESTURL);
        String executeParamJson = param.getString(ThirdAuthConstant.INVOKE_EXCUTEPARAMJSON);
        String contextPath = param.getString(ThirdAuthConstant.INVOKE_CONTEXTPATH);

        JSONObject executeParam = JSON.parseObject(executeParamJson);
        JSONObject queryParam = (JSONObject) executeParam.get(ThirdAuthConstant.AUTHORIZATIONAPI_QUERY);
        JSONObject jsonParam = (JSONObject) executeParam.get(ThirdAuthConstant.AUTHORIZATIONAPI_JSON);
        String url = getUrlTemplate(applicationName, contextPath + requestUrl, queryParam);
        HttpHeaders headers = new HttpHeaders();
        headers.add(ThirdAuthConstant.PARAM_OBJECT, originalParamJson.toJSONString());
        if (!jsonParam.isEmpty()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonParam, headers);
        ResponseEntity<ResultDTO> responseEntity = restTemplate.exchange(url, requestType.equals(HttpMethod.POST.name()) ? HttpMethod.POST : HttpMethod.GET, entity, ResultDTO.class);
        return responseEntity.getBody();
    }

    /**
     * 拼接授权URL
     * @param applicationName 微服务名
     * @param path            请求路径
     * @param params          query参数
     * @return 请求URL
     */
    private String getUrlTemplate(String applicationName, String path, JSONObject params) {
        StringBuilder stringBuilder = new StringBuilder(String.format(OAUTH_URL_TEMPLATE, applicationName, path));
        //拼装url中的参数
        int index = 0;
        for (Map.Entry<String, Object> mapEntry : params.entrySet()) {
            if (index == 0) {
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(mapEntry.getKey()).append("=").append(mapEntry.getValue());
            index++;
        }
        return stringBuilder.toString();
    }

    /**
     * 获取要执行的第三方应用信息
     * @param originalParamJson 原有需要调用外部接口的JSON
     * @param authInfoPos 当前api绑定的所有第三方应用
     * @return 第三方应用信息
     */
    private AuthInfoPo getAuthInfoPo(JSONObject originalParamJson, List<AuthInfoPo> authInfoPos) {
        // 只绑定了一个第三方应用，则默认使用该第三方应用信息
        if (authInfoPos.size() == 1){
            AuthInfoPo authInfoPo = authInfoPos.get(0);
            return authInfoPo;
        }

        // 编排中的start参数
        String startParamtl = LiteFlowStartParamThreadLocal.getStartParam();
        // 单个http外部接口
        if (StringUtils.isBlank(startParamtl)){
            JSONObject queryObj = (JSONObject) originalParamJson.get(HttpParamKind.QUERY);
            Integer authId = getAuthIdInSingleHttp(queryObj);
            AuthInfoPo authInfoPo = authInfoService.detail(authId);
            return authInfoPo;
        }

        // 流程编排
        JSONObject startParamObj = JSONObject.parseObject(startParamtl);
        Integer authId = getAuthIdInComponent(startParamObj);
        AuthInfoPo authInfoPo = authInfoService.detail(authId);
        return authInfoPo;
    }

    /**
     * 获取单个http接口的第三方认证标识
     * @param queryObj query中的參數
     * @return 第三方认证标识
     */
    private Integer getAuthIdInSingleHttp(JSONObject queryObj) {
        Integer authId = 0;
        String authidStr = queryObj.get(ThirdAuthConstant.BIND_APP_AUTH_ID).toString();
        if (StringUtils.isNotEmpty(authidStr)){
            authId = Integer.valueOf(authidStr);
        }
        return authId;
    }

    /**
     * 获取编排中的第三方认证标识
     * @param queryObj query中的參數
     * @return 第三方认证标识
     */
    private Integer getAuthIdInComponent(JSONObject queryObj) {
        AtomicReference<Integer> authId = new AtomicReference<>(0);
        String authidStr = queryObj.get(ThirdAuthConstant.BIND_APP_AUTH_ID).toString();

        Map authMap = new Gson().fromJson(authidStr, LinkedHashMap.class);
        List authList = (List)authMap.get(ThirdAuthConstant.BIND_APP_AUTH_LIST);
        authList.forEach(apiRefAuth->{
            Map<String, Object> apiRefAuthMap = (Map<String, Object>) apiRefAuth;
            String apiIdStr = apiRefAuthMap.get(ThirdAuthConstant.BIND_APP_AUTH_LIST_APIID).toString();
            if (StringUtils.isNotEmpty(apiIdStr)){
                authId.set(Integer.valueOf(apiIdStr));
                return;
            }
        });

        return authId.get();
    }

    /**
     * 解析认证结果
     * @param resultData 调用第三方服务返回的认证结果
     * @param originalParamJson 原有需要调用外部接口的JSON
     * @return 解析后的结果（添加HEAD/QUERY/BODY）
     */
    public JSONObject parseAuthResult(Map<String,Object> resultData, JSONObject originalParamJson){
        resultData.forEach((location,map)->{
            originalParamJson.forEach((key,value)->{
                if (key.equals(location)){
                    if (StringUtils.isNotEmpty(originalParamJson.get(key).toString())){
                        JSONObject jsonObject = (JSONObject)originalParamJson.get(key);
                        // 移除无用参数
                        jsonObject.remove(RequestParamUtils.ENABLE_LOG);
                        Map<String, Object> innerMap = (Map<String, Object>) map;
                        innerMap.forEach((k,v)->{
                            jsonObject.put(k,v);
                        });
                    }
                }
            });
        });
        return originalParamJson;
    }

    /**
     * 自定义方式JSON解析
     * @param paramContentJson 前端传过来的参数信息
     * @param originalParamJson 原有需要调用外部接口的JSON
     * @return 解析后的结果（添加HEAD/QUERY/BODY）
     */
    public JSONObject parseCustomMethodJson(String paramContentJson, JSONObject originalParamJson){
        List<Map<String,String>> listmap = new Gson().fromJson(paramContentJson, ArrayList.class);

        listmap.forEach((map)->{
            String location = map.get(ThirdAuthConstant.CONFIGS_LOCATION);
            originalParamJson.forEach((key,value)->{
                if (key.equals(location)){
                    JSONObject jsonObject = (JSONObject)originalParamJson.get(key);
                    jsonObject.put(map.get(ThirdAuthConstant.CONFIGS_KEY),map.get(ThirdAuthConstant.CONFIGS_VALUE));
                }
            });
        });

        return originalParamJson;
    }

    /**
     * 解析为将要执行第三方认证服务的JSON
     * @param paramContentJson 前端传过来的参数信息
     * @param authorizationApiJson 从nacos获取的应用方法等JSON
     * @param originalParamJson 原有需要调用外部接口的JSON
     * @return 解析后的结果（应用信息/url信息/参数信息）
     */
    public JSONObject parseToInvokeJson(String paramContentJson, String authorizationApiJson,String originalParamJson){

        Map authorizationApiJsonMap = new Gson().fromJson(authorizationApiJson, LinkedHashMap.class);

        JSONObject httpToAuthInvokeMethodJson = new JSONObject();
        httpToAuthInvokeMethodJson.put(ThirdAuthConstant.INVOKE_APPLICATION_NAME,authorizationApiJsonMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_APPLICATION_NAME));
        httpToAuthInvokeMethodJson.put(ThirdAuthConstant.INVOKE_CONTEXTPATH,authorizationApiJsonMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_CONTEXTPATH));

        List authorizationApiList = (List)authorizationApiJsonMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_AUTHORIZATION_API);
        Map<String, Object> authorizationApiMap = (Map<String, Object>) authorizationApiList.get(0);

        httpToAuthInvokeMethodJson.put(ThirdAuthConstant.INVOKE_REQUESTURL,authorizationApiMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_REQUESTURL));
        httpToAuthInvokeMethodJson.put(ThirdAuthConstant.INVOKE_REQUESTTYPE,authorizationApiMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_REQUESTTYPE));

        List jsonArray = (List)authorizationApiMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_JSON);
        List queryArray = (List)authorizationApiMap.get(ThirdAuthConstant.AUTHORIZATIONAPI_QUERY);

        List<Map<String,String>> listmap = new Gson().fromJson(paramContentJson, ArrayList.class);

        JSONObject paramQuery = new JSONObject();
        JSONObject paramJson = new JSONObject();
        listmap.forEach((map)->{
            if (queryArray.contains(map.get(ThirdAuthConstant.CONFIGS_KEY))){
                paramQuery.put(map.get(ThirdAuthConstant.CONFIGS_KEY),map.get(ThirdAuthConstant.CONFIGS_VALUE));
            }
            if (jsonArray.contains(map.get(ThirdAuthConstant.CONFIGS_KEY))){
                paramJson.put(map.get(ThirdAuthConstant.CONFIGS_KEY),map.get(ThirdAuthConstant.CONFIGS_VALUE));
            }
        });

        JSONObject excuteParamJson = new JSONObject();
        httpToAuthInvokeMethodJson.put(ThirdAuthConstant.INVOKE_ORIGINALPARAMJSON, originalParamJson);

        excuteParamJson.put(ThirdAuthConstant.AUTHORIZATIONAPI_QUERY,paramQuery);
        excuteParamJson.put(ThirdAuthConstant.AUTHORIZATIONAPI_JSON,paramJson);
        httpToAuthInvokeMethodJson.put(ThirdAuthConstant.INVOKE_EXCUTEPARAMJSON,excuteParamJson.toJSONString());
        return httpToAuthInvokeMethodJson;
    }
}
