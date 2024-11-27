package com.wakedata.dw.open.openapi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.ApiConditionDefaultColumnEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.gateway.OpenApiAuthParamThreadLocal;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.DataApiAccessService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.openapi.OpenApiGatewayService;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.wakedata.dw.open.service.utils.RequestParamUtils.DEFAULT_PARAMS;
import static com.wakedata.dw.open.service.utils.RequestParamUtils.ORDER_BY;

/**
 * @author luomeng
 * @date 2022/9/30 17:15
 */
@Slf4j
public abstract class OpenApiGatewayAbstractService implements OpenApiGatewayService {

    /**
     * api信息
     */
    protected DataAssetApiPo dataAssetApiPo;

    /**
     * app信息
     */
    protected AppAccessInfo appAccessInfo;

    /**
     * 请求参数
     */
    protected OpenApiParams openApiParams;

    /**
     * api配置的参数
     */
    protected List<ApiConditionPo> apiConditionPoList;

    /**
     * api有权限返回的列
     */
    protected HashSet<String> accessRuleFields;


    @Override
    public <T> ResultDTO<T> invokeOpenApi(OpenApiParams openApiParams, DataAssetApiPo dataAssetApiPo, AppAccessInfo appAccessInfo) {
        //初始化参数
        initParam(openApiParams, dataAssetApiPo, appAccessInfo);
        // 处理传入的参数
        JSONObject params = RequestParamUtils.requestDetails(openApiParams.getRequest(), dataAssetApiPo, this.apiConditionPoList, openApiParams.getPostData());
        //根据不同api类型处理参数
        before(params);
        //透传鉴权参数
        transmissionAuthParam(params);
        //执行api
        T result = process(params);

        // 构建响应头和响应体
        setResponseHead(this.openApiParams.getResponse(), result, dataAssetApiPo);
        result = setResponseBody(result);
        //构建返回结果
        ResultDTO<T> resultDTO = buildResultDto(params, result, dataAssetApiPo, apiConditionPoList, this.openApiParams.getPageQuery().getPageNo(), this.openApiParams.getPageQuery().getPageSize());
        //根据不同api类型对返回结果进行处理
        ResultDTO<T> dto = after(result, resultDTO);
        return dto == null ? resultDTO : dto;
    }

    /**
     * 鉴权参数传递
     * @param params
     */
    private static void transmissionAuthParam(JSONObject params) {
        Map<String, JSONObject> authParams = OpenApiAuthParamThreadLocal.getAuthParams();
        if(ObjectUtil.isNotEmpty(authParams)){
            JSONObject common = ObjectUtil.defaultIfNull(params.getJSONObject(HttpParamKind.COMMON.name()),new JSONObject());
            common.put(OpenApiAuthParamThreadLocal.AUTH_PARAM_KEY,authParams);
            params.put(HttpParamKind.COMMON.name(),common);
        }
    }

    /**
     * 初始化参数
     *
     * @param openApiParams
     * @param dataAssetApiPo
     * @param appAccessInfo
     */
    private void initParam(OpenApiParams openApiParams, DataAssetApiPo dataAssetApiPo, AppAccessInfo appAccessInfo) {
        //通过apiId查询该API信息中包含的请求参数
        this.apiConditionPoList = GlobalApplicationContext.getBean(OpenApiDataCache.class).getApiConditions(dataAssetApiPo.getDataAssetApiId());
        this.accessRuleFields = GlobalApplicationContext.getBean(OpenApiDataCache.class).getAppApiAssetColumns(dataAssetApiPo, appAccessInfo.getDataAccessAppId());
        this.dataAssetApiPo = dataAssetApiPo;
        this.appAccessInfo = appAccessInfo;
        this.openApiParams = openApiParams;
        // 如果API类型是服务编排并且API是GET请求，增加一个特殊参数用于存放事件接收算子监听的消息
        if (DataAssetEnums.ReqMethod.GET == dataAssetApiPo.getReqMethod() && DataAssetEnums.DataApiType.LITE_FLOW == dataAssetApiPo.getApiType()) {
            this.apiConditionPoList.add(buildEventReceiveDefaultParam(dataAssetApiPo.getDataAssetApiId()));
        }
    }

