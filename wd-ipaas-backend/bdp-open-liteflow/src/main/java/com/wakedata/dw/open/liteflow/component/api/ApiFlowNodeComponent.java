package com.wakedata.dw.open.liteflow.component.api;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiComponent;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.DataApiAccessService;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ZhangXueJun
 * @title ApiComponent
 * @date 2021/5/7 16:16
 * @projectName dw-open
 * @description
 */
@Slf4j
public class ApiFlowNodeComponent extends AbstractNodeComponent<ApiOperator> {

    @Override
    protected List<RequestParamMapping> getRequestParamMappings() {
        ApiOperator operator = (ApiOperator) threadLocal.get().getOperator();
        return operator.getRequestParamMappings();
    }

    @Override
    protected JSON afterMappingRequestParams(JSON currentRequestParams) {
        ApiOperator operator = (ApiOperator) threadLocal.get().getOperator();
        // 提前判断参数所属位置
        List<RequestParamMapping> requestParamMappings = operator.getRequestParamMappings();
        Map<String, RequestParamMapping> fieldMappings = new HashMap<>(16);
        for (RequestParamMapping requestParamMapping : requestParamMappings) {
            fieldMappings.put(requestParamMapping.getField() + ":" + requestParamMapping.getHttpParamKind().name(), requestParamMapping);
        }

        ApiComponent component = operator.getComponent();
        List<ApiConditionPo> conditions = component.getParameters();
        DataAssetApiPo dataAssetApi = component.getDataAssetApi();
        for (ApiConditionPo condition : conditions) {
            // 如果api是数据表类型并且参数是FILTER类型，需要拼接前缀，不然无法从fieldMappings匹配到数据
            String assetColumn = DataAssetEnums.DataApiType.NORMAL_TABLE == dataAssetApi.getApiType() && HttpParamKind.FILTER == condition.getHttpParamKind()
                    ? DwOpenConstant.FILTER_PREFIX + condition.getAssetColumns() : condition.getAssetColumns();
            RequestParamMapping requestParamMapping = fieldMappings.get(assetColumn + ":" + condition.getHttpParamKind().name());
            if (requestParamMapping == null) {
                continue;
            }

            requestParamMapping.setHttpParamKind(condition.getHttpParamKind());
        }

        if (currentRequestParams instanceof JSONObject) {
            JSONObject origin = (JSONObject) currentRequestParams;
            JSONObject result = super.assembleHttpRequestParams(origin, fieldMappings);
            return result;
        }

        JSONArray origin = (JSONArray) currentRequestParams;
        JSONArray result = new JSONArray();
        for (Object o : origin) {
            result.add(super.assembleHttpRequestParams((JSONObject) o, fieldMappings));
        }
        return result;
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) {
        Object results = invokeApi(nodeRunTimeContext,currentRequestParams);
//        storeOperatorResultSet(nodeRunTimeContext,results);
        return AbstractNodeComponent.jsonParse(results);
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        ApiOperator operator = (ApiOperator) threadLocal.get().getOperator();
        switch (operator.getComponent().getDataAssetApi().getApiType()) {
            case EXTERNAL_HTTP:
                return OpenApiMsgCodeEnum.w_api_graph_execute_http_operator_error;
            case NORMAL_TABLE:
                return OpenApiMsgCodeEnum.w_api_graph_execute_table_operator_error;
            case CUSTOM_SQL:
                return OpenApiMsgCodeEnum.w_api_graph_execute_custom_operator_error;
            default:
                throw new UnsupportedOperationException("请指定错误码！");
        }
    }

