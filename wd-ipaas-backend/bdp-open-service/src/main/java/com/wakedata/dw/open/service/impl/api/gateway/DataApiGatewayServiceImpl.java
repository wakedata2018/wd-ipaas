package com.wakedata.dw.open.service.impl.api.gateway;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.common.core.hashids.HashidsUtil;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.AssetDetailKeyConstant;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.gateway.OpenApiAuthHandler;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.Markdown;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import com.wakedata.dw.open.service.api.*;
import com.wakedata.dw.open.service.api.dto.ApiGroupDTO;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDTO;
import com.wakedata.dw.open.service.api.dto.PublicReturnParametersDTO;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import com.wakedata.dw.open.service.impl.api.helper.ApiGroupHelper;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import com.wakedata.dw.open.service.utils.DocumentMarkDownUtil;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.service.vo.DocumentApiDetailVo;
import com.wakedata.dw.open.utils.ColumnTypeConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yiyufeng
 * @title DataApiGatewayServiceImpl
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
public class DataApiGatewayServiceImpl implements DataApiGatewayService {

    @Autowired
    private DwOpenCommonConfig dwOpenCommonConfig;
    @Autowired
    private AccessTokenService accessTokenService;
    @Resource
    private ApiConditionMapper apiConditionMapper;
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Resource
    private ApiResponseParamService apiResponseParamService;
    @Resource
    private ApiGroupHelper apiGroupHelper;
    @Resource
    private OpenApiAuthHandler openApiAuthHandler;
    @Resource
    private AppAccessService appAccessService;
    @Resource
    private ApiGroupService apiGroupService;


