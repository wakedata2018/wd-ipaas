package com.wakedata.dw.open.gateway;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.authentication.ConnectorApiProcessorFactory;
import com.wakedata.dw.open.authentication.dto.ConnectorProcessorParamDTO;
import com.wakedata.dw.open.condition.ConditionUtils;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.connector.ConnectorApiRequestParamPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.connector.ConnectorApiService;
import com.wakedata.dw.open.service.connector.ConnectorAuthConfigService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDetailDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiRequestParamDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorAuthConfigDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorAuthParamDTO;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.HttpParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 连接器鉴权
 *
 * @author luomeng
 * @date 2022/11/22 10:45
 */
@Service
@Slf4j
public class OpenApiConnectorAuthServiceImpl implements OpenApiAuthService {
    @Resource
    private ConnectorAuthConfigService connectorAuthConfigService;
    @Resource
    private ConnectorApiProcessorFactory connectorApiProcessorFactory;
    @Resource
    private ConnectorApiService connectorApiService;
    @Resource
    private OpenApiDataCache openApiDataCache;

    @Override
    public AppAccessInfo authenticate(OpenApiParams openApiParams, AppAccessPo appAccessPo) {
        //获取连接器鉴权配置
        ConnectorAuthConfigDTO authConfigDTO = connectorAuthConfigService.getConnectorAuthConfigById(appAccessPo.getConnectorAuthId());
        if (ObjectUtil.isNull(authConfigDTO)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_connector_auth_config_not_found);
        }
        ConnectorAuthParamDTO authConfigParam = authConfigDTO.getAuthConfigParam();
        //解析需要的鉴权参数
        Map<String, JSONObject> authParams = parseAuthParams(openApiParams, authConfigDTO.getConnectorApiId());
        //调用api鉴权
        JSONObject resultJsonObject = executeConnectorApi(authConfigDTO, authParams);
        //根据规则判断是否有效
        try {
            boolean execResult = ConditionUtils.exec(authConfigParam.getCondition(), resultJsonObject);
            if (!execResult) {
                throw new OpenException(OpenApiMsgCodeEnum.s_connector_auth_param_invalid);
            }
        }catch (OpenException e){
            throw new OpenException(OpenApiMsgCodeEnum.s_connector_auth_param_rule_invalid);
        }
        //鉴权参数透传到后续api中
        OpenApiAuthParamThreadLocal.setAuthParams(authParams);
        return BeanUtil.copy(appAccessPo, AppAccessInfo.class);
    }


    /**
     * 执行连接器api
     *
     * @param authConfigDTO
     * @param authParams
     * @return
     */
    private JSONObject executeConnectorApi(ConnectorAuthConfigDTO authConfigDTO, Map<String, JSONObject> authParams) {
        try {
            ConnectorProcessorParamDTO paramDto = buildConnectorProcessorParamDTO(authParams, authConfigDTO);
            ResponseEntity<?> responseEntity = connectorApiProcessorFactory.getConnectorApiProcessor(paramDto.getApiAuthTypeEnum()).execute(paramDto);
            if (responseEntity == null) {
                throw new OpenException(OpenApiMsgCodeEnum.s_connector_auth_param_invalid);
            }
            JSONObject resultJsonObject = new JSONObject();
            resultJsonObject.put(HttpParamKind.BODY.name(), JSONObject.parseObject((String) responseEntity.getBody()));
            resultJsonObject.put(HttpParamKind.HEAD.name(), responseEntity.getHeaders());
            return resultJsonObject;
        } catch (Exception e) {
            if (!(e instanceof OpenException)) {
                log.error("execute connector api fail-> param:{},config:{}", authParams, authConfigDTO, e);
            }
            throw new OpenException(OpenApiMsgCodeEnum.s_connector_auth_param_invalid);
        }
    }


    /**
     * 构造连接器执行参数
     *
     * @param authParams
     * @param authConfigDTO
     * @return
     */
    private ConnectorProcessorParamDTO buildConnectorProcessorParamDTO(Map<String, JSONObject> authParams, ConnectorAuthConfigDTO authConfigDTO) {
        ConnectorProcessorParamDTO dto = new ConnectorProcessorParamDTO();
        dto.setConnectorApi(connectorApiService.show(authConfigDTO.getConnectorApiId().intValue()));
        JSONObject params = new JSONObject();
        params.putAll(authParams);
        dto.setParams(params);
        dto.setApiAuthTypeEnum(ConnectorApiAuthTypeEnum.NO_AUTHENTICATION);
        dto.setEnvironmentAddress(openApiDataCache.getConnectorEnvironmentAddressPo(authConfigDTO.getConnectorEnvironmentId()));
        return dto;
    }

    /**
     * 解析鉴权参数
     *
     * @param openApiParams
     * @param connectorApiId
     * @return
     */
    private Map<String, JSONObject> parseAuthParams(OpenApiParams openApiParams, Long connectorApiId) {
        List<ConnectorApiRequestParamPo> requestParamPoList = openApiDataCache.getConnectorApiRequestParamList(connectorApiId);
        Map<String, JSONObject> authParams = new HashMap<>(5);
        Map<HttpParamKind, List<ConnectorApiRequestParamPo>> apiRequestParamMap = requestParamPoList.stream().collect(Collectors.groupingBy(ConnectorApiRequestParamPo::getHttpParamKind));
        for (Map.Entry<HttpParamKind, List<ConnectorApiRequestParamPo>> entry : apiRequestParamMap.entrySet()) {
            HttpParamKind position = entry.getKey();
            List<ConnectorApiRequestParamPo> value = entry.getValue();
            switch (position) {
                case HEAD:
                    authParams.putAll(parseHeadParams(openApiParams, value));
                    break;
                case QUERY:
                    authParams.putAll(parseQueryParams(openApiParams, value));
                    break;
                case BODY:
                    Map<String, JSONObject> bodyParams = parseBodyParams(openApiParams, value);
                    if(CollUtil.isNotEmpty(bodyParams)){
                        authParams.putAll(bodyParams);
                    }
                    break;
                default:
                    break;
            }
        }
        return authParams;
    }

    /**
     * 解析head参数
     *
     * @param openApiParams
     * @param authConfigParam
     * @return
     */
    private static Map<String, JSONObject> parseHeadParams(OpenApiParams openApiParams, List<ConnectorApiRequestParamPo> authConfigParam) {
        Map<String,JSONObject> authParams = new HashMap<>(2);
        Set<ApiConditionPo> apiConditions = authConfigParam.stream().map(x -> {
            ApiConditionPo conditionPo = new ApiConditionPo();
            conditionPo.setAssetColumns(x.getAssetColumns());
            return conditionPo;
        }).collect(Collectors.toSet());
        HttpHeaders headers = RequestParamUtils.getHeaders(openApiParams.getRequest(), apiConditions);
        JSONObject params = new JSONObject();
        params.putAll(headers);
        authParams.put(HttpParamKind.HEAD.name(), params);
        return authParams;
    }

    /**
     * 解析query参数
     *
     * @param openApiParams
     * @param authConfigParam
     * @return
     */
    private static Map<String, JSONObject> parseQueryParams(OpenApiParams openApiParams, List<ConnectorApiRequestParamPo> authConfigParam) {
        Map<String,JSONObject> authParams = new HashMap<>(2);
        Map<String, String> requestParams = HttpParamUtil.getRequestParams(openApiParams.getRequest());
        List<String> paramKey = authConfigParam.stream().map(ConnectorApiRequestParamPo::getAssetColumns).collect(Collectors.toList());
        JSONObject params = new JSONObject();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            if (paramKey.contains(entry.getKey())) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        authParams.put(HttpParamKind.QUERY.name(), params);
        return authParams;
    }

    /**
     * 解析body参数
     * @param openApiParams
     * @param authConfigParam
     * @return
     */
    private static Map<String, JSONObject> parseBodyParams(OpenApiParams openApiParams, List<ConnectorApiRequestParamPo> authConfigParam) {
        JSONObject params = RequestParamUtils.getBodyParams(openApiParams.getRequest(),openApiParams.getPostData()
                , authConfigParam.get(CommonConstant.ZERO).getJsonSchema());
        if(ObjectUtil.isNull(params)){
            return null;
        }
        Map<String,JSONObject> authParams = new HashMap<>(2);
        authParams.put(HttpParamKind.BODY.name(), params);
        return authParams;
    }

    @Override
    public DataAssetEnums.DataAccessAppAuthType getSupportAuthType() {
        return DataAssetEnums.DataAccessAppAuthType.CONNECTOR_AUTH;
    }

    @Override
    public Map<String, List<ApiConditionPo>> getApiTestAuthInfo(AppAccessPo appAccessPo) {
        Map<String,List<ApiConditionPo>> param = new HashMap<>(2);
        ConnectorAuthConfigDTO config = connectorAuthConfigService.getConnectorAuthConfigById(appAccessPo.getConnectorAuthId());
        ResultDTO<ConnectorApiDetailDTO> connectorApiDetailDto = connectorApiService.detail(config.getConnectorApiId());
        List<ConnectorApiRequestParamDTO> requestParamDTOList = connectorApiDetailDto.getData().getRequestParams();
        if(CollUtil.isNotEmpty(requestParamDTOList)){
            return BeanUtil.copyList(requestParamDTOList, ApiConditionPo.class).stream()
                    .filter((p)->{
                        if(HttpParamKind.BODY == p.getHttpParamKind()){
                            ApiRespParamDTO convert = JsonSchemaConvertUtil.convert(p.getJsonSchema());
                            return CollUtil.isNotEmpty(convert.getChildApiRespParams());
                        }
                        return true;
                    })
                    .collect(Collectors.groupingBy((po)-> po.getHttpParamKind().name()));
        }
        return param;
    }
}