    /**
     * 执行之前
     */
    public abstract void before(JSONObject params);

    /**
     * 执行之后
     *
     * @return
     */
    public abstract <T> ResultDTO<T> after(T result, ResultDTO<T> resultDTO);

    /**
     * 执行
     *
     * @return
     */
    public abstract <T> T process(JSONObject params);


    /**
     * 设置分页参数
     *
     * @param queryParams
     * @param orderBy
     * @param page
     * @param size
     */
    protected void setPageQueryParam(JSONObject queryParams, String orderBy, int page, int size) {
        queryParams.put(ORDER_BY, orderBy);
        queryParams.put(RequestParamUtils.PAGE_NO, page);
        queryParams.put(RequestParamUtils.PAGE_SIZE, size);
    }

    /**
     * 统一处理响应参数的内容
     *
     * @param <T>    <T>
     * @param result API调用返回的响应参数(HEAD BODY LOG)
     * @return <T>
     */
    @SuppressWarnings("unchecked")
    private <T> T setResponseBody(T result) {
        // 处理无HEAD和BODY结构的result
        if (!(result instanceof JSONObject)) {
            return result;
        }
        return responseBodyHandle(result);
    }

    /**
     * 响应体处理
     *
     * @param result
     * @param <T>
     * @return
     */
    public abstract <T> T responseBodyHandle(T result);

    /**
     * 响应头处理
     *
     * @param apiId
     * @return
     */
    public abstract List<ApiRespParamDTO> responseHeadHandle(Integer apiId);

    /**
     * 构建响应头
     *
     * @param <T>          <T>
     * @param response     响应对象
     * @param result       API执行结果(HEAD和BODY)
     * @param dataAssetApi api
     */
    private <T> void setResponseHead(HttpServletResponse response, T result, DataAssetApiPo dataAssetApi) {
        // 只处理JsonObject的情况(HEAD和BODY结构)
        if (!(result instanceof JSONObject)) {
            return;
        }

        // 获取调用API返回的响应头并转换为Map<响应头key,响应头key对应的值>
        final Map<String, String> apiResponseMap = new HashMap<>(16);
        Object head = ((JSONObject) result).get(HttpParamKind.HEAD.name());
        if (ObjectUtil.isNotNull(head)) {
            if (head instanceof JSONObject) {
                ((JSONObject) head).entrySet().stream()
                        .filter(entry -> StringUtils.isNotEmpty(entry.getKey()) || entry.getValue() != null)
                        .forEach(entry -> apiResponseMap.put(entry.getKey(), ((JSONObject) head).getString(entry.getKey())));
            } else {
                ((HttpHeaders) head).entrySet().stream()
                        .filter(entry -> StringUtils.isNotEmpty(entry.getKey()) || CollectionUtil.isNotEmpty(entry.getValue()))
                        .forEach(entry -> apiResponseMap.put(entry.getKey(), entry.getValue().get(DwOpenConstant.DEFAULT_FIRST)));
            }
        }

        // 获取自定义响应头
        Integer apiId = dataAssetApi.getDataAssetApiId();
        List<ApiRespParamDTO> apiRespParamDTOList = responseHeadHandle(apiId);

        if (CollUtil.isEmpty(apiRespParamDTOList)) {
            return;
        }

        Map<String, String> defaultMap = new HashMap<>(16);
        for (ApiRespParamDTO apiRespParamDTO : apiRespParamDTOList) {
            if (ObjectUtil.equal(HttpParamKind.HEAD, apiRespParamDTO.getType())) {
                defaultMap.put(apiRespParamDTO.getAssetColumns(), apiRespParamDTO.getDefaultValue());
            }
        }
        if (CollectionUtil.isEmpty(defaultMap)) {
            return;
        }

        // 设置自定义响应头(优先获取API返回的响应头值,没有再去默认值)
        for (String headName : defaultMap.keySet()) {
            // 字段content-type不支持自定义value
            if (StringUtils.containsIgnoreCase(headName, DwOpenConstant.CONTENT_TYPE)) {
                continue;
            }
            String value = StringUtils.isNotEmpty((apiResponseMap.get(headName))) ? apiResponseMap.get(headName) : defaultMap.get(headName);
            response.setHeader(headName, value);
        }
    }


