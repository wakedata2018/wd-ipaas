package com.wakedata.dw.open.openapi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.DataApiAccessServiceImpl;
import com.wakedata.dw.open.service.impl.api.attr.ApiFlowAttrService;
import com.wakedata.dw.open.service.impl.api.strategy.impl.LiteFlowApiInvokeStrategy;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.wakedata.dw.open.service.utils.RequestParamUtils.ORDER_BY;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 流程编排
 *
 * @author luomeng
 * @date 2022/10/8 11:37
 */
@Service
@Scope(value = SCOPE_PROTOTYPE,proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OpenApiGatewayLiteflowService extends OpenApiGatewayAbstractService {

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Resource
    private ApiFlowAttrService apiFlowAttrService;

    @Resource
    private DataApiAccessServiceImpl dataApiAccessService;

    @Override
    public void before(JSONObject params) {
        String orderBy = this.openApiParams.getRequest().getParameter(ORDER_BY);
        JSONObject headParams = params.getJSONObject(HttpParamKind.HEAD.name());
        JSONObject queryParams = params.getJSONObject(HttpParamKind.QUERY.name());
        String bodyParams = params.getString(HttpParamKind.BODY.name());
        JSONObject startParams = new JSONObject();
        buildStartParams(this.apiConditionPoList, headParams, queryParams, bodyParams, startParams, this.openApiParams.getPageQuery().getPageNo(), this.openApiParams.getPageQuery().getPageSize(), orderBy);
    }

    /**
     * 处理流程编排中开始算子的参数透传
     *
     * @param apiConditions 流程编排API的参数条件
     * @param headParams    请求头HEAD
     * @param queryParams   请求参数QUERY
     * @param bodyParams    请求体BODY
     * @param startParams   开始算子参数
     * @param page          page
     * @param size          size
     * @param orderBy       orderBy
     */
    private void buildStartParams(List<ApiConditionPo> apiConditions,
                                  JSONObject headParams, JSONObject queryParams, String bodyParams, JSONObject startParams,
                                  int page, int size, String orderBy) {
        // 初始化HEAD QUERY BODY的参数载体
        JSONObject startHead = new JSONObject();
        JSONObject startQuery = new JSONObject();
        this.setPageQueryParam(startQuery, orderBy, page, size);
        if (CollectionUtil.isEmpty(apiConditions)) {
            return;
        }

        apiConditions.forEach(x -> {
            // 过滤type = DataAssetEnums.FiledTypeEnums.PARAMETERS的condition
            if (ObjectUtil.notEqual(DataAssetEnums.FiledTypeEnums.PARAMETERS, x.getType())) {
                return;
            }
            // 判断请求中是否包含了这个参数，如果存在就以请求参数值为准
            String paramName = x.getAssetColumns();
            HttpParamKind httpParamKind = x.getHttpParamKind();
            if (RequestParamUtils.DEFAULT_PARAMS.contains(paramName)) {
                return;
            }

            // 根据不同的HttpKind设置不同的参数
            switch (httpParamKind) {
                case HEAD:
                    boolean existHead = headParams.containsKey(paramName);
                    startHead.put(paramName, existHead ? headParams.get(paramName) : null);
                    break;
                case QUERY:
                    boolean existQuery = queryParams.containsKey(paramName);
                    startQuery.put(paramName, existQuery ? queryParams.get(paramName) : null);
                    break;
                case BODY:
                    String jsonType = ParamMappingsUtils.isJsonObjectOrJsonArray(bodyParams);
                    if (DataTypeEnum.JSON_ARRAY.getType().equals(jsonType)) {
                        JSONArray startBody = JSONArray.parseArray(bodyParams);
                        startParams.put(HttpParamKind.BODY.name(), startBody);
                    }else {
                        JSONObject startBody = JSONObject.parseObject(bodyParams);
                        startParams.put(HttpParamKind.BODY.name(), startBody);
                    }
                    break;
                default:
                    break;
            }
        });

        // 组装start节点的参数,进行透传
        startParams.put(HttpParamKind.HEAD.name(), startHead);
        startParams.put(HttpParamKind.QUERY.name(), startQuery);
        queryParams.put(Operator.VERTEX_OPERATOR_ID, startParams);
    }

    @Override
    public <T> T process(JSONObject params) {

        ApiFlowAttr apiFlowAttr = openApiDataCache.getApiFlowAttr(dataAssetApiPo.getDataAssetApiId());
        this.dataAssetApiPo.setApiAttr(apiFlowAttr);
        apiFlowAttrService.saveLiteFlowIfNecessary(apiFlowAttr, false);

        return (T) new LiteFlowApiInvokeStrategy(
                dataApiAccessService,
                dataAssetApiPo.getApiType(),
                dataAssetApiPo,
                appAccessInfo.getDataAccessAppId(),
                params,
                accessRuleFields
        ).invoke();
    }



    @Override
    public <T> ResultDTO<T> after(T result, ResultDTO<T> resultDTO) {
        return resultDTO;
    }

    @Override
    public <T> T responseBodyHandle(T result) {
        JSONObject jsonObject = (JSONObject) result;
        Object body = jsonObject.get(HttpParamKind.BODY.name());
        String log = jsonObject.getString(DwOpenConstant.ENABLE_LOG);

        // 无日志的情况
        if (StringUtils.isEmpty(log)) {
            return (body instanceof JSONArray)
                    ? (T) jsonObject.getJSONArray(HttpParamKind.BODY.name())
                    : (T) jsonObject.getJSONObject(HttpParamKind.BODY.name());
        }
        // 有日志的情况(TODO 后续会将日志挪入响应头 该情况可忽略)
        if (StringUtils.isNotEmpty(log)) {
            // 流程编排返回数组
            if (body instanceof JSONArray) {
                return result;
            }
            // 流程编排返回非数组
            JSONObject bodyJsonObject = jsonObject.getJSONObject(HttpParamKind.BODY.name());
            jsonObject = bodyJsonObject == null ? new JSONObject() : bodyJsonObject;

            if (StringUtils.isNotEmpty(log)) {
                jsonObject.put(DwOpenConstant.ENABLE_LOG, log);
            }
            return (T) jsonObject;
        }
        return result;
    }

    @Override
    public List<ApiRespParamDTO> responseHeadHandle(Integer apiId) {
        return openApiDataCache.getApiLiteFlowParams(apiId);
    }

    @Override
    public DataAssetEnums.DataApiType apiType() {
        return DataAssetEnums.DataApiType.LITE_FLOW;
    }
}