    @Override
    public ResultDTO export() {
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setDataAssetPublishStatus(DataAssetPublishStatusEnum.PUBLISH);
        List<DataAssetApiPo> dataAssetApiList = dataAssetApiService.find(dataAssetApiPo);
        List<Map<String, Object>> exportResult = new ArrayList<>();
        for (DataAssetApiPo assetApiPo : dataAssetApiList) {
            // 这里时间戳参数传null会导致生成token的时候出问题，但是现在页面没有导出功能，以后需要用到再处理 TODO
            Map<String, Object> map = getApiConditions(assetApiPo, null,false);
            exportResult.add(map);
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(exportResult);
        return resultDTO;
    }

    /**
     * 获取公共请求分页参数列表（有需要在放开这个方法）
     *
     * @param dataAssetApiId API ID
     * @param datasourceKind 数据库类型枚举类
     * @return 请求分页参数列表
     */
    @Deprecated
    public static List<ApiConditionPo> getDefaultDataSourceQueryCondition(Integer dataAssetApiId, DatasourceTypeEnum datasourceKind) {
        List<ApiConditionPo> results = new ArrayList<>();
        if (Objects.isNull(datasourceKind)) {
            return results;
        }
        if (DatasourceTypeEnum.HBASE == datasourceKind) {
            results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.ROW_KEY, "rowkey", dataAssetApiId));
        } else {
            results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.PAGE_NO, "1", dataAssetApiId));
            results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.PAGE_SIZE, "10", dataAssetApiId));
        }
        return results;
    }


    @Override
    public ResultDTO<Map<String, Object>> apiDetail(Integer dataAssetId, String timestamp,Boolean isApiTest) {
        DataAssetApiPo assetApi = dataAssetApiService.detail(dataAssetId);
        Map<String, Object> apiConditions = getApiConditions(assetApi, timestamp,isApiTest);
        ResultDTO<Map<String, Object>> resultDTO = new ResultDTO<>();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(apiConditions);
        return resultDTO;
    }

    @Override
    public ResultDTO<Map<String, Object>> getRequireInput(Integer dataAssetId) {
        Map<String, Object> results = new HashMap<>();
        //设置公共请求参数
        results.put("requiredInput", getApiDocumentDefaultParams(dataAssetId));
        //设置公共返回参数
        results.put("returnOutput", getPublicReturnParameters());
        return ResultDTO.success(results);
    }


    @Override
    public Map<String, List<ApiConditionPo>> chooseAppTest(Long lesseeId, String appKey) {
        AppAccessPo appAccessPo = appAccessService.queryAppInfoByKey(appKey);
        if(ObjectUtil.isNull(appAccessPo) || !lesseeId.equals(appAccessPo.getLesseeId())){
            throw new OpenException(MsgCodeEnum.w_app_lessee_not_exists);
        }
        Map<String, List<ApiConditionPo>> authMap = openApiAuthHandler.getOpenApiAuthService(appAccessPo.getAuthType()).getApiTestAuthInfo(appAccessPo);
        if(ObjectUtil.isNull(authMap)){
            authMap = new HashMap<>(2);
        }
        List<ApiConditionPo> conditionPoList = ObjectUtil.defaultIfNull(authMap.get(HttpParamKind.QUERY.name()),new ArrayList<>());
        conditionPoList.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.APP_KEY,appAccessPo.getDataAccessKey(),0));
        authMap.put(HttpParamKind.QUERY.name(),conditionPoList);
        return authMap;
    }

    private List<PublicReturnParametersDTO> getPublicReturnParameters() {
        //构造公共返回参数
        List<PublicReturnParametersDTO> publicReturnParametersDTOList = new ArrayList<>();
        //循环获取枚举的属性
        EnumSet<PublicReturnParametersEnum> weekSet = EnumSet.allOf(PublicReturnParametersEnum.class);
        for (PublicReturnParametersEnum publicReturnParametersEnum : weekSet) {
            PublicReturnParametersDTO publicReturnParametersDTO = new PublicReturnParametersDTO();
            publicReturnParametersDTO.setAttributeName(publicReturnParametersEnum.getAttributeName());
            publicReturnParametersDTO.setAttributeType(ColumnTypeConvertUtil.convertParamType(publicReturnParametersEnum.getAttributeType()));
            publicReturnParametersDTO.setAttributeDescribe(publicReturnParametersEnum.getAttributeDesc());
            publicReturnParametersDTOList.add(publicReturnParametersDTO);
        }
        return publicReturnParametersDTOList;
    }

    public Map<String, Object> getApiConditions(DataAssetApiPo assetApi, String timestamp,Boolean isApiTest) {
        Map<String, Object> results = getDefaultParams(assetApi, timestamp,isApiTest);
        results.put(AssetDetailKeyConstant.KEY_METHOD, assetApi.getReqMethod().name());
        // 获取接口分组名称
        DataAssetApiDTO dataAssetApiDTO = BeanUtil.copyProperties(assetApi, DataAssetApiDTO.class);
        Map<Integer, String> groupMap = apiGroupHelper.getGroupList(Collections.singletonList(dataAssetApiDTO));
        assetApi.setApiGroupName(groupMap.get(dataAssetApiDTO.getApiGroupId()));
        assetApi.setDataAssetApiMethod(assetApi.getDataAssetApiMethod());
        if(isApiTest){
            assetApi.setDataAssetApiUri(openApiAuthHandler.getPlatformTestApiPrefix() + assetApi.getDataAssetApiMethod());
        }else{
            assetApi.setDataAssetApiUri(openApiAuthHandler.getPlatformDefaultApiPrefix() + assetApi.getDataAssetApiMethod());
        }
        results.put(AssetDetailKeyConstant.KEY_BASE_INFO, assetApi);

        if (!Objects.isNull(assetApi.getDataSourceId())) {
            DataSourcePo dataSourcePo = dataSourceService.show(assetApi.getDataSourceId());
            if (!Objects.isNull(dataSourcePo)) {
                results.put(AssetDetailKeyConstant.KEY_DATA_SOURCE, dataSourcePo);
            }
        }

        ApiConditionPo record = new ApiConditionPo();
        record.setRequired(true);
        record.setDataAssetId(assetApi.getDataAssetApiId());
        record.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);

        List<ApiConditionPo> dbApiConditions = apiConditionMapper.select(record);
        List<ApiConditionPo> defaultApiConditions = (List<ApiConditionPo>) results.get(AssetDetailKeyConstant.KEY_REQUIRED_INPUT);
        //流程编排api才显示日志接口
        if(assetApi.getApiType() == DataAssetEnums.DataApiType.LITE_FLOW) {
            defaultApiConditions.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.ENABLE_LOG, "false", assetApi.getDataAssetApiId()));
        }
        for (ApiConditionPo dbApiCondition : dbApiConditions) {
            if (defaultApiConditions.contains(dbApiCondition)) {
                continue;
            }
            defaultApiConditions.add(dbApiCondition);
        }

        record.setRequired(false);
        List<ApiConditionPo> optionConditions = apiConditionMapper.select(record);
        //比对必填和可选参数中是否存在Content-Type参数，存在则不进行处理，不存在则在必填参数中返回一个默认值
        setDefaultContentType(defaultApiConditions,optionConditions);
        results.put(AssetDetailKeyConstant.KEY_REQUIRED_INPUT, defaultApiConditions);
        results.put(AssetDetailKeyConstant.KEY_OPTIONAL_INPUT, optionConditions);
        record = new ApiConditionPo();
        record.setDataAssetId(assetApi.getDataAssetApiId());
        record.setType(DataAssetEnums.FiledTypeEnums.RESULT);
        List<ApiConditionPo> select = apiConditionMapper.select(record);
        results.put(AssetDetailKeyConstant.KEY_RESPONSE, select);

        // 返回响应头和响应体(HTTP),解析成树便于前端展示
        List<ApiRespParamDTO> apiRespParamDTOListTree =
                ApiResponseHelper.buildApiResponseDTOList(apiResponseParamService.listByApiId(assetApi.getDataAssetApiId()));
        results.put(DwOpenConstant.API_DETAIL_HTTP_RESPONSE,apiRespParamDTOListTree);

        // 返回响应头和响应体(服务编排),解析成树便于前端展示
        List<ApiRespParamDTO> liteFlowResultTree =
                ApiResponseHelper.buildApiResponseDTOList(apiResponseParamService.findLiteflowResult(assetApi.getDataAssetApiId()));
        results.put(DwOpenConstant.API_DETAIL_LIFT_FLOW_RESPONSE,liteFlowResultTree);

        Map<String, Object> sample = new HashMap<>();
        List<Map<String, Object>> resultDataList = new ArrayList<>(1);
        if (!optionConditions.isEmpty()) {
            ApiConditionPo optionCondition = optionConditions.get(0);
            optionCondition.setId(null);
            sample.put(optionCondition.getAssetColumns(), getSampleValue(optionCondition.getAssetDatatype()));
            resultDataList.add(sample);
        }
        PageResultDTO pageResultDTO = new PageResultDTO();
        pageResultDTO.setErrorCode(MsgCodeEnum.s_success.getCode());
        pageResultDTO.setErrorMessage(MsgCodeEnum.s_success.getDesc());
        pageResultDTO.setSuccess(true);
        pageResultDTO.setData(resultDataList);
        results.put(AssetDetailKeyConstant.KEY_NORMAL_RETURN, pageResultDTO);
        PageResultDTO errorResult = new PageResultDTO();
        errorResult.setFailed(MsgCodeEnum.s_error);
        results.put(AssetDetailKeyConstant.KEY_ERROR_RETURN, errorResult);
        setApiDocUrl(assetApi, results);
        return results;
    }

    /**
     * 设置对应的文档链接，未发布的API不需要设置
     *
     * @param assetApi DataAssetApiPo
     * @param results  返回结果
     */
    private void setApiDocUrl(DataAssetApiPo assetApi, Map<String, Object> results) {
        if (DataAssetPublishStatusEnum.PUBLISH == assetApi.getDataAssetPublishStatus()) {
            String apiDocUrl = String.format(dwOpenCommonConfig.getApiUrl(), HashidsUtil.encodeDefault(assetApi.getDataAssetApiId()));
            results.put(AssetDetailKeyConstant.KEY_API_DOC_URL, apiDocUrl);
            return;
        }
        results.put(AssetDetailKeyConstant.KEY_API_DOC_URL, null);
    }

    private void setDefaultContentType(List<ApiConditionPo> defaultApiConditions, List<ApiConditionPo> optionConditions) {
        //新增或者编辑API时增加的Content-Type参数会因为是否是必填，而过滤到不同的List，当这两个List都不存在Content-Type字段时增加一个默认值
        boolean defaultApiConditionResult = defaultApiConditions.stream().anyMatch(s -> RequestParamUtils.CONTENT_TYPE.equalsIgnoreCase(s.getAssetColumns()));
        boolean optionConditionResult = optionConditions.stream().anyMatch(s -> RequestParamUtils.CONTENT_TYPE.equalsIgnoreCase(s.getAssetColumns()));
        if (!defaultApiConditionResult && !optionConditionResult) {
            ApiConditionPo apiConditionPo = new ApiConditionPo();
            apiConditionPo.setAssetColumns(RequestParamUtils.CONTENT_TYPE);
            apiConditionPo.setAssetDatatype("string");
            apiConditionPo.setDescriptions("默认请求参数格式");
            apiConditionPo.setHttpParamKind(HttpParamKind.HEAD);
            apiConditionPo.setRequired(true);
            apiConditionPo.setDefaultInputParam(true);
            apiConditionPo.setSample(RequestParamUtils.CONTENT_TYPE_JSON);
            apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
            apiConditionPo.setTypeAttr(DataAssetEnums.FiledTypeAttrEnums.OPERATOR);
            defaultApiConditions.add(apiConditionPo);
        } else {
            defaultApiConditions.stream().filter(x -> RequestParamUtils.CONTENT_TYPE.equalsIgnoreCase(x.getAssetColumns()))
                    .findFirst().ifPresent(x -> x.setDefaultInputParam(true));
            optionConditions.stream().filter(x -> RequestParamUtils.CONTENT_TYPE.equalsIgnoreCase(x.getAssetColumns()))
                    .findFirst().ifPresent(x -> x.setDefaultInputParam(true));
        }
    }


    public static List<ApiConditionPo> defaultApiCondition(DataAssetApiPo assetApi, DatasourceTypeEnum datasourceKind) {
        Integer dataAssetApiId = assetApi.getDataAssetApiId();
        List<ApiConditionPo> results = getBaseDefaultApiConditionList(assetApi, datasourceKind);
        results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN_ACCESS_TOKEN, "", dataAssetApiId));
        return results;
    }

    /**
     * 获取公共请求参数列表
     *
     * @param assetApi       DataAssetApiPo
     * @param datasourceKind 数据库类型枚举类
     * @return 请求参数列表
     */
    private static List<ApiConditionPo> getBaseDefaultApiConditionList(DataAssetApiPo assetApi, DatasourceTypeEnum datasourceKind) {
        Integer dataAssetApiId = assetApi.getDataAssetApiId();
        List<ApiConditionPo> results = new ArrayList<>();
        String sampleTimeStamp = String.valueOf(System.currentTimeMillis());
        results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.TIMESTAMP, sampleTimeStamp, dataAssetApiId));
        return results;
    }

    /**
     * 获取默认的请求参数，并生成临时Token
     *
     * @param assetApi       DataAssetApiPo
     * @param datasourceKind 数据库类型枚举类
     * @param timestamp      时间戳
     * @return API请求参数列表
     */
    private List<ApiConditionPo> defaultApiConditionGeneralTestToken(DataAssetApiPo assetApi, DatasourceTypeEnum datasourceKind, String timestamp,Boolean isApiTest) {
        Integer dataAssetApiId = assetApi.getDataAssetApiId();
        List<ApiConditionPo> results = getBaseDefaultApiConditionList(assetApi, datasourceKind);
        if(!isApiTest){
            results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN_ACCESS_TOKEN, accessTokenService.generate(timestamp).getAccessToken(), dataAssetApiId));
            results.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN, IdUtil.fastSimpleUUID(), dataAssetApiId));
        }
        return results;
    }

    /**
     * 获取默认请求参数
     *
     * @param assetApi  DataAssetApiPo
     * @param timestamp 时间戳
     * @return 请求参数MAP
     */
    private Map<String, Object> getDefaultParams(DataAssetApiPo assetApi, String timestamp,Boolean isApiTest) {
        Map<String, Object> results = new HashMap<>();
        results.put(AssetDetailKeyConstant.KEY_METHOD, "GET");
        results.put(AssetDetailKeyConstant.KEY_RESPONSE_CONTENT_TYPE, "application/json;charset=utf8");
        results.put(AssetDetailKeyConstant.KEY_REQUEST_CONTENT_TYPE, "application/x-www-form-urlencoded");

        DatasourceTypeEnum datasourceKind = null;
        Integer dataSourceId = assetApi.getDataSourceId();
        if (Objects.nonNull(dataSourceId)) {
            DataSourcePo dataSourcePo = dataSourceService.show(dataSourceId);
            datasourceKind = dataSourcePo.getDbType();
        }
        results.put(AssetDetailKeyConstant.KEY_REQUIRED_INPUT, defaultApiConditionGeneralTestToken(assetApi, datasourceKind, timestamp,isApiTest));
        return results;
    }

    /**
     * API文档获取公共请求参数
     *
     * @param dataAssetApiId API ID
     * @return 请求参数MAP
     */
    private List<ApiConditionPo> getApiDocumentDefaultParams(Integer dataAssetApiId) {
        List<ApiConditionPo> list = new ArrayList<>();
        String sampleTimeStamp = String.valueOf(System.currentTimeMillis());
        list.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.TIMESTAMP, sampleTimeStamp, dataAssetApiId));
        list.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN_ACCESS_TOKEN, "testAt19281AE9E21787C3426B4EB02DB178E3", dataAssetApiId));
        list.add(ApiConditionPo.buildRequestParam(ApiConditionDefaultColumnEnum.SIGN, "testAt08436B0ACA4CDF9C7B251DC7A44770F0", dataAssetApiId));
        return list;
    }

    private Object getSampleValue(String dataType) {
        if(StringUtils.isEmpty(dataType)){
            return "其他类型";
        }
        Random random = new Random();
        switch (dataType) {
            case "varchar":
                return "样例";
            case "datatime":
                return new Date();
            case "timestamp":
                return System.currentTimeMillis();
            case "text":
                return "text value";
            case "int":
                return random.nextInt();
            case "double":
                return random.nextDouble();
            case "bigint":
                return random.nextLong();
            case "decimal":
                return random.nextDouble();
            case "time":
                return "12:12:32";
            case "tinyint":
                return (char) 5;
            case "smallint":
                return (short) 12345;
            case "float":
                return random.nextFloat();
            case "longtext":
                return "a long text ";
            case "date":
                return "2019-11-12";
            case "char":
                return 'a';
            default:
                return "其他类型";
        }
    }
}