    private Object invokeApi(NodeRunTimeContext nodeRunTimeContext,JSONObject requestParam) {
        ApiOperator operator = (ApiOperator) nodeRunTimeContext.getOperator();
        ApiComponent component = operator.getComponent();

        DataAssetApiPo dataAssetApi = component.getDataAssetApi();
        DataAssetEnums.DataApiType apiType = dataAssetApi.getApiType();
        JSONObject refactorRequestParam = transformApiRequestParams(requestParam);
        if(apiType.equals(DataAssetEnums.DataApiType.NORMAL_TABLE) && dataAssetApi.getOperationType().getValue() < DataAssetEnums.DataApiOperationType.QUERY.getValue()) {

            List<ApiConditionPo> conditions = component.getParameters();

            Object results = nodeRunTimeContext.getDataApiAccessService().dmlDataApiData(
                dataAssetApi,
                nodeRunTimeContext.getAppId(),
                refactorRequestParam,
                conditions
            );
            return buildResultDTO(results);
        } else {
            // 根据请求参数类型判断分页参数所在位置
            DataAssetEnums.ReqMethod reqMethod = dataAssetApi.getReqMethod();
            JSONObject pageParams = buildPageParam(refactorRequestParam, reqMethod);
            //加入鉴权参数
            JSONObject startInputParams = nodeRunTimeContext.getOperatorContainer().getStartInputParams();
            if(ObjectUtil.isNotNull(startInputParams)){
                refactorRequestParam.put(HttpParamKind.COMMON.name(),startInputParams.getJSONObject(HttpParamKind.COMMON.name()));
            }
            Integer pageNo = (Integer) pageParams.get(RequestParamUtils.PAGE_NO);
            Integer pageSize = (Integer) pageParams.get(RequestParamUtils.PAGE_SIZE);
            Object result = nodeRunTimeContext.getDataApiAccessService().readDataApiData(
                    dataAssetApi,
                    nodeRunTimeContext.getAppId(),
                    refactorRequestParam,
                    pageNo,
                    pageSize,
                    (String) pageParams.get(RequestParamUtils.ORDER_BY)
            );
            // 如果API类型是SQL和query能力类型数据表，需要转换成标准的PageResultDTO响应类型的JSON字符串
            if (DataAssetEnums.DataApiType.CUSTOM_SQL == apiType || DataAssetEnums.DataApiType.NORMAL_TABLE == apiType) {
                if (DataAssetEnums.DataApiOperationType.QUERY.equals(dataAssetApi.getOperationType())) {
                    PageResultDTO<Object> pageResultDTO = buildPageResultForSqlAndNormalTable(result, dataAssetApi, refactorRequestParam, pageNo, pageSize);
                    return JSONObject.toJSONString(pageResultDTO);
                } else {
                    return buildResultDTO(result);
                }
            }
            return result;
        }
    }

    private Object buildResultDTO(Object result) {
        ResultDTO<Object> resultDTO = new ResultDTO<>();
        resultDTO.setCode(OpenApiMsgCodeEnum.s_success.getCode());
        resultDTO.setMsg(OpenApiMsgCodeEnum.s_success.getDesc());
        resultDTO.setSuccess(true);
        resultDTO.setData(result);
        return JsonUtil.toJson(resultDTO);
    }

    /**
     * 构建分页参数pageNo、pageSize、orderBy
     */
    private JSONObject buildPageParam(JSONObject refactorRequestParam, DataAssetEnums.ReqMethod reqMethod) {
        JSONObject pageParam = new JSONObject();
        String jsonType = ParamMappingsUtils.isJsonObjectOrJsonArray(refactorRequestParam.get(HttpParamKind.BODY.name()));
        // 如果请求方式是POST并且请求体不是jsonObject格式，直接返回默认值
        if ((DataAssetEnums.ReqMethod.POST == reqMethod && !DataTypeEnum.JSON_OBJECT.getType().equals(jsonType))) {
            pageParam.put(RequestParamUtils.PAGE_NO, PageQuery.DEFAULT_PAGE_NO);
            pageParam.put(RequestParamUtils.PAGE_SIZE, PageQuery.DEFAULT_PAGE_SIZE);
            pageParam.put(RequestParamUtils.ORDER_BY, StringUtils.EMPTY);
        } else {
            JSONObject param = DataAssetEnums.ReqMethod.POST == reqMethod ?
                    refactorRequestParam.getJSONObject(HttpParamKind.BODY.name()) : refactorRequestParam.getJSONObject(HttpParamKind.QUERY.name());
            pageParam.put(RequestParamUtils.PAGE_NO, JsonUtil.getPageParam(param, RequestParamUtils.PAGE_NO, PageQuery.DEFAULT_PAGE_NO));
            pageParam.put(RequestParamUtils.PAGE_SIZE, JsonUtil.getPageParam(param, RequestParamUtils.PAGE_SIZE, PageQuery.DEFAULT_PAGE_SIZE));
            pageParam.put(RequestParamUtils.ORDER_BY, JsonUtil.getParam(param, RequestParamUtils.ORDER_BY, StringUtils.EMPTY));
        }
        return pageParam;
    }
    private PageResultDTO<Object> buildPageResultForSqlAndNormalTable(Object result, DataAssetApiPo dataAssetApi, JSONObject refactorRequestParam, Integer pageNo, Integer pageSize) {
        PageResultDTO<Object> pageResultDTO = new PageResultDTO<>();
        pageResultDTO.setPageNo(pageNo);
        pageResultDTO.setPageSize(pageSize);
        long dataCount = GlobalApplicationContext.getBean(DataApiAccessService.class).getDataCount(dataAssetApi, refactorRequestParam);
        pageResultDTO.setTotalCount(dataCount);
        //相对于SQL，QUERY类型能力数据表返回的结果多包了一层BODY，需要去除
        if (DataAssetEnums.DataApiType.NORMAL_TABLE == dataAssetApi.getApiType()) {
            JSONObject jsonResult = (JSONObject) JSONObject.toJSON(result);
            pageResultDTO.setData(jsonResult.get(HttpParamKind.BODY.name()));
        }else {
            pageResultDTO.setData(result);
        }
        pageResultDTO.setCode(OpenApiMsgCodeEnum.s_success.getCode());
        pageResultDTO.setMsg(OpenApiMsgCodeEnum.s_success.getDesc());
        return pageResultDTO;
    }

