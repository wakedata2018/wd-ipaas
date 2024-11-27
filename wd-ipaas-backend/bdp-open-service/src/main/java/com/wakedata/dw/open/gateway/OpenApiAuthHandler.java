package com.wakedata.dw.open.gateway;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wakedata.dw.open.accesstoken.*;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.api.AppAccessService;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.wakedata.dw.open.constant.DwOpenConstant.OPEN_API_PREFIX;
import static com.wakedata.dw.open.constant.DwOpenConstant.OPEN_API_TEST_PREFIX;

/**
 * 开放平台api身份认证
 * @author luomeng
 * @date 2022/11/21 17:51
 */
@Component
@Slf4j
public class OpenApiAuthHandler {

    @Resource
    private OpenApiDataCache openApiDataCache;
    @Resource
    private AppApprovalService appApprovalService;
    @Autowired
    private List<OpenApiAuthService> openApiAuthServiceList;
    @Value("${spring.mvc.backend.api.prefix}")
    private String backendApiPrefix;
    @Value("${server.servlet.context-path}")
    private String serverPath;
    @Resource
    private AppAccessService appAccessService;


    /**
     * 获取平台默认接口访问前缀
     * @return
     */
    public String getPlatformDefaultApiPrefix(){
        return serverPath + backendApiPrefix + "/" + OPEN_API_PREFIX + "/";
    }

    /**
     * 获取平台测试接口访问前缀
     * @return
     */
    public String getPlatformTestApiPrefix(){
        return serverPath + backendApiPrefix + "/" + OPEN_API_TEST_PREFIX + "/";
    }

    /**
     * 获取应用访问前缀
     * @param appPrefix
     * @return
     */
    public String getAppApiPrefix(String appPrefix){
        return serverPath + backendApiPrefix + "/" + appPrefix + "/";
    }

    /**
     * 身份认证
     * @param openApiParams
     * @return
     */
    public AppAccessInfo authenticate(OpenApiParams openApiParams){
        //api默认使用accessToken模式
        if(OPEN_API_PREFIX.equals(openApiParams.getApiPrefix())){
            return getOpenApiAuthService(DataAssetEnums.DataAccessAppAuthType.TOKEN_AUTH)
                    .authenticate(openApiParams,null);
        }
        AppAccessPo appAccessPo;
        //后台测试使用
        if(OPEN_API_TEST_PREFIX.equals(openApiParams.getApiPrefix())){
            appAccessPo = appAccessService.queryAppInfoByKey(openApiParams.getAppKey());
        }else{
            //解析api前缀
            appAccessPo = openApiDataCache.getAppAccessInfoByPrefix(openApiParams.getApiPrefix());
        }
        if(ObjectUtil.isNull(appAccessPo)){
            throw new OpenException(OpenApiMsgCodeEnum.w_app_api_asset_fail);
        }
        return getOpenApiAuthService(ObjectUtil.defaultIfNull(appAccessPo.getAuthType(),DataAssetEnums.DataAccessAppAuthType.TOKEN_AUTH))
                .authenticate(openApiParams,appAccessPo);
    }

    /**
     * 校验api权限
     * @param isApiTest 是否api测试
     * @param groupApi 请求url
     * @param appAccessInfo 应用信息
     * @return
     */
    public DataAssetApiPo checkApiAuthAndThrowExc(Boolean isApiTest,String groupApi, AppAccessInfo appAccessInfo) {
        //校验是否存在该api
        DataAssetApiPo dataAssetApi = openApiDataCache.getDataAssetApiPo(groupApi);
        if (ObjectUtil.isNull(dataAssetApi)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_wrong_api_path);
        }
        if(appAccessInfo.getIsApiTest() || Boolean.TRUE.equals(isApiTest)){
            return dataAssetApi;
        }
        // 判断该api是否上线
        if (DataAssetPublishStatusEnum.PUBLISH != dataAssetApi.getDataAssetPublishStatus()) {
            throw new OpenException(OpenApiMsgCodeEnum.w_data_api_not_publish);
        }
        //校验api是否已授权给该应用
        Integer count = appApprovalService.selectAuthAppCountByAppId(appAccessInfo.getLesseeId(),appAccessInfo.getDataAccessAppId()
                ,dataAssetApi.getDataAssetApiId());
        if (ObjectUtil.defaultIfNull(count,0) <= 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_api_jwt_not_binding_error);
        }
        return dataAssetApi;
    }


    /**
     * 选择执行鉴权服务
     * @param authType
     * @return
     */
    public OpenApiAuthService getOpenApiAuthService(DataAssetEnums.DataAccessAppAuthType authType) {
        Optional<OpenApiAuthService> groupSelectOptional = openApiAuthServiceList.stream().filter(t -> t.getSupportAuthType() == authType).findAny();
        return groupSelectOptional.orElseThrow(() -> new IllegalArgumentException("authType error，api request fail"));
    }

    /**
     * 是否是api测试
     * @param openApiParams
     * @return
     */
    public boolean isApiTest(OpenApiParams openApiParams) {
        if(StrUtil.isBlank(openApiParams.getAccessToken())){
            return false;
        }
        return openApiParams.getAccessToken().contains(DwOpenConstant.TEST_ACCESS_TOKEN_PREFIX);
    }


}