    /**
     * 构建ResultDTO（根据query参数是否传入分页参数决定返回ResultDTO还是PageResultDTO）
     * 针对api处理，有可能外部api的分页参数不叫pageNo、pageSize等
     * 但是这里只是初步生成一个ResultDTO/PageResultDTO，在after方法中有需要会摒弃这里生成的resultDTO，重新根据映射规则生成
     *
     * @param params          请求参数
     * @param dataAssetApi    DataAssetApiPo
     * @param apiConditions   API请求参数
     * @param defaultPageNo   分页参数，页码
     * @param defaultPageSize 分页参数，每页查询条数
     * @param <T>             T
     * @return ResultDTO
     */
    private <T> ResultDTO<T> buildResultDto(JSONObject params, T data, DataAssetApiPo dataAssetApi
            , List<ApiConditionPo> apiConditions, int defaultPageNo, int defaultPageSize) {
        // 如果API是服务编排类型，返回ResultDTO
        if (DataAssetEnums.DataApiType.LITE_FLOW == dataAssetApi.getApiType()) {
            ResultDTO<T> resultDTO = new ResultDTO<>();
            buildResultDto(resultDTO, data);
            return resultDTO;
        }
        boolean hasPageQuery = hasPageQuery(apiConditions);
        if (hasPageQuery) {
            // 根据请求参数类型判断分页参数所在位置
            DataAssetEnums.ReqMethod reqMethod = dataAssetApi.getReqMethod();
            JSONObject pageParam = DataAssetEnums.ReqMethod.POST == reqMethod ?
                    params.getJSONObject(HttpParamKind.BODY.name()) : params.getJSONObject(HttpParamKind.QUERY.name());
            Integer pageNo = JsonUtil.getPageParam(pageParam, RequestParamUtils.PAGE_NO, defaultPageNo);
            Integer pageSize = JsonUtil.getPageParam(pageParam, RequestParamUtils.PAGE_SIZE, defaultPageSize);
            PageResultDTO<T> pageResultDTO = new PageResultDTO<>();
            pageResultDTO.setPageNo(pageNo);
            pageResultDTO.setPageSize(pageSize);
            long dataCount = 0L;
            DataAssetEnums.DataApiType apiType = dataAssetApi.getApiType();
            boolean condition = data instanceof List && (DataAssetEnums.DataApiType.CUSTOM_SQL.equals(apiType) || DataAssetEnums.DataApiType.NORMAL_TABLE.equals(apiType));
            if (condition) {
                //去除默认参数
                for (String defaultParam : DEFAULT_PARAMS) {
                    pageParam.remove(defaultParam);
                }
                dataCount = GlobalApplicationContext.getBean(DataApiAccessService.class).getDataCount(dataAssetApi, params);
            }
            pageResultDTO.setTotalCount(dataCount);
            buildResultDto(pageResultDTO, data);
            return pageResultDTO;
        }
        ResultDTO<T> resultDTO = new ResultDTO<>();
        buildResultDto(resultDTO, data);
        return resultDTO;
    }