    /**
     * 再次转换结构
     *
     * @param requestParam
     * @return
     */
    private JSONObject transformApiRequestParams(JSONObject requestParam) {
        JSONObject refactorRequestParam = new JSONObject();
        JSONObject queryParams = (JSONObject) requestParam.get(HttpParamKind.QUERY.name());
        if (queryParams == null) {
            queryParams = new JSONObject();
        }
        refactorRequestParam.put(HttpParamKind.QUERY.name(), queryParams);
        refactorRequestParam.put(HttpParamKind.FILTER.name(),requestParam.getJSONObject(HttpParamKind.FILTER.name()));

        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiOperator operator = (ApiOperator) nodeRunTimeContext.getOperator();
        ApiComponent component = operator.getComponent();
        DataAssetEnums.DataApiType apiType = component.getDataAssetApi().getApiType();
        if (Objects.requireNonNull(apiType) == DataAssetEnums.DataApiType.EXTERNAL_HTTP
                || Objects.requireNonNull(apiType) == DataAssetEnums.DataApiType.CUSTOM_SQL
                || Objects.requireNonNull(apiType) == DataAssetEnums.DataApiType.NORMAL_TABLE) {
            refactorRequestParam.put(HttpParamKind.HEAD.name(), requestParam.getJSONObject(HttpParamKind.HEAD.name()));
            refactorRequestParam.put(HttpParamKind.BODY.name(), requestParam.get(HttpParamKind.BODY.name()));
        } else {
            // TODO PAGESIZE PAGE ORDER BY 是否可以直接作为参数映射, 后期处理
            JSONObject startInputParams = nodeRunTimeContext.getOperatorContainer().getStartInputParams().getJSONObject(HttpParamKind.QUERY.name());
            if (Objects.nonNull(startInputParams)) {
                if (!queryParams.containsKey(RequestParamUtils.PAGE_NO) && startInputParams.containsKey(RequestParamUtils.PAGE_NO)) {
                    queryParams.put(RequestParamUtils.PAGE_NO, startInputParams.get(RequestParamUtils.PAGE_NO));
                }

                if (!queryParams.containsKey(RequestParamUtils.PAGE_SIZE) && startInputParams.containsKey(RequestParamUtils.PAGE_SIZE)) {
                    queryParams.put(RequestParamUtils.PAGE_SIZE, startInputParams.get(RequestParamUtils.PAGE_SIZE));
                }
            }
        }
        return refactorRequestParam;
    }

    @Override
    public void processCall() throws Exception {
        // 1、处理请求参数映射
        beforeProcessInternal();

        // 2、内部处理算子
        processInternal();

        // 3、处理响应参数映射
        afterProcessInternal();
    }
}