    /**
     * 判断接口是否包含分页参数（这种判断只能判断惟客云类型的api，也许外部api的分页参数名称和下面的不同）
     *
     * @return true：包含、false：不包含
     */
    private boolean hasPageQuery(List<ApiConditionPo> apiConditions) {
        String pageNoParamName = ApiConditionDefaultColumnEnum.PAGE_NO.getAssetColumns();
        String pageSizeParamName = ApiConditionDefaultColumnEnum.PAGE_SIZE.getAssetColumns();
        // ApiConditionPo集合中是否存在assetColumns属性为pageNo或者pageSize的值
        boolean assetColumnsHasQueryParam = apiConditions.stream()
                .anyMatch(apiConditionPo -> pageNoParamName.equals(apiConditionPo.getAssetColumns()) || pageSizeParamName.equals(apiConditionPo.getAssetColumns()));
        // ApiConditionPo中请求参数为body并且body参数中存在pageNo或者pageSize
        boolean jsonSchemaHasQueryParam = false;
        Optional<ApiConditionPo> bodyParam = apiConditions.stream()
                .filter(apiConditionPo -> DataAssetEnums.FiledTypeEnums.PARAMETERS == apiConditionPo.getType() && HttpParamKind.BODY == apiConditionPo.getHttpParamKind())
                .findFirst();
        ApiConditionPo apiConditionPo = bodyParam.orElse(new ApiConditionPo());
        String jsonSchema = apiConditionPo.getJsonSchema();
        if (StringUtils.isNotBlank(jsonSchema)) {
            ApiRespParamDTO apiRespParamDTO = JsonSchemaConvertUtil.convert(jsonSchema);
            jsonSchemaHasQueryParam = schemaHasPageQuery(apiRespParamDTO.getChildApiRespParams(), DwOpenConstant.JSON_SCHEMA_INIT_ID);
        }
        return assetColumnsHasQueryParam || jsonSchemaHasQueryParam;
    }

    /**
     * Json Schema中是否包含分页参数
     *
     * @param childApiRespParams 子项参数
     * @param parentId           父级id
     * @return true：包含、false：不包含
     */
    private boolean schemaHasPageQuery(List<ApiRespParamDTO> childApiRespParams, Integer parentId) {
        String pageNoParamName = ApiConditionDefaultColumnEnum.PAGE_NO.getAssetColumns();
        String pageSizeParamName = ApiConditionDefaultColumnEnum.PAGE_SIZE.getAssetColumns();
        List<ApiRespParamDTO> children = childApiRespParams.stream().filter(x -> parentId.equals(x.getParentId())).collect(Collectors.toList());
        for (ApiRespParamDTO apiRespParamDTO : children) {
            String assetColumns = apiRespParamDTO.getAssetColumns();
            if (pageNoParamName.equals(assetColumns) || pageSizeParamName.equals(assetColumns)) {
                return Boolean.TRUE;
            }
            if (CollectionUtil.isNotEmpty(apiRespParamDTO.getChildApiRespParams())) {
                return schemaHasPageQuery(apiRespParamDTO.getChildApiRespParams(), apiRespParamDTO.getId());
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 设置ResultDTO公共参数（不包含PageResultDTO）
     *
     * @param resultDTO ResultDTO
     * @param data      data
     * @param <T>       T
     */
    private <T> void buildResultDto(ResultDTO<T> resultDTO, T data) {
        resultDTO.setData(data);
        resultDTO.setCode(OpenApiMsgCodeEnum.s_success.getCode());
        resultDTO.setMsg(OpenApiMsgCodeEnum.s_success.getDesc());
    }

    /**
     * 构建事件接收算子GET接收参数
     *
     * @param apiId API ID
     * @return ApiConditionPo
     */
    private ApiConditionPo buildEventReceiveDefaultParam(Integer apiId) {
        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setDataAssetId(apiId);
        apiConditionPo.setAssetColumns(DwOpenConstant.EVENT_RECEIVE_QUERY_PARAM_NAME);
        apiConditionPo.setAssetDatatype(DataAssetEnums.AssetDataTypeEnum.STRING.getValue());
        apiConditionPo.setRequired(Boolean.FALSE);
        apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
        apiConditionPo.setHttpParamKind(HttpParamKind.QUERY);
        return apiConditionPo;
    }
}
