package com.wakedata.dw.open.service.impl.api;

import com.google.common.collect.Lists;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.helper.SqlBlockAttackInnerHelper;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.datasource.model.SqlAnalysisDo;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.enums.DataAssetEnums.DataApiType;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.gateway.OpenApiAuthHandler;
import com.wakedata.dw.open.liteflow.DataAssetApiLiteflowService;
import com.wakedata.dw.open.mapper.AppApprovalMapper;
import com.wakedata.dw.open.mapper.api.*;
import com.wakedata.dw.open.mapper.api.attr.EventReceiveApiMapper;
import com.wakedata.dw.open.mapper.api.attr.EventSendApiMapper;
import com.wakedata.dw.open.mapper.datasource.DataSourceMapper;
import com.wakedata.dw.open.model.api.*;
import com.wakedata.dw.open.model.api.dto.ApiInfoDTO;
import com.wakedata.dw.open.model.api.dto.AppAuthApiReqDTO;
import com.wakedata.dw.open.model.api.dto.AuthApiRespDTO;
import com.wakedata.dw.open.model.api.dto.NotAuthApiReqParam;
import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowRelation;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.domain.*;
import com.wakedata.dw.open.model.event.SubscribeRecordPo;
import com.wakedata.dw.open.model.query.ApiQuery;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.*;
import com.wakedata.dw.open.service.api.dto.*;
import com.wakedata.dw.open.service.approval.AppApprovalService;
import com.wakedata.dw.open.service.event.SubscribeRecordService;
import com.wakedata.dw.open.service.event.TopicService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.impl.api.attr.*;
import com.wakedata.dw.open.service.impl.api.helper.ApiGroupHelper;
import com.wakedata.dw.open.service.impl.utils.ParamBuildUtil;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskHelper;
import com.wakedata.dw.open.service.swagger.SwaggerInfoService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.service.utils.DocumentManagementUtils;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import com.wakedata.dw.open.service.utils.JsonUtils;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.service.vo.DocumentApiConditionVo;
import com.wakedata.dw.open.service.vo.DocumentApiDetailVo;
import com.wakedata.dw.open.service.vo.DocumentDataAssetApiVo;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;
import com.wakedata.dw.open.service.vo.event.TopicVo;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.jsqlparser.SqlDynamicParameterParserUtil;
import com.wakedata.dw.open.utils.jsqlparser.SqlParameter;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.openapi.service.EventReceiveAttrService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.CronExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wakedata.dw.open.enums.DataAssetPublishStatusEnum.PUBLISH;
import static com.wakedata.dw.open.enums.DataAssetPublishStatusEnum.UN_PUBLISH;

/**
 * @author yiyufeng
 * @title DataAssetApiServiceImpl
 * @projectName bdp-open
 * @date
 * @description
 */
@Data
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DataAssetApiServiceImpl extends BaseServiceImpl<DataAssetApiPo, DataAssetApiMapper> implements DataAssetApiService {
    Pattern pattern = Pattern.compile("\\w{1,127}");

    Pattern PATTERN_SQL = Pattern.compile("\\$\\{\\S+}");

    /**
     * cron 标准格式
     */
    private static final String[] CRON_FORMAT = {"*", "*", "*", "*", "*", "?", "*"};

    @Resource
    private DataAssetApiMapper dataAssetApiMapper;
    @Resource
    private EventReceiveApiMapper eventReceiveApiMapper;
    @Resource
    private ApiConditionMapper apiConditionMapper;
    @Resource
    private AppApprovalMapper appApprovalMapper;
    @Resource
    private ApiResponseParamMapper apiResponseParamMapper;

    @Resource
    private RespParamMappingRuleMapper respParamMappingRuleMapper;
    @Autowired
    private HttpExternalApiAttrService httpExternalApiAttrService;

    @Autowired
    private RedisLockConfigAttrService redisLockConfigAttrService;

    @Autowired
    private ApiFlowAttrService apiFlowAttrService;

    @Autowired
    private AppAccessRuleService appAccessRuleService;
    @Resource
    private DataSourceMapper dataSourceMapper;
    @Autowired
    private DataAssetApiRuleService dataAssetApiRuleService;
    @Autowired
    private DataAssetApiWarnService dataAssetApiWarnService;
    @Autowired
    private ApiGroupService apiGroupService;

    @Autowired
    private TopicService topicService;
    @Autowired
    private BatchCurdService<DataAssetApiPo> dataAssetApiService;
    @Autowired
    private BatchCurdService<ApiConditionPo> apiParamService;
    @Resource
    private ApiResponseParamService apiResponseParamService;
    @Resource
    private CurdService<ApiResponseParamPo> apiResponseParamCurdService;
    @Resource
    private ApiFlowRelationMapper apiFlowRelationMapper;

    @Resource
    private SwaggerInfoService swaggerInfoService;

    @Autowired
    private EventSendApiAttrService eventSendApiAttrService;

    @Autowired
    private EventReceiveApiAttrService eventReceiveApiAttrService;

    @Autowired
    private SubscribeRecordService subscribeRecordService;

    @Autowired
    private AppApprovalService appApprovalService;

    @Resource
    private ApiGroupHelper apiGroupHelper;

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Resource
    private EventSendApiMapper eventSendApiMapper;

    @Resource
    private EventReceiveAttrService eventReceiveAttrService;

    @Resource
    private XxlJobTaskHelper xxlJobTaskHelper;

    @Resource
    private OpenApiAuthHandler openApiAuthHandler;

    @Resource
    private DataAssetApiLiteflowService dataAssetApiLiteflowService;

    private final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy-MM-dd HH");

    @Autowired
    @Override
    protected void init(CurdService<DataAssetApiPo> curdService, DataAssetApiMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Boolean addApi(DataAssetApiPo dataAssetApi) {
        DataAssetApiPo checkPo = new DataAssetApiPo();
        checkPo.setApiName(dataAssetApi.getApiName());
        AuthUtils.setAppId(checkPo);
        List<DataAssetApiPo> checkList = getMapper().select(checkPo);
        if (CollectionUtils.isNotEmpty(checkList)) {
            throw new OpenException(MsgCodeEnum.w_api_name_exists);
        }
        checkPo = new DataAssetApiPo();
        checkPo.setDataAssetApiMethod(dataAssetApi.getDataAssetApiMethod());
        String dataAssetApiMethod = dataAssetApi.getDataAssetApiMethod();
        String[] split = dataAssetApiMethod.split("/");
        if (split.length < 2) {
            throw new OpenException(MsgCodeEnum.w_wrong_api_path_rule);
        }
        Matcher matcher = pattern.matcher(split[1]);
        if (!matcher.find()) {
            throw new OpenException(MsgCodeEnum.w_wrong_api_path_rule);
        }

        checkList = getMapper().select(checkPo);
        if (CollectionUtils.isNotEmpty(checkList)) {
            throw new OpenException(MsgCodeEnum.w_api_path_exists);
        }
        this.add(dataAssetApi);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addApi(ApiDetailVo apiDetailVo) {
//        // 转换cron表达式 (待删除)
//        setCronExpression(apiDetailVo);
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        DataApiType apiType = dataAssetApi.getApiType();
        if (dataAssetApi.getApiName() != null && dataAssetApi.getApiName().length() > 256) {
            throw new OpenException(MsgCodeEnum.w_beyond_the_max_api_name_length);
        }
        if (dataAssetApi.getDataAssetApiMethod() != null && dataAssetApi.getDataAssetApiMethod().length() > 256) {
            throw new OpenException(MsgCodeEnum.w_beyond_the_max_api_path_length);
        }
        if (dataAssetApi.getApiDescription() != null && dataAssetApi.getApiDescription().length() > 256) {
            throw new OpenException(MsgCodeEnum.w_beyond_the_max_api_description_length);
        }
        if (apiType.equals(DataAssetEnums.DataApiType.CUSTOM_SQL) && dataAssetApi.getApiSql().length() > 10000) {
            throw new OpenException(MsgCodeEnum.w_api_custom_sql_max_length);
        }
        //判断API Path路径是否超过了最大限制级数，如果超出限制抛出异常
        if (!DataApiType.EVENT_SEND.equals(apiType)) {
            this.checkApiPathSlashSplitCount(dataAssetApi.getDataAssetApiMethod());
        }
        //根据API的不同类型做不同的参数校验
        this.checkParamForApiType(apiType, dataAssetApi);
        dataAssetApi.setCreateTime(new Date());
        dataAssetApi.setDataAssetPublishStatus(UN_PUBLISH);
        dataAssetApi.setUpdateTime(dataAssetApi.getCreateTime());
        AuthUtils.setAppId(dataAssetApi);
        // sql类型和数据表类型api执行过程中最后需要通过这个这个字段判断，决定使用pageResultDTO还是ResultDTO包装
        if (dataAssetApi.getOperationType() == null) {
            dataAssetApi.setOperationType(DataAssetEnums.DataApiOperationType.QUERY);
        }

        if (Objects.nonNull(dataAssetApi.getApiTagInfo())) {
            this.getMapper().addOrUpdateApi(dataAssetApi);
        } else {
            addApi(dataAssetApi);
        }

        Integer dataAssetApiId = dataAssetApi.getDataAssetApiId();
        AbstractApiAttr abstractApiAttr = dataAssetApi.getApiAttr();
        switch (apiType) {
            // HTTP模式和WebService模式可共用逻辑
            case EXTERNAL_HTTP:
            case WEB_SERVICE:
                httpExternalApiAttrService.saveOrUpdateApiAttr(dataAssetApiId, (HttpExternalApiAttr) abstractApiAttr);
                break;
            case LITE_FLOW:
                ApiFlowAttr apiAttr = (ApiFlowAttr) abstractApiAttr;
                apiAttr.setApiId(dataAssetApiId);
                dataAssetApiLiteflowService.updateInternalApiOperatorIfNecessary(apiDetailVo.getSubscribeRecord(), apiAttr, apiDetailVo.getDataAssetApi().getInCharge());
                apiFlowAttrService.saveOrUpdateApiAttr(apiDetailVo.getDataAssetApi().getDataAssetApiId(), apiAttr);
                break;
            case EVENT_SEND:
                eventSendApiAttrService.saveOrUpdateApiAttr(dataAssetApiId, (EventSendApiAttr) abstractApiAttr, dataAssetApi.getLesseeId(), apiDetailVo.getOperatorName());
                break;
            case EVENT_RECEIVE:
                //api保存成功以后，如果有接收事件算了，则保存订阅记录
                SubscribeRecordVo subscribeRecordVo = apiDetailVo.getSubscribeRecord();
                if (subscribeRecordVo != null && subscribeRecordVo.getEventId() > 0) {
                    // 关联api信息
                    subscribeRecordVo.setHttpId(dataAssetApiId);
                    subscribeRecordService.add(subscribeRecordVo);
                }
                eventReceiveApiAttrService.saveOrUpdateApiAttr(dataAssetApiId, (EventReceiveApiAttr) abstractApiAttr);
                break;
            case CUSTOM_SQL:
            case NORMAL_TABLE:
                RedisLockConfigAttr redisLockConfigAttr = (RedisLockConfigAttr) abstractApiAttr;
                if (redisLockConfigAttr == null || ActiveStateEnum.INVALID.getValue().equals(redisLockConfigAttr.getEnableRedisLock())) {
                    break;
                }
                checkRedisConfig(redisLockConfigAttr);
                redisLockConfigAttrService.saveOrUpdateApiAttr(dataAssetApiId, redisLockConfigAttr);
                break;
            default:
                break;
        }
        apiDetailVo.setDataAssetApi(dataAssetApi);
        return publishDataAssetApi(apiDetailVo);
    }

    @Override
    public void checkRedisConfig(RedisLockConfigAttr attr) {
        if (attr.getLockType() == null) {
            throw new OpenException(MsgCodeEnum.w_redis_lock_type_not_null);
        }
        if (CollectionUtil.isEmpty(attr.getKeyParams())) {
            throw new OpenException(MsgCodeEnum.w_redis_lock_key_params_not_empty);
        }
        long zeroSeconds = 0L;
        long sixtySeconds = 60L;
        Long waitTime = attr.getWaitTime();
        Long leaseTime = attr.getLeaseTime();
        if (waitTime == null || waitTime < zeroSeconds || waitTime > sixtySeconds) {
            throw new OpenException(MsgCodeEnum.w_redis_lock_wait_time_is_invalid);
        }
        if (leaseTime == null || leaseTime < zeroSeconds || leaseTime > sixtySeconds) {
            throw new OpenException(MsgCodeEnum.w_redis_lock_lease_time_is_invalid);
        }
    }

//    /**
//     * 填充 cron 表达式到 apiDetailVo (待删除)
//     *
//     * @param apiDetailVo api信息
//     */
//    private void setCronExpression(ApiDetailVo apiDetailVo) {
//        AbstractApiAttr apiAttr = apiDetailVo.getDataAssetApi().getApiAttr();
//        if (Objects.isNull(apiAttr) || !(apiAttr instanceof ApiFlowAttr)) {
//            return;
//        }
//        ApiFlowAttr attr = (ApiFlowAttr) apiAttr;
//        Map<String, Operator> operators = attr.getOperators();
//        for (Entry<String, Operator> entry : operators.entrySet()) {
//            Operator operator = entry.getValue();
//            if (operator instanceof CrontabOperator) {
//                CrontabOperator crontabOperator = (CrontabOperator) operator;
//                String cronExpression = buildCronExpression(crontabOperator.getCycleValue(),
//                        crontabOperator.getCycleUnit());
//                crontabOperator.setCronExpression(cronExpression);
//            }
//        }
//    }

    /**
     * 构建 cron 表达式
     *
     * @param cycleValue 每 cycleValue 执行一次
     * @param cycleUnit  执行单位
     * @return cron表达式
     */
    private String buildCronExpression(Integer cycleValue, Integer cycleUnit) {
        if (Objects.isNull(cycleValue) || Objects.isNull(cycleUnit)) {
            throw new IllegalArgumentException("执行周期参数错误！");
        }
        String[] clone = CRON_FORMAT.clone();
        clone[cycleUnit] = clone[cycleUnit] + "/" + cycleValue;
        String cronExpression = StringUtils.join(clone, " ");
        if (CronExpression.isValidExpression(cronExpression)) {
            return cronExpression;
        }
        throw new OpenException(MsgCodeEnum.w_wrong_argument);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateApi(ApiDetailVo apiDetailVo) {
//        // 转换cron表达式 (待删除)
//        setCronExpression(apiDetailVo);
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        Integer dataAssetApiId = dataAssetApi.getDataAssetApiId();
        DataAssetApiPo dataAssetApi1 = getCurdService().selectByPrimaryKey(dataAssetApiId, getMapper());
        if (!DataApiType.EVENT_SEND.equals(apiDetailVo.getDataAssetApi().getApiType()) && !DataApiType.EVENT_RECEIVE.equals(apiDetailVo.getDataAssetApi().getApiType())) {
            //校验是否存在该api
            if (ObjectUtil.isEmpty(dataAssetApi1)) {
                throw new OpenException(MsgCodeEnum.w_not_exists_api);
            }
            //校验api地址（apiPath）是否重复
            if (!dataAssetApi1.getDataAssetApiMethod().equals(dataAssetApi.getDataAssetApiMethod())) {
                checkApiMethodExists(dataAssetApi);
            }
            //判断API Path路径是否超过了最大限制级数，如果超出限制抛出异常
            this.checkApiPathSlashSplitCount(dataAssetApi.getDataAssetApiMethod());
            //根据API的不同类型做不同的参数校验
            this.checkParamForApiType(dataAssetApi.getApiType(), dataAssetApi);

            //如果api已经发布且api不是事件接收算子、事件发送算子，则不能修改api
            boolean publishFlag =
                    DataAssetPublishStatusEnum.PUBLISH.equals(dataAssetApi1.getDataAssetPublishStatus()) &&
                            !DataApiType.EVENT_RECEIVE.equals(dataAssetApi1.getApiType()) &&
                            !DataApiType.EVENT_SEND.equals(dataAssetApi1.getApiType());

            if (publishFlag) {
                throw new OpenException(MsgCodeEnum.w_cannot_delete_published_api);
            }
            //检验修改后的api名称时候有重复
            DataAssetApiPo checkPo = new DataAssetApiPo();
            checkPo.setApiName(dataAssetApi.getApiName());
            AuthUtils.setAppId(checkPo);
            List<DataAssetApiPo> checkList = getMapper().select(checkPo);
            if (CollectionUtils.isNotEmpty(checkList)) {
                for (DataAssetApiPo dataAssetApiPo : checkList) {
                    String apiName = dataAssetApiPo.getApiName();
                    if (apiName.equals(checkPo.getApiName()) &&
                            !dataAssetApiPo.getDataAssetApiId().equals(dataAssetApiId)) {
                        throw new OpenException(MsgCodeEnum.w_api_name_exists);
                    }
                }
            }
            //检验接口路径是否有重复
            checkPo = new DataAssetApiPo();
            checkPo.setDataAssetApiMethod(dataAssetApi.getDataAssetApiMethod());
            checkList = getMapper().select(checkPo);
            if (CollectionUtils.isNotEmpty(checkList)) {
                for (DataAssetApiPo dataAssetApiPo : checkList) {
                    if (dataAssetApiPo.getDataAssetApiMethod().equals(checkPo.getDataAssetApiMethod()) &&
                            !dataAssetApiPo.getDataAssetApiId().equals(dataAssetApiId)) {
                        throw new OpenException(MsgCodeEnum.w_api_path_exists);
                    }
                }
            }
            //api保存成功以后，如果有接收事件算了，则修改订阅记录
            SubscribeRecordVo subscribeRecordVo = apiDetailVo.getSubscribeRecord();
            if (subscribeRecordVo != null && subscribeRecordVo.getEventId() > 0) {
                subscribeRecordService.updateById(subscribeRecordVo);
            }
            //写入DW_OPEN_API主表
            int i = getCurdService().updateByPrimaryKeySelective(dataAssetApi, getMapper());
        }
        AbstractApiAttr abstractApiAttr = apiDetailVo.getDataAssetApi().getApiAttr();
        switch (dataAssetApi.getApiType()) {
            // HTTP模式和WebService模式可共用逻辑
            case EXTERNAL_HTTP:
            case WEB_SERVICE:
                httpExternalApiAttrService.saveOrUpdateApiAttr(dataAssetApiId, (HttpExternalApiAttr) abstractApiAttr);
                break;
            case LITE_FLOW:
                ApiFlowAttr apiAttr = (ApiFlowAttr) abstractApiAttr;
                dataAssetApiLiteflowService.updateInternalApiOperatorIfNecessary(apiDetailVo.getSubscribeRecord(), apiAttr, dataAssetApi.getInCharge());
                //保存这个流程编排的关联关系等信息
                apiFlowAttrService.saveOrUpdateApiAttr(dataAssetApiId, apiAttr);
                break;
            case EVENT_SEND:
                eventSendApiAttrService.saveOrUpdateApiAttr(dataAssetApiId, (EventSendApiAttr) abstractApiAttr, IpaasUserContext.getUserInfo().getLesseeId(), apiDetailVo.getOperatorName());
                break;
            case EVENT_RECEIVE:
                eventReceiveApiAttrService.saveOrUpdateApiAttr(dataAssetApiId, (EventReceiveApiAttr) abstractApiAttr, IpaasUserContext.getUserInfo().getLesseeId(), apiDetailVo.getOperatorName());
                break;
            case CUSTOM_SQL:
            case NORMAL_TABLE:
                RedisLockConfigAttr redisLockConfigAttr = (RedisLockConfigAttr) abstractApiAttr;
                if (redisLockConfigAttr == null) {
                    break;
                }
                if (ActiveStateEnum.ACTIVE.getValue().equals(redisLockConfigAttr.getEnableRedisLock())) {
                    checkRedisConfig(redisLockConfigAttr);
                }
                redisLockConfigAttrService.saveOrUpdateApiAttr(dataAssetApiId, redisLockConfigAttr);
                break;
            default:
                break;
        }
        //清除缓存
        removeDataAssetApi(dataAssetApiId);

        return publishDataAssetApi(apiDetailVo);
    }

    /**
     * 清除缓存
     *
     * @param dataAssetApiId
     */
    public void removeDataAssetApi(Integer dataAssetApiId) {
        openApiDataCache.removeApiConditions(dataAssetApiId);
        openApiDataCache.removeHttpExternalApiAttr(dataAssetApiId);
        openApiDataCache.removeApiLiteFlowParams(dataAssetApiId);
        openApiDataCache.removeApiResponseParams(dataAssetApiId);
        openApiDataCache.removeApiFlowAttr(dataAssetApiId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean publishDataAssetApi(ApiDetailVo apiDetailVo) {
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        dataAssetApi.setUpdateTime(new Date());
        this.getCurdService().updateByPrimaryKeySelective(dataAssetApi, this.dataAssetApiMapper);

        // 清理api参数，根据ApiId清理请求参数和响应参数
        cleanApiParams(dataAssetApi);

        saveRequestParams(apiDetailVo);
        saveResponseParams(apiDetailVo);
        saveFilterParams(apiDetailVo);
        // 保存API响应参数
        saveResponses(apiDetailVo);
        // liteFlow类型需要保存结果参数
        if (dataAssetApi.getApiType().equals(DataApiType.LITE_FLOW)) {
            // 保存liteFlow结果响应参数
            saveLiteFlowResult(apiDetailVo);
        }
        return true;
    }

    /**
     * 保存流程编排结果模板
     *
     * @param apiDetailVo 流程编排相关的参数信息和响应等信息
     */
    public void saveLiteFlowResult(ApiDetailVo apiDetailVo) {

        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        if (Objects.isNull(dataAssetApi)) {
            throw new OpenException("ApiDetailVo 参数不能为空");
        }

        Integer dataAssetApiId = dataAssetApi.getDataAssetApiId();
        if (null == dataAssetApiId) {
            throw new OpenException("dataAssetApiId 参数不能为空");
        }

        List<ApiRespParamDTO> resultRespParamDTOS = apiDetailVo.getResutRespParamDTOS();
        if (CollectionUtil.isEmpty(resultRespParamDTOS)) {
            return;
        }

        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        resultRespParamDTOS.forEach(apiRespParamDTO -> {
            saveResultResponseNode(apiRespParamDTO, DwOpenConstant.RESPONSE_PARENT_ID, dataAssetApiId, ipaasUserInfo);
            String content = "";
            if(HttpParamKind.BODY == apiRespParamDTO.getType()){
                content = apiRespParamDTO.getResponsePostData();
            }else{
                content = apiRespParamDTO.getExpression();
            }
            apiFlowAttrService.saveApiRelationCustomFuncName(ipaasUserInfo.getLesseeId(),dataAssetApiId,content);
        });
    }

    /**
     * 递归保存流程编排结果响应参数
     *
     * @param node           当前响应节点参数
     * @param parentId       父节点id
     * @param dataAssetApiId 接口ID
     * @param ipaasUserInfo  用户上下文
     */
    private void saveResultResponseNode(ApiRespParamDTO node, Integer parentId, Integer dataAssetApiId, IpaasUserInfo ipaasUserInfo) {
        if (Objects.isNull(node)) {
            return;
        }
        ApiResponseParamPo apiResponseParamPo = com.wakedata.common.core.util.BeanUtil.copy(node, ApiResponseParamPo.class);
        apiResponseParamPo.setDataAssetId(dataAssetApiId);
        apiResponseParamPo.setParentId(parentId);
        apiResponseParamPo.setLesseeId(ipaasUserInfo.getLesseeId());
        apiResponseParamPo.setCreateBy(ipaasUserInfo.getName());
        apiResponseParamPo.setUpdateBy(ipaasUserInfo.getName());
        apiResponseParamPo.setCreateTime(new Date());
        apiResponseParamPo.setUpdateTime(new Date());
        apiResponseParamPo.setResponded(0);
        apiResponseParamPo.setId(null);
        apiResponseParamPo.setBusinessType(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        if (StringUtils.isBlank(apiResponseParamPo.getParamValueType())) {
            apiResponseParamPo.setParamValueType(ParamMappingTypeEnum.REFERENCE_TYPE.getType());
        }
        apiResponseParamCurdService.insert(apiResponseParamPo, apiResponseParamMapper);

        // todo 前端传参不传树状结构是不会走下面代码 联调完评估下是否需要去除该代码
        List<ApiRespParamDTO> childApiRespParams = node.getChildApiRespParams();
        if (CollectionUtils.isEmpty(childApiRespParams)) {
            return;
        }
        for (ApiRespParamDTO child : childApiRespParams) {
            saveResultResponseNode(child, apiResponseParamPo.getId(), dataAssetApiId, ipaasUserInfo);
        }
    }

    /**
     * 保存API接口响应参数模板
     *
     * @param apiDetailVo API资产对象
     */
    private void saveResponses(ApiDetailVo apiDetailVo) {
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        Integer dataAssetApiId = dataAssetApi.getDataAssetApiId();
        Long lesseeId = AuthUtils.currentAppId();
        String userName = AuthUtils.getCurrentUserInfo().getAccount();
        List<ApiRespParamDTO> responseParams = null;

        // 根据api类型区分响应参数来源
        switch (dataAssetApi.getApiType()) {
            case WEB_SERVICE:
            case EXTERNAL_HTTP:
            case CUSTOM_SQL:
            case NORMAL_TABLE:
                // 手动输入
                responseParams = apiDetailVo.getResponseParams();
                break;
            default:
                break;
        }
        if (CollectionUtils.isEmpty(responseParams)) {
            return;
        }
        // 入库
        responseParams.forEach(paramDTO -> saveResponseNode(dataAssetApiId, DwOpenConstant.RESPONSE_PARENT_ID, lesseeId, userName, paramDTO));
    }

    /**
     * 递归插入当前api返回参数节点
     *
     * @param dataAssetApiId api资产ID
     * @param parentId       父节点id
     * @param lesseeId       租户id
     * @param userName       用户名
     * @param node           需要插入的响应参数节点
     */
    private void saveResponseNode(Integer dataAssetApiId, Integer parentId, Long lesseeId, String userName,
                                  ApiRespParamDTO node) {

        // 递归出口
        if (Objects.isNull(node)) {
            return;
        }
        //移除all
        if (HttpExternalApiAttr.ALL_FIELD.equals(node.getAssetColumns())) {
            return;
        }
        // 映射为po
        ApiResponseParamPo paramPo = ApiResponseParamPo.builder()
                .assetColumns(node.getAssetColumns())
                .assetColumnLength(node.getAssetColumnLength())
                .assetDataType(node.getAssetDataType())
                .description(node.getDescription())
                .type(node.getType())
                .parentId(parentId)
                .responded(0)
                .businessType(DataAssetEnums.ApiResponseBusinessType.HTTP_RESULT)
                .responsePostData(node.getResponsePostData())
                .isSchema(node.getIsSchema())
                .defaultValue(node.getDefaultValue())
                .build();
        paramPo.setDataAssetId(dataAssetApiId);
        paramPo.setLesseeId(lesseeId);
        paramPo.setCreateBy(userName);
        paramPo.setUpdateBy(userName);

        // 入库
        apiResponseParamCurdService.insert(paramPo, apiResponseParamMapper);
        // todo 前端传参不传树状结构是不会走下面代码 联调完评估下是否需要去除该代码
        List<ApiRespParamDTO> childApiRespParams = node.getChildApiRespParams();
        if (CollectionUtils.isEmpty(childApiRespParams)) {
            return;
        }
        for (ApiRespParamDTO child : childApiRespParams) {
            saveResponseNode(dataAssetApiId, paramPo.getId(), lesseeId, userName, child);
        }
    }

    /**
     * 将result列表映射为ApiRespParamDTO列表
     *
     * @param apiResults result列表
     * @return ApiRespParamDTO列表
     */
    private List<ApiRespParamDTO> resultsToRespParamDTO(List<ApiConditionPo> apiResults) {
        List<ApiRespParamDTO> responses = new ArrayList<>();
        apiResults.forEach(apiResult -> {
            ApiRespParamDTO responseParamPo = new ApiRespParamDTO();
            responseParamPo.setId(apiResult.getId());
            responseParamPo.setAssetColumns(apiResult.getAssetColumns());
            responseParamPo.setAssetDataType(apiResult.getAssetDatatype());
            responseParamPo.setAssetColumnLength(apiResult.getAssetColumnsLength());
            responseParamPo.setDescription(apiResult.getDescriptions());
            responseParamPo.setType(HttpParamKind.BODY);
            responses.add(responseParamPo);
        });
        return responses;
    }

    private void saveResponseParams(ApiDetailVo apiDetailVo) {
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        List<ApiConditionPo> resultParams = assembleApiParams(
                apiDetailVo.getResults(),
                DataAssetEnums.FiledTypeEnums.RESULT,
                dataAssetApi.getDataAssetApiId(),
                apiDetailVo.getDataAssetApi().getApiType()
        );

        switch (dataAssetApi.getApiType()) {
            case EXTERNAL_HTTP:
            case WEB_SERVICE:
            case EVENT_SEND:
            case EVENT_RECEIVE:
                // TODO 待业务清晰后再处理
                // 加入ALL字段
                ApiConditionPo apiParam = buildAllFieldCondition(dataAssetApi);
                if (!resultParams.contains(apiParam)) {
                    resultParams.add(apiParam);
                    apiDetailVo.getResults().add(apiParam);
                }
                break;
            case LITE_FLOW:
                apiDetailVo.getResults().clear();
                List<ApiConditionPo> sinkResultParams = dataAssetApiLiteflowService.getApiSinkOperatorsResultParams((ApiFlowAttr) apiDetailVo.getDataAssetApi().getApiAttr(),dataAssetApi.getDataAssetApiId());
                if(CollUtil.isNotEmpty(sinkResultParams)){
                    for(ApiConditionPo conditionPo : sinkResultParams) {
                        if (!resultParams.contains(conditionPo)) {
                            resultParams.add(conditionPo);
                            apiDetailVo.getResults().add(conditionPo);
                        }
                    }
                }
                break;
            case NORMAL_TABLE:
                if (dataAssetApi.getOperationType() != DataAssetEnums.DataApiOperationType.QUERY) {
                    // 加入ALL字段
                    apiParam = buildAllFieldCondition(dataAssetApi);
                    if (!resultParams.contains(apiParam)) {
                        resultParams.add(apiParam);
                        apiDetailVo.getResults().add(apiParam);
                    }
                }
                break;
            case CUSTOM_SQL:
                if (CollectionUtils.isEmpty(resultParams)) {
                    apiParam = buildAllFieldCondition(dataAssetApi);
                    resultParams.add(apiParam);
                    apiDetailVo.getResults().add(apiParam);
                }
                break;
            default:
                break;
        }
        if (CollectionUtils.isEmpty(resultParams)) {
            return;
        }

        apiParamService.insertList(resultParams, apiConditionMapper);
    }

    private void saveRequestParams(ApiDetailVo apiDetailVo) {
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();

        List<ApiConditionPo> params = Lists.newArrayList();
        if (dataAssetApi.getApiType() != DataAssetEnums.DataApiType.LITE_FLOW) {
            // 非画布任务 来源于页面自动解析
            List<ApiConditionPo> requestParams = assembleApiParams(
                    apiDetailVo.getParameters(),
                    DataAssetEnums.FiledTypeEnums.PARAMETERS,
                    dataAssetApi.getDataAssetApiId(),
                    apiDetailVo.getDataAssetApi().getApiType()
            );
            params.addAll(requestParams);
        } else {
            params = parseOperatorParam(apiDetailVo, true);
        }
        apiParamService.insertList(params, apiConditionMapper);
    }

    private void saveFilterParams(ApiDetailVo apiDetailVo) {
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        Integer dataSourceId = dataAssetApi.getDataSourceId();
        //插入新的过滤条件
        List<ApiConditionPo> params = Lists.newArrayList();
        Date date = new Date();
        for (ApiConditionPo parameter : apiDetailVo.getParameters()) {
            if (parameter.getTypeAttr() == DataAssetEnums.FiledTypeAttrEnums.FILTER) {
                parameter.setDataAssetId(apiDetailVo.getDataAssetApi().getDataAssetApiId());
                parameter.setCreateTime(date);
                parameter.setUpdateTime(date);
                parameter.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);  //过滤条件是参数的一种
                if (Objects.isNull(parameter.getHttpParamKind())) {
                    parameter.setHttpParamKind(HttpParamKind.FILTER);
                }
                if (Objects.isNull(parameter.getRequired())) {
                    parameter.setRequired(false);
                }
                params.add(parameter);
            }
        }
        apiParamService.insertList(params, apiConditionMapper);
    }

    /**
     * 构建__ALL__参数的ApiCondition实体类
     *
     * @param dataAssetApi API对象
     * @return ApiConditionPo
     */
    private ApiConditionPo buildAllFieldCondition(DataAssetApiPo dataAssetApi) {
        return ApiConditionPo.build(
                dataAssetApi.getDataAssetApiId(),
                HttpExternalApiAttr.ALL_FIELD,
                "所有信息",
                null,
                null,
                DataAssetEnums.FiledTypeEnums.RESULT,
                null,
                false,
                false
        );
    }

    private List<ApiConditionPo> parseOperatorParam(ApiDetailVo apiDetailVo, boolean isSave) {
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        if (dataAssetApi.getApiType() != DataAssetEnums.DataApiType.LITE_FLOW) {
            return apiDetailVo.getParameters();
        }

        //保存的时候清空前端传来不是自定义请求数据
        if (isSave && Objects.nonNull(apiDetailVo.getParameters())) {
            apiDetailVo.getParameters().removeIf(ApiConditionPo::isAutoPare);
        }

        List<OperatorParam> operatorParams = Lists.newArrayList();

        // 外部API为空则直接返回
        AbstractApiAttr apiAttr = apiDetailVo.getDataAssetApi().getApiAttr();
        if (null == apiAttr) {
            return apiDetailVo.getParameters();
        }

        List<ApiConditionPo> autParses = operatorParams.stream().map(operatorParam -> operatorParam.param).collect(Collectors.toList());
        // 最后处理自定义的请求参数
        if (CollectionUtil.isNotEmpty(apiDetailVo.getParameters())) {
            apiDetailVo.getParameters().removeAll(autParses);
            for (ApiConditionPo parameter : apiDetailVo.getParameters()) {
                if (Objects.isNull(parameter.getHttpParamKind())) {
                    parameter.setHttpParamKind(HttpParamKind.QUERY);
                }

                if (Objects.isNull(parameter.getType())) {
                    parameter.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
                }

                if (Objects.isNull(parameter.getRequired())) {
                    parameter.setRequired(Boolean.FALSE);
                }

                parameter.setDataAssetId(dataAssetApi.getDataAssetApiId());
                parameter.setAutoPare(Boolean.FALSE);
                operatorParams.add(new OperatorParam(parameter));
            }
        }
        return operatorParams.stream().map(operatorParam -> operatorParam.param).collect(Collectors.toList());
    }

    private class OperatorParam {
        Operator operator;
        ApiConditionPo param;

        public OperatorParam(ApiConditionPo param) {
            this.param = param;
        }

        public OperatorParam(Operator operator, ApiConditionPo param) {
            this.operator = operator;
            this.param = param;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OperatorParam that = (OperatorParam) o;
            return Objects.equals(param, that.param);
        }

        @Override
        public int hashCode() {
            return Objects.hash(param);
        }

        @Override
        public String toString() {
            if (Objects.isNull(operator)) {
                return param.toString();
            }
            return operator.getName() + " " + param.toString();
        }
    }

    private void cleanApiParams(DataAssetApiPo dataAssetApi) {
        // 查询请求参数，并清理
        Integer dataAssetApiId = dataAssetApi.getDataAssetApiId();
        if (dataAssetApiId == null) {
            log.error("dataAssetApiId is empty, dataAssetApi={}", JSONUtil.toJsonStr(dataAssetApi));
            throw new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        ApiConditionPo apiParam = new ApiConditionPo();
        apiParam.setDataAssetId(dataAssetApiId);
        apiConditionMapper.delete(apiParam);
        appAccessRuleService.delete(dataAssetApiId);

        // 清理API响应参数表
        ApiResponseParamPo apiResponseParam = new ApiResponseParamPo();
        apiResponseParam.setDataAssetId(dataAssetApiId);
        apiResponseParamMapper.delete(apiResponseParam);
    }

    @Override
    @Transactional
    public Boolean publishDataAssetApi(String username, Integer dataAssetApiId) {
        // 服务编排类型：需判断关联API算子是否已发布
        ApiDetailVo apiDetail = detailVo(dataAssetApiId, null);
        DataAssetApiPo dataAssetApi = apiDetail.getDataAssetApi();
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setDataAssetApiId(dataAssetApiId);
        dataAssetApiPo.setDataAssetPublishStatus(PUBLISH);
        dataAssetApiPo.setPublisher(username);
        dataAssetApiPo.setUpdateTime(new Date());
        dataAssetApiPo.setPublishTime(new Date());
        int i = getCurdService().updateByPrimaryKeySelective(dataAssetApiPo, getMapper());

        if (!dataAssetApi.getApiType().equals(DataAssetEnums.DataApiType.LITE_FLOW)) {
            return true;
        }
        ApiFlowAttr apiFlowAttr = (ApiFlowAttr) dataAssetApi.getApiAttr();
        List<Integer> apiIds = dataAssetApiLiteflowService.getApiLiteflowRelationApiAndHandle(apiFlowAttr);
        if (CollectionUtils.isEmpty(apiIds)) {
            return true;
        }
        List<DataAssetApiPo> unpublishs = selectByIdList(apiIds)
                .stream()
                .filter(streamObj -> streamObj.getDataAssetPublishStatus() == UN_PUBLISH)
                .collect(Collectors.toList());

        for (DataAssetApiPo unpublish : unpublishs) {
            publishDataAssetApi(username, unpublish.getDataAssetApiId());
        }
        return true;
    }

    /**
     * 根据前端入参的ApiConditionPo集合组装参数生成新的ApiConditionPo集合
     *
     * @param list           原ApiConditionPo
     * @param fieldType      参数类型枚举类
     * @param dataAssetApiId API ID
     * @param apiType        API类型枚举类
     * @return 组装参数后的ApiConditionPo集合
     */
    private List<ApiConditionPo> assembleApiParams(List<ApiConditionPo> list, DataAssetEnums.FiledTypeEnums fieldType
            , Integer dataAssetApiId, DataAssetEnums.DataApiType apiType) {
        List<ApiConditionPo> result = new ArrayList<>();
        Date date = new Date();
        boolean hasWsDefaultCondition = false;
        for (ApiConditionPo apiConditionPo : list) {
            if (ApiConditionDefaultColumnEnum.ACCESS_WSDL.getAssetColumns().equals(apiConditionPo.getAssetColumns())) {
                hasWsDefaultCondition = true;
            }
            apiConditionPo.setDataAssetId(dataAssetApiId);
            apiConditionPo.setCreateTime(date);
            apiConditionPo.setUpdateTime(date);
            apiConditionPo.setType(fieldType);
            if (Objects.isNull(apiConditionPo.getHttpParamKind())) {
                apiConditionPo.setHttpParamKind(HttpParamKind.QUERY);
            }

            if (Objects.isNull(apiConditionPo.getRequired())) {
                apiConditionPo.setRequired(false);
            }

            // 如果是数据表格式，并且参数类型为FILTER，需要补充前缀区分参数
            if (DataApiType.NORMAL_TABLE == apiType && HttpParamKind.FILTER == apiConditionPo.getHttpParamKind()) {
                String prefixColumn = DwOpenConstant.FILTER_PREFIX + apiConditionPo.getAssetColumns();
                apiConditionPo.setAssetColumns(prefixColumn);
            }
            if (DataAssetEnums.FiledTypeEnums.PARAMETERS == fieldType && apiConditionPo.getTypeAttr() == DataAssetEnums.FiledTypeAttrEnums.OPERATOR) {
                result.add(apiConditionPo);
            } else if (DataAssetEnums.FiledTypeEnums.RESULT == fieldType) {
                result.add(apiConditionPo);
            }
        }
        // 如果是WebService类型，如果前端没传的accessWsdl参数的情况下，需要增加一个默认参数
        if (DataApiType.WEB_SERVICE == apiType && DataAssetEnums.FiledTypeEnums.PARAMETERS == fieldType && !hasWsDefaultCondition) {
            result.add(buildWsDefaultCondition(dataAssetApiId, date));
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean unPublishDataAssetApi(Integer dataAssetApiId) {
        DataAssetApiPo dataAssetApi = getCurdService().selectByPrimaryKey(dataAssetApiId, dataAssetApiMapper);
        List<DataAssetApiPo> dataAssetApis = getRelationApis(dataAssetApiId);
        if (CollectionUtils.isNotEmpty(dataAssetApis)) {
            throw new OpenException("API["+dataAssetApi.getApiName()+"]关联服务编排API[" + StringUtils.join(dataAssetApis.stream().map(DataAssetApiPo::getApiName).collect(Collectors.toList()), ", ") + "] , 无法下线！");
        }
        //清空不需要的属性
        dataAssetApi.setDataAssetPublishStatus(UN_PUBLISH);
        dataAssetApi.setPublisher(null);
        dataAssetApi.setUpdateTime(new Date());
        this.getCurdService().updateByPrimaryKeySelective(dataAssetApi, this.dataAssetApiMapper);
        deleteSubscribeOfEventReceiveOperator(dataAssetApi);
        //通过api id删除授权字段
        appAccessRuleService.deleteAppAccessRuleOfApiId(dataAssetApiId);
        // API下线 则将审批表的相关api数据置为失效状态
        appApprovalService.updateActiveStatusById(dataAssetApiId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean dataAssetApiBatchPublish(String publisher, List<Integer> dataAssetApiIds) {
        for (Integer dataAssetApiId : dataAssetApiIds) {
            publishDataAssetApi(publisher, dataAssetApiId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean dataAssetApiBatchUnPublish(List<Integer> dataAssetApiIds, DataAssetPublishStatusEnum publishStatus) {
        List<ApiFlowRelation> apiRelations = apiFlowRelationMapper.getApiRelationsByApiIds(dataAssetApiIds);
        //获取已关联流程编排的api的名称
        List<String> apiNameList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(apiRelations)) {
            List<Integer> apiIdList = apiRelations.stream().map(ApiFlowRelation :: getApiId).collect(Collectors.toList());
            List<DataAssetApiPo> dataAssetApiPos= selectByIdList(apiIdList);
            apiNameList = dataAssetApiPos.stream().map(DataAssetApiPo :: getApiName).collect(Collectors.toList());
        }
        //获取关联的流程编排的api
        List<Integer> apiIdList =  Lists.newArrayList(apiRelations.stream().map(ApiFlowRelation::getGraphId).collect(Collectors.toSet()));
        List<DataAssetApiPo> dataAssetApis = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(apiIdList)) {
            dataAssetApis = selectByIdList(apiIdList);
        }
        if (CollectionUtils.isNotEmpty(dataAssetApis)) {
            throw new OpenException("API" + apiNameList +"关联服务编排API[" + StringUtils.join(dataAssetApis.stream().map(DataAssetApiPo::getApiName).collect(Collectors.toList()), ", ") + "] , 无法下线！");
        }
        //服务编排下线接收算子订阅者
        for (DataAssetApiPo dataAssetApi : dataAssetApis) {
            deleteSubscribeOfEventReceiveOperator(dataAssetApi);
        }
        //批量下线API将发布者置空
        dataAssetApiMapper.batchUpdateDataAssetApi(dataAssetApiIds, publishStatus.getValue(), null);
        //删除需要返回字段
        appAccessRuleService.batchDeleteAppAccessRuleOfApiId(dataAssetApiIds);
        // API下线 则将审批表的相关api数据置为失效状态
        appApprovalService.updateActiveStatusByIds(dataAssetApiIds, DataAssetEnums.ActiveStatus.DISABLE);

        //如果当前算子已经发布且api不是分支算子、事件接收算子、事件发送算子，则跳过
        return true;
    }

    /**
     * 服务编排下线订阅者
     *
     * @param dataAssetApi DataAssetApiPo
     */
    private void deleteSubscribeOfEventReceiveOperator(DataAssetApiPo dataAssetApi) {
        if (DataAssetEnums.DataApiType.LITE_FLOW.equals(dataAssetApi.getApiType())) {
            Integer apiId = dataAssetApi.getDataAssetApiId();
            if (apiId == null) {
                return;
            }
            Example example = new Example(EventReceiveApiAttr.class);
            example.createCriteria().andEqualTo("apiId", apiId);
            List<EventReceiveApiAttr> eventReceiveApiAttrs = eventReceiveApiMapper.selectByExample(example);
            if (CollectionUtil.isEmpty(eventReceiveApiAttrs)) {
                return;
            }
            eventReceiveApiAttrs.forEach(eventReceiveApiAttr -> eventReceiveAttrService.deleteSubscribe(eventReceiveApiAttr.getId()));
        }
    }

    private List<DataAssetApiPo> getRelationApis(Integer dataAssetApiId) {
        List<ApiFlowRelation> apiRelations = apiFlowRelationMapper.getApiRelations(dataAssetApiId);
        if (CollectionUtils.isEmpty(apiRelations)) {
            return Lists.newArrayList();
        }

        Set<Integer> apiIds = apiRelations.stream().map(ApiFlowRelation::getGraphId).collect(Collectors.toSet());
        return selectByIdList(Lists.newArrayList(apiIds));
    }

    @Override
    public Boolean appendInCharge(String userIdentification, String appendInChargeEmployeeNumber, Integer dataAssetApiId) {
        // TODO 校验employeeNumber是否有增加负责人权限

        // 查出数据库中的负责人
        DataAssetApiPo dataAssetApi = this.getCurdService().selectByPrimaryKey(dataAssetApiId, this.dataAssetApiMapper);
        // 拼接负责人
        DataAssetApiPo dataAssetApiInsert = new DataAssetApiPo();
        dataAssetApiInsert.setDataAssetApiId(dataAssetApiId);
        if (StringUtils.isEmpty(dataAssetApi.getInCharge())) {
            dataAssetApiInsert.setInCharge(appendInChargeEmployeeNumber);
        } else {
            dataAssetApiInsert.setInCharge(dataAssetApi.getInCharge() + "," + appendInChargeEmployeeNumber);
        }
        this.getCurdService().updateByPrimaryKeySelective(dataAssetApiInsert, this.dataAssetApiMapper);
        return true;
    }

    @Override
    public Boolean removeInCharge(String userIdentification, String removeInChargeEmployeeNumber, Integer dataAssetApiId) {
        // 查出数据库中的负责人
        DataAssetApiPo dataAssetApi = this.getCurdService().selectByPrimaryKey(dataAssetApiId, this.dataAssetApiMapper);
        // 拼接负责人
        DataAssetApiPo dataAssetApiUpdate = new DataAssetApiPo();
        dataAssetApiUpdate.setDataAssetApiId(dataAssetApiId);
        if (StringUtils.isEmpty(dataAssetApi.getInCharge())) {
            return true;
        } else {
            String[] inChargeArray = dataAssetApi.getInCharge().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for (String inCharge : inChargeArray) {
                if (StringUtils.equals(inCharge, removeInChargeEmployeeNumber)) {
                    continue;
                }
                stringBuffer.append(inCharge).append(",");
            }
            dataAssetApiUpdate.setInCharge(stringBuffer.substring(0, stringBuffer.length() - 1));
        }
        this.getCurdService().updateByPrimaryKeySelective(dataAssetApiUpdate, this.dataAssetApiMapper);
        return true;
    }

    private String appendApiUri(String apiMethod) {
        return openApiAuthHandler.getPlatformDefaultApiPrefix() + apiMethod;
    }

    public DataAssetApiPo selectByPrimaryKey(Object dataAssetApiId) {
        DataAssetApiPo dataAssetApi = this.getCurdService().selectByPrimaryKey(dataAssetApiId, this.dataAssetApiMapper);
        if (null == dataAssetApi) {
            return null;
        }
        dataAssetApi.setDataAssetApiUri(this.appendApiUri(dataAssetApi.getDataAssetApiMethod()));
        return dataAssetApi;
    }

    public List<DataAssetApiPo> select(DataAssetApiPo dataAssetApi) {
        return this.getCurdService().select(dataAssetApi, this.dataAssetApiMapper);
    }

    @Override
    public List<DataAssetApiPo> selectByIdList(List<Integer> list) {
        return this.dataAssetApiService.selectByIdList(list, this.dataAssetApiMapper);
    }

    @Override
    public ApiDetailVo detailVo(Integer dataAssetApiId,List<ApiConditionPo> parameters) {
        ApiDetailVo apiDetail = new ApiDetailVo();
        DataAssetApiPo dataAssetApi = detail(dataAssetApiId);
        apiDetail.setDataAssetApi(dataAssetApi);
        ApiConditionPo apiParam = new ApiConditionPo();
        apiParam.setDataAssetId(dataAssetApiId);
        apiParam.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);

        // 填充返回参数列表
        apiDetail.setResponseParams(apiResponseParamService.listByApiId(dataAssetApiId));

        DataApiType apiType = dataAssetApi.getApiType();
        //编排内部api算子不替换参数信息
        if (CollUtil.isNotEmpty(parameters)) {
            apiDetail.setParameters(parameters);
        } else {
            List<ApiConditionPo> apiConditions = apiConditionMapper.select(apiParam);
            // 数据表模式修改
            if (DataApiType.NORMAL_TABLE == apiType && DataAssetEnums.DataApiOperationType.UPDATE == dataAssetApi.getOperationType()) {
                apiConditions.forEach(apiConditionPo -> {
                    if (HttpParamKind.FILTER == apiConditionPo.getHttpParamKind() && apiConditionPo.getAssetColumns().contains(DwOpenConstant.FILTER_PREFIX)) {
                        String replaceAssetColumn = apiConditionPo.getAssetColumns().replace(DwOpenConstant.FILTER_PREFIX, "");
                        apiConditionPo.setAssetColumns(replaceAssetColumn);
                    }
                });
            }
            apiDetail.setParameters(apiConditions);
        }

        apiParam.setType(DataAssetEnums.FiledTypeEnums.RESULT);
        apiDetail.setResults(apiConditionMapper.select(apiParam));

        SubscribeRecordPo subscribeRecordPo = new SubscribeRecordPo();
        subscribeRecordPo.setHttpId(dataAssetApiId);
        List<SubscribeRecordPo> subscribeRecordPos = subscribeRecordService.find(subscribeRecordPo);
        if (CollectionUtils.isNotEmpty(subscribeRecordPos)) {
            Integer topicId = subscribeRecordPos.get(0).getTopicId();
            SubscribeRecordVo subscribeRecordVo = new SubscribeRecordVo();
            if (topicId != null && topicId > 0) {
                subscribeRecordVo.setIsHttpSubscriber(0);
                TopicVo topicVo = topicService.detail(topicId);
                Integer addressType = topicVo.getAddressType();
                subscribeRecordVo.setAddressType(addressType);
            } else {
                subscribeRecordVo.setAddressType(EventSourceTypeEnum.HTTP.getValue());
                subscribeRecordVo.setIsHttpSubscriber(1);
            }
            SubscribeRecordPo subscribeRecordPoSource = subscribeRecordPos.get(0);
            BeanUtils.copyProperties(subscribeRecordPoSource, subscribeRecordVo);
            apiDetail.setSubscribeRecord(subscribeRecordVo);
        }
        apiDetail.setParameters(parseOperatorParam(apiDetail, false));
        if (DataApiType.LITE_FLOW.equals(apiType)) {
            apiDetail.setResutRespParamDTOS(apiResponseParamService.findLiteflowResult(dataAssetApiId));
        }
        return apiDetail;
    }

    @Override
    public List<ApiDetailVo> queryApiListByGroupIdsAndTypes(List<Integer> apiGroupIdList, List<Integer> apiTypeList, Boolean isRemoveGroupPath) {
        List<ApiDetailVo> apiDetailList = new ArrayList<>();
        Map<Integer, String> apiGroupMap = apiGroupService.queryApiGroupListByIds(apiGroupIdList).stream().collect(Collectors.toMap(ApiGroupPo::getId, ApiGroupPo::getGroupPath));
        for (Integer apiGroupId : apiGroupIdList) {
            Example apiExample = new Example(DataAssetApiPo.class);
            apiExample.createCriteria().andEqualTo("apiGroupId", apiGroupId).andIn("apiType", apiTypeList);
            List<DataAssetApiPo> dataAssetApiPos = getMapper().selectByExample(apiExample);
            String apiGroupPath = apiGroupMap.get(apiGroupId);
            for (DataAssetApiPo dataAssetApiPo : dataAssetApiPos) {
                DataApiType apiType = dataAssetApiPo.getApiType();
                Integer dataAssetApiId = dataAssetApiPo.getDataAssetApiId();
                switch (apiType) {
                    case EXTERNAL_HTTP:
                    case WEB_SERVICE:
                        HttpExternalApiAttr httpExternalApi = httpExternalApiAttrService.getApiAttr(dataAssetApiId, apiType);
                        httpExternalApi.setId(null);
                        httpExternalApi.setApiId(null);
                        dataAssetApiPo.setApiAttr(httpExternalApi);
                        break;
                    case LITE_FLOW:
                        ApiFlowAttr apiFlowAttr = apiFlowAttrService.getApiAttr(dataAssetApiId, apiType);
                        if (Objects.isNull(apiFlowAttr)) {
                            break;
                        }
                        dataAssetApiPo.setApiAttr(dataAssetApiLiteflowService.fillApiOperatorInfo(apiFlowAttr));
                        break;
                    default:
                        break;
                }
                // 移除api path分组路径，在导入时组装新的分组路径
                String dataAssetApiMethod = dataAssetApiPo.getDataAssetApiMethod();
                dataAssetApiPo.setDataAssetApiMethod(isRemoveGroupPath ? dataAssetApiMethod.substring(apiGroupPath.length()) : dataAssetApiMethod);
                // 组装ApiDetailVo
                ApiDetailVo apiDetail = new ApiDetailVo();
                apiDetail.setDataAssetApi(dataAssetApiPo);
                ApiConditionPo apiParam = new ApiConditionPo();
                apiParam.setDataAssetId(dataAssetApiId);
                apiParam.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
                apiDetail.setParameters(apiConditionMapper.select(apiParam));
                apiDetail.setResponseParams(apiResponseParamService.listByApiId(dataAssetApiId));
                apiDetail.setResutRespParamDTOS(apiResponseParamService.findLiteflowResult(dataAssetApiId));
                apiDetailList.add(apiDetail);
            }
        }
        return apiDetailList;
    }

    @Override
    public DataAssetApiPo detail(Integer dataAssetApiId) {
        DataAssetApiPo dataAssetApi = getCurdService().selectByPrimaryKey(dataAssetApiId, getMapper());
        if (ObjectUtil.isEmpty(dataAssetApi)) {
            throw new OpenException(MsgCodeEnum.w_not_exists_api);
        }
        DataApiType apiType = dataAssetApi.getApiType();
        Integer apiId = dataAssetApi.getDataAssetApiId();
        switch (apiType) {
            // HTTP模式和WebService模式可共用逻辑
            case EXTERNAL_HTTP:
            case WEB_SERVICE:
                HttpExternalApiAttr httpExternalApi = httpExternalApiAttrService.getApiAttr(apiId, apiType);
                dataAssetApi.setApiAttr(httpExternalApi);
                break;
            case EVENT_RECEIVE:
                EventReceiveApiAttr eventReceiveApiAttr = eventReceiveApiAttrService.getApiAttr(apiId, apiType);
                dataAssetApi.setApiAttr(eventReceiveApiAttr);
                break;
            case EVENT_SEND:
                EventSendApiAttr eventSendApiAttr = eventSendApiAttrService.getApiAttr(apiId, apiType);
                dataAssetApi.setApiAttr(eventSendApiAttr);
                break;
            case LITE_FLOW:
                ApiFlowAttr apiFlowAttr = apiFlowAttrService.getApiAttr(apiId, apiType);
                if (Objects.isNull(apiFlowAttr)) {
                    break;
                }
                dataAssetApi.setApiAttr(dataAssetApiLiteflowService.fillApiOperatorInfo(apiFlowAttr));
                break;
            case NORMAL_TABLE:
            case CUSTOM_SQL:
                RedisLockConfigAttr redisLockConfigAttr = redisLockConfigAttrService.getApiAttr(apiId, apiType);
                if (Objects.isNull(redisLockConfigAttr)) {
                    break;
                }
                dataAssetApi.setApiAttr(redisLockConfigAttr);
            default:
                break;
        }
        return dataAssetApi;
    }

    @Override
    public List<ApiCollectDo> listIndex(String username, Integer dataAssetId, Integer appId) {
        return getMapper().listByCollect(username, dataAssetId, AuthUtils.currentAppId(), appId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer deleteApi(Integer apiId) {

        // 绑定了定时任务不允许删除API
        xxlJobTaskHelper.checkApiBindXxlJob(apiId);
        List<DataAssetApiPo> dataAssetApis = getRelationApis(apiId);
        if (CollectionUtils.isNotEmpty(dataAssetApis)) {
            throw new OpenException(
                    "关联服务编排API[" + StringUtils.join(dataAssetApis.stream().map(DataAssetApiPo::getApiName).collect(Collectors.toList()), ", ") + "] 处于引用状态, 无法刪除！");
        }
        DataAssetApiPo dataAssetApiPo = getCurdService().selectByPrimaryKey(apiId, getMapper());
        if (dataAssetApiPo == null) {
            throw new OpenException(MsgCodeEnum.w_not_exists_api);
        }
        if (DataAssetPublishStatusEnum.PUBLISH.equals(dataAssetApiPo.getDataAssetPublishStatus())) {
            throw new OpenException(MsgCodeEnum.w_api_is_unpublished_cannot_delete);
        }
        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        // 如果API已经授权至少一个应用，也不允许删除
        Integer count = appApprovalService.selectAuthAppCount(lesseeId, apiId);
        if (0 < count) {
            throw new OpenException(MsgCodeEnum.w_api_is_authorized);
        }

        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setDataAssetId(apiId);
        apiConditionMapper.delete(apiConditionPo);
        appAccessRuleService.delete(apiId);
        dataAssetApiRuleService.deleteRule(apiId);
        dataAssetApiWarnService.deleteWarn(apiId);
        int deleteCnt = getCurdService().deleteByPrimaryKey(apiId, getMapper());

        switch (dataAssetApiPo.getApiType()) {
            case EXTERNAL_HTTP:
                httpExternalApiAttrService.deleteApiAttrByApiId(apiId);
                break;
            case LITE_FLOW:
                apiFlowAttrService.deleteApiAttrByApiId(apiId);
                redisLockConfigAttrService.deleteApiAttrByApiId(apiId);
                break;
            case CUSTOM_SQL:
            case NORMAL_TABLE:
                redisLockConfigAttrService.deleteApiAttrByApiId(apiId);
                break;
            default:
                break;
        }
        return deleteCnt;
    }

    @Override
    public Page<DataAssetApiPo> myCollect(String username, PageQuery pageQuery, Integer accessAppId, Integer dataAssetId, String keyword) {
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        Page<DataAssetApiPo> apiCollectDos = (Page<DataAssetApiPo>) dataAssetApiMapper.listMyCollect(
                username,
                accessAppId,
                dataAssetId,
                AuthUtils.currentAppId(),
                keyword
        );
        return apiCollectDos;
    }

    @Override
    public ResultDTO<ApiTotalCountDo> assetMonitorInfo(Integer dataAssetId) {
        ApiTotalCountDo apiTotalCountDo = new ApiTotalCountDo();
        apiTotalCountDo.setHistoryCount(dataAssetApiMapper.countHistoryAsset(dataAssetId));
        apiTotalCountDo.setTodayCount(dataAssetApiMapper.countTodayAsset(dataAssetId, false, false));
        apiTotalCountDo.setTodaySuccess(dataAssetApiMapper.countTodayAsset(dataAssetId, true, false));
        apiTotalCountDo.setTodayFailed(dataAssetApiMapper.countTodayAsset(dataAssetId, false, true));
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(apiTotalCountDo);
        return resultDTO;
    }

    @Override
    public ResultDTO<Map<String, ApiTimesDo>> countTimesDay(Integer dataAssetId) {

        int hour = 24;
        //24小时前的日期
        Date date = DateUtils.addHours(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY), -24);
        Map<String, BigDecimal> countMap = parseListToMap(dataAssetApiMapper.countTimesHour(hourFormat.format(date), dataAssetId));
        Map<String, BigDecimal> errorMap = parseListToMap(dataAssetApiMapper.countTimesHourError(hourFormat.format(date), dataAssetId));
        Calendar calendar = getHourCalendar();
        Map<String, ApiTimesDo> successMap = new HashMap<>();
        while (hour > 0) {
            ApiTimesDo apiTimesDo = new ApiTimesDo();
            if (countMap.containsKey(hourFormat.format(calendar.getTime()))) {
                apiTimesDo.setTotalNum(countMap.get(hourFormat.format(calendar.getTime())));
            } else {
                apiTimesDo.setTotalNum(BigDecimal.ZERO);
            }
            if (errorMap.containsKey(hourFormat.format(calendar.getTime()))) {
                apiTimesDo.setErrorNum(errorMap.get(hourFormat.format(calendar.getTime())));
            } else {
                apiTimesDo.setErrorNum(BigDecimal.ZERO);
            }
            successMap.put(hourFormat.format(calendar.getTime()), apiTimesDo);
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            hour--;
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(successMap);
        return resultDTO;
    }

    @Override
    public ResultDTO countTimesWeek(Integer dataAssetId) {
        int day = 7;
        //7天前的日期
        Date date = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -7);
        Map<String, BigDecimal> countMap = parseListToMap(dataAssetApiMapper.countTimesDay(dayFormat.format(date), dataAssetId));
        Map<String, BigDecimal> errorMap = parseListToMap(dataAssetApiMapper.countTimesDayError(dayFormat.format(date), dataAssetId));
        Calendar calendar = getDayCalendar();
        Map<String, ApiTimesDo> successMap = new HashMap<>();
        while (day > 0) {
            ApiTimesDo apiTimesDo = new ApiTimesDo();
            if (errorMap.containsKey(dayFormat.format(calendar.getTime()))) {
                apiTimesDo.setErrorNum(errorMap.get(dayFormat.format(calendar.getTime())));
            } else {
                apiTimesDo.setErrorNum(BigDecimal.ZERO);
            }
            if (countMap.containsKey(dayFormat.format(calendar.getTime()))) {
                apiTimesDo.setTotalNum(countMap.get(dayFormat.format(calendar.getTime())));
            } else {
                apiTimesDo.setTotalNum(BigDecimal.ZERO);
            }
            successMap.put(dayFormat.format(calendar.getTime()), apiTimesDo);
            calendar.add(Calendar.DATE, -1);
            day--;
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(successMap);
        return resultDTO;
    }

    public Calendar getDayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public Calendar getHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public Map<String, BigDecimal> parseListToMap(List<ApiCountResultDo> list) {
        if (list == null) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(ApiCountResultDo::getCusDate, ApiCountResultDo::getNum));
    }

    @Override
    public ResultDTO<Map<String, BigDecimal>> countQpsDay(Integer dataAssetId, boolean qps) {
        int hour = 24;
        //24小时前的日期
        Date date = DateUtils.addHours(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY), -24);
        Map<String, BigDecimal> countMap = parseListToMap(qps ? dataAssetApiMapper.countQpsHour(hourFormat.format(date), dataAssetId)
                : dataAssetApiMapper.countTakeTimeHour(hourFormat.format(date), dataAssetId));
        Calendar calendar = getHourCalendar();
        Map<String, BigDecimal> resultMap = new HashMap<>();
        while (hour > 0) {
            if (countMap.containsKey(hourFormat.format(calendar.getTime()))) {
                resultMap.put(hourFormat.format(calendar.getTime()), countMap.get(hourFormat.format(calendar.getTime())));
            } else {
                resultMap.put(hourFormat.format(calendar.getTime()), BigDecimal.ZERO);
            }
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            hour--;
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(resultMap);
        return resultDTO;
    }

    @Override
    public ResultDTO countQpsWeek(Integer dataAssetId, boolean qps) {
        int day = 7;
        //7天前的日期
        Date date = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -7);
        Map<String, BigDecimal> countMap = parseListToMap(qps ? dataAssetApiMapper.countQpsDay(dayFormat.format(date), dataAssetId)
                : dataAssetApiMapper.countTakeTimeDay(dayFormat.format(date), dataAssetId));
        Calendar calendar = getHourCalendar();
        Map<String, BigDecimal> resultMap = new HashMap<>();
        while (day > 0) {
            if (countMap.containsKey(dayFormat.format(calendar.getTime()))) {
                resultMap.put(dayFormat.format(calendar.getTime()), countMap.get(dayFormat.format(calendar.getTime())));
            } else {
                resultMap.put(dayFormat.format(calendar.getTime()), BigDecimal.ZERO);
            }
            calendar.add(Calendar.DATE, -1);
            day--;
        }
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(resultMap);
        return resultDTO;
    }

    @Override
    public ResultDTO<List<AppTimesDo>> countAppTimesDay(Integer dataAssetId) {
        //24小时前的日期
        Date date = DateUtils.addHours(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY), -24);
        List<AppTimesDo> appTimesDos = Optional.ofNullable(dataAssetApiMapper.countAppTimesHour(dayFormat.format(date), dataAssetId)).orElse(new ArrayList<>());
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(appTimesDos);
        return resultDTO;
    }

    @Override
    public ResultDTO<List<AppTimesDo>> countAppTimesWeek(Integer dataAssetId) {
        //7天前的日期
        Date date = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -7);
        List<AppTimesDo> appTimesDos = Optional.ofNullable(dataAssetApiMapper.countAppTimesDay(dayFormat.format(date), dataAssetId)).orElse(new ArrayList<>());
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setMessage(MsgCodeEnum.s_success, true);
        resultDTO.setData(appTimesDos);
        return resultDTO;
    }

    @Override
    public SqlAnalysisDo analysisSql(String sql, Integer dataSourceId) {
        List<DatasourceTableColumnDo> requestParam = new ArrayList<>();
        List<String> request = new ArrayList<>();
        //解析sql参数
        List<SqlParameter> sqlParamList = SqlDynamicParameterParserUtil.matchParam(sql);
        sqlParamList.forEach(param->{
            if(!request.contains(param.getParam())){
                requestParam.add(new DatasourceTableColumnDo(param.getParam(),param.getType(),!param.getIsDynamic(),param.getIsDynamic()));
                request.add(param.getParam());
            }
        });
        //填充sql参数解析sql
        String transformSql = SqlDynamicParameterParserUtil.transformSql(sql, Boolean.TRUE, null);
        Statement parse;
        try {
            parse = CCJSqlParserUtil.parse(transformSql);
        } catch (JSQLParserException e) {
            log.error("analysis sql：{}",transformSql,e);
            throw new OpenException(MsgCodeEnum.w_api_sql_analysis_failed);
        }
        //组装结果数据
        if (parse instanceof Insert) {
            return buildAnalysisDoByNotSelect(requestParam, DataAssetEnums.DataApiOperationType.INSERT);
        } else if (parse instanceof Delete) {
            SqlBlockAttackInnerHelper.parser(parse, sqlParamList, sql);
            return buildAnalysisDoByNotSelect(requestParam, DataAssetEnums.DataApiOperationType.DELETE);
        } else if (parse instanceof Update) {
            SqlBlockAttackInnerHelper.parser(parse, sqlParamList, sql);
            return buildAnalysisDoByNotSelect(requestParam, DataAssetEnums.DataApiOperationType.UPDATE);
        } else if (parse instanceof Select) {
            return buildAnalysisDoBySelect(parse, requestParam, dataSourceId);
        }
        throw new OpenException(MsgCodeEnum.w_unsupported_sql_types);
    }

    /**
     * 构建修改、查询语句的请求、响应参数
     *
     * @param requestParamList 解析出来的请求参数
     * @param sqlOperationType SQL操作类型
     * @return SqlAnalysisDo
     */
    private SqlAnalysisDo buildAnalysisDoByNotSelect(List<DatasourceTableColumnDo> requestParamList, DataAssetEnums.DataApiOperationType sqlOperationType) {
        SqlAnalysisDo sqlAnalysisDo = new SqlAnalysisDo();
        sqlAnalysisDo.setRequestParam(requestParamList);
        sqlAnalysisDo.setResponseParam(new ArrayList<>());
        sqlAnalysisDo.setBaseResponseParam(ParamBuildUtil.getDefaultPageResultParam(sqlOperationType));
        sqlAnalysisDo.setSqlOperationType(sqlOperationType);
        return sqlAnalysisDo;
    }

    /**
     * 构建查询语句的请求、响应参数
     *
     * @param statement        Statement
     * @param requestParamList 解析出来的请求参数
     * @param dataSourceId     数据源id
     * @return SqlAnalysisDo
     */
    private SqlAnalysisDo buildAnalysisDoBySelect(Statement statement, List<DatasourceTableColumnDo> requestParamList, Integer dataSourceId) {
        List<DatasourceTableColumnDo> responseParamList = new ArrayList<>();
        Select select = (Select) statement;
        select.getSelectBody().accept(new MySelectVisitor(responseParamList));
        select.getSelectBody().accept(new SelectVisitorAdapter() {
            @Override
            public void visit(SetOperationList setOperationList) {
                //UNION语句,特殊处理一下,取第一条sql的字段和别名
                setOperationList.getSelects().get(0).accept(new MySelectVisitor(responseParamList));
            }
        });
        if (CollectionUtil.isEmpty(responseParamList)) {
            throw new OpenException(MsgCodeEnum.w_api_sql_response_param_must_not_empty);
        }
        SqlAnalysisDo sqlAnalysisDo = new SqlAnalysisDo();
        sqlAnalysisDo.setRequestParam(requestParamList);
        sqlAnalysisDo.setResponseParam(responseParamList);
        sqlAnalysisDo.setBaseResponseParam(ParamBuildUtil.getDefaultPageResultParam(DataAssetEnums.DataApiOperationType.QUERY));
        sqlAnalysisDo.setSqlOperationType(DataAssetEnums.DataApiOperationType.QUERY);
        //设置分页参数
        fillDefaultDataSourceQueryCondition(requestParamList, dataSourceId);
        checkResponseParamNameAndFormat(responseParamList);
        return sqlAnalysisDo;
    }

    /**
     * 填充分页参数
     *
     * @param requestParam 请求参数集合
     * @param dataSourceId 数据源id
     */
    private void fillDefaultDataSourceQueryCondition(List<DatasourceTableColumnDo> requestParam, Integer dataSourceId) {
        if (dataSourceId == null) {
            return;
        }
        DataSourcePo dataSourcePo = dataSourceMapper.selectByPrimaryKey(dataSourceId);
        if (dataSourcePo == null) {
            return;
        }
        DatasourceTypeEnum dbType = dataSourcePo.getDbType();
        if (DatasourceTypeEnum.HBASE == dbType) {
            ApiConditionDefaultColumnEnum rowKey = ApiConditionDefaultColumnEnum.ROW_KEY;
            requestParam.add(new DatasourceTableColumnDo(rowKey.getAssetColumns(), rowKey.getAssetDatatype(), rowKey.getDescriptions(), "rowkey", rowKey.getRequired()));
        } else {
            ApiConditionDefaultColumnEnum pageNo = ApiConditionDefaultColumnEnum.PAGE_NO;
            requestParam.add(new DatasourceTableColumnDo(pageNo.getAssetColumns(), pageNo.getAssetDatatype(), pageNo.getDescriptions(), "1", pageNo.getRequired()));
            ApiConditionDefaultColumnEnum pageSize = ApiConditionDefaultColumnEnum.PAGE_SIZE;
            requestParam.add(new DatasourceTableColumnDo(pageSize.getAssetColumns(), pageSize.getAssetDatatype(), pageSize.getDescriptions(), "10", pageSize.getRequired()));
            ApiConditionDefaultColumnEnum orderBy = ApiConditionDefaultColumnEnum.ORDER_BY;
            requestParam.add(new DatasourceTableColumnDo(orderBy.getAssetColumns(), orderBy.getAssetDatatype(), orderBy.getDescriptions(), null, orderBy.getRequired()));
        }
    }

    /**
     * 检查SQL模式API响应参数是否存在"."，如果不存在，需要把特殊参数去除
     *
     * @param responseParam 响应参数列表
     */
    private void checkResponseParamNameAndFormat(List<DatasourceTableColumnDo> responseParam) {
        responseParam.forEach(x -> {
            String columnName = x.getDatasourceTableColumnName();
            if (columnName.contains(StrUtil.DOT)) {
                MsgCodeEnum msgCodeEnum = MsgCodeEnum.w_api_sql_analysis_column_alias_must_not_null;
                throw new OpenException(msgCodeEnum.getCode(), String.format(msgCodeEnum.getDesc(), columnName));
            }
            // 响应参数需要去除特殊符号
            x.setDatasourceTableColumnName(columnName.replace("`", ""));
        });
    }

    @Override
    public List<Integer> findGroupIds(Integer apiGroupId) {
        return apiGroupService.findGroupIds(apiGroupId);
    }

    @Override
    public DataAssetApiPo selectByApiPath(String apiPath) {
        if (apiPath.startsWith("/")) {
            apiPath = apiPath.substring(1, apiPath.length());
        }
        DataAssetApiPo dataAssetApi = new DataAssetApiPo();
        dataAssetApi.setDataAssetApiMethod(apiPath);
        List<DataAssetApiPo> apiList = this.getCurdService().select(dataAssetApi, this.dataAssetApiMapper);
        if (CollectionUtils.isEmpty(apiList)) {
            throw new OpenException(MsgCodeEnum.w_not_exists_api);
        }
        return apiList.iterator().next();
    }

    @Override
    public Integer updateByApiId(DataAssetApiPo dataAssetApiPo) {
        return getCurdService().updateByPrimaryKeySelective(dataAssetApiPo, getMapper());
    }


    @Override
    public PageResultDTO<List<AuthApiRespDTO>> listByAppIdGroupIdName(AppAuthApiReqDTO dto) {
        List<Integer> groupIds = findGroupIds(dto.getApiGroupId());
        if (CollectionUtils.isEmpty(groupIds)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }

        dto.setApiGroupIds(groupIds);
        Page<Object> page = PageHelper.startPage(dto.getPageNo(), dto.getPageSize());
        PageInfo<AuthApiRespDTO> pageInfo = new PageInfo<>(dataAssetApiMapper.listByAppIdGroupIdName(dto));

        return createPageResultDTO(page, pageInfo);
    }

    private <T> PageResultDTO<List<T>> createPageResultDTO(Page<Object> page, PageInfo<T> pageInfo) {
        PageResultDTO<List<T>> pageResultDTO = new PageResultDTO<>();
        pageResultDTO.setPageNo(page.getPageNum());
        pageResultDTO.setPageSize(page.getPageSize());
        pageResultDTO.setTotalCount((int) pageInfo.getTotal());
        pageResultDTO.setData(pageInfo.getList());
        return pageResultDTO;
    }

    @Override
    public PageResultDTO<List<DataAssetApiPo>> listNotAuthApi(NotAuthApiReqParam param) {
        if (Objects.nonNull(param.getApiGroupId())) {
            param.setApiGroupIds(findGroupIds(param.getApiGroupId()));
        }

        Page<Object> page = PageHelper.startPage(param.getPageNo(), param.getPageSize());
        PageInfo<DataAssetApiPo> pageInfo = new PageInfo<>(dataAssetApiMapper.listNotAuthApi(param));

        return createPageResultDTO(page, pageInfo);
    }

    /**
     * 根据API ID获取节点信息（无论是编排或者单个API）
     *
     * @param dataAssetApiId dataAssetApiId API ID
     * @return 节点信息集合（如果非编排只有一个只有一个元素）
     */
    @Override
    public List<ApiInfoDTO> getApiInfos(Integer dataAssetApiId) {
        List<ApiInfoDTO> result = new ArrayList<>();
        DataAssetApiPo dataAssetApiPo = getCurdService().selectByPrimaryKey(dataAssetApiId, getMapper());
        DataApiType apiType = dataAssetApiPo.getApiType();
        switch (apiType) {
            case EXTERNAL_HTTP:
                result.add(new ApiInfoDTO(dataAssetApiPo.getDataAssetApiId(), dataAssetApiPo.getApiName()));
                break;
            case LITE_FLOW:
                ApiFlowAttr apiFlowAttr = apiFlowAttrService.getApiAttr(dataAssetApiPo.getDataAssetApiId(), dataAssetApiPo.getApiType());
                List<ApiInfoDTO> infoDTOList = dataAssetApiLiteflowService.getApiOperatorInfo(apiFlowAttr);
                if(CollUtil.isNotEmpty(infoDTOList)){
                    result.addAll(infoDTOList);
                }
                break;
            default:
                break;
        }
        return result;
    }

    private class MySelectVisitor extends SelectVisitorAdapter {
        private List<DatasourceTableColumnDo> responseParam;

        public MySelectVisitor(List<DatasourceTableColumnDo> responseParam) {
            this.responseParam = responseParam;
        }

        @Override
        public void visit(PlainSelect plainSelect) {
            List<SelectItem> selectItems = plainSelect.getSelectItems();
            selectItems.forEach(selectItem -> {
                DatasourceTableColumnDo tableColumn = new DatasourceTableColumnDo();
                selectItem.accept(new SelectItemVisitorAdapter() {
                    @Override
                    public void visit(SelectExpressionItem selectExpressionItem) {
                        //普通的select a from b
                        if (selectExpressionItem.getExpression() instanceof Column) {
                            String columnName = ((Column) selectExpressionItem.getExpression()).getColumnName();
                            String tableName = null;
                            if (null != ((Column) selectExpressionItem.getExpression()).getTable()) {
                                tableName = ((Column) selectExpressionItem.getExpression()).getTable().getName();
                            }
                            String field = tableName == null ? columnName : tableName + "." + columnName;
                            tableColumn.setDatasourceTableColumnName(field);
                            if (selectExpressionItem.getAlias() != null) {
                                String aliasName = selectExpressionItem.getAlias().getName();
                                tableColumn.setAlias(aliasName);
                                tableColumn.setDatasourceTableColumnName(aliasName);
                            }
                            //select `a` from b
                        } else if (selectExpressionItem.getExpression() instanceof StringValue) {
                            tableColumn.setDatasourceTableColumnName((((StringValue) selectExpressionItem.getExpression()).getValue()));
                            tableColumn.setAlias(selectExpressionItem.getAlias().getName());
                            //select case when a = 0 then 0 else null end as c ,sum(a) from b
                        } else if (selectExpressionItem.getExpression() instanceof CaseExpression || selectExpressionItem.getExpression() instanceof Function) {
                            //((CaseExpression) selectExpressionItem.getExpression()).getElseExpression().
                            if (selectExpressionItem.getAlias() == null) {
                                throw new OpenException(MsgCodeEnum.w_api_sql_analysis_alias_error);
                            }
                            String aliasName = selectExpressionItem.getAlias().getName();
                            tableColumn.setAlias(aliasName);
                            tableColumn.setDatasourceTableColumnName(aliasName);
                        } else {
                            if (selectExpressionItem.getAlias() == null) {
                                throw new OpenException(MsgCodeEnum.w_api_sql_analysis_alias_must_not_null);
                            }
                            String aliasName = selectExpressionItem.getAlias().getName();
                            tableColumn.setAlias(aliasName);
                            tableColumn.setDatasourceTableColumnName(aliasName);
                        }
                        responseParam.add(tableColumn);
                    }
                });
            });
        }
    }

    @Override
    public PageResultDTO<List<DataAssetApiDTO>> pageList(ApiQuery query) {
        PageResultDTO<List<DataAssetApiDTO>> resultDTO = new PageResultDTO<>();

        setQueryParamByRole(query);

        // 分页查询
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<DataAssetApiPo> pageList = this.getMapper().selectPageList(query);
        if (pageList.isEmpty()) {
            resultDTO.setData(new ArrayList<>());
            return resultDTO;
        }
        PageInfo<DataAssetApiPo> pageInfo = new PageInfo<>(pageList);
        List<DataAssetApiPo> list = pageInfo.getList();
        List<DataAssetApiDTO> dataList = BeanUtil.copyList(list, DataAssetApiDTO.class);

        Map<Integer, String> map = apiGroupHelper.getGroupList(dataList);
        for (DataAssetApiDTO dataAssetApiDTO : dataList) {
            // 设置接口分组名称
            dataAssetApiDTO.setApiGroupName(map.get(dataAssetApiDTO.getApiGroupId()));
            // 设置开发者所看到的信息(审核状态和申请状态)
            if (!query.isPlatformAdmin()) {
                // 企业开发者-api申请状态(true)
                dataAssetApiDTO.setApplyStatus(Objects.isNull(dataAssetApiDTO.getApprovalStatus()) ? ApiApplyStatusEnum.UN_APPLY : ApiApplyStatusEnum.getEnum(dataAssetApiDTO.getApprovalStatus().getValue()));
                // 企业开发者-审核状态(false)
                dataAssetApiDTO.setApprovalStatus(null);
            }
        }

        resultDTO.setData(dataList);
        resultDTO.setPageNo(query.getPageNo());
        resultDTO.setPageSize(query.getPageSize());
        resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
        return resultDTO;
    }

    @Override
    public void checkApiMethodExists(DataAssetApiPo dataAssetApi) {
        DataAssetApiPo dataAssetApiPo = new DataAssetApiPo();
        dataAssetApiPo.setDataAssetApiMethod(dataAssetApi.getDataAssetApiMethod());
        if (select(dataAssetApiPo).size() > 0) {
            throw new OpenException(MsgCodeEnum.w_api_path_exists);
        }
    }

    @Override
    public List<DataAssetApiPo> findByDataAssetApiMethodList(List<String> dataAssetApiMethodList) {
        Example example = new Example(DataAssetApiPo.class);
        example.createCriteria().andIn("dataAssetApiMethod", dataAssetApiMethodList);
        return getMapper().selectByExample(example);
    }

    @Override
    public List<DataAssetApiPo> findByGroupIds(List<Integer> groupIds) {
        return dataAssetApiMapper.listByGroupId(groupIds);
    }

    @Override
    public Boolean checkApiName(String apiName) {
        DataAssetApiPo checkApiName = new DataAssetApiPo();
        checkApiName.setApiName(apiName);
        List<DataAssetApiPo> checkApiNameList = dataAssetApiMapper.select(checkApiName);
        return CollectionUtil.isNotEmpty(checkApiNameList);
    }

    @Override
    public Boolean checkApiPath(String apiPath) {
        DataAssetApiPo checkApiPath = new DataAssetApiPo();
        checkApiPath.setDataAssetApiMethod(apiPath);
        List<DataAssetApiPo> checkApiPathList = dataAssetApiMapper.select(checkApiPath);
        return CollectionUtil.isNotEmpty(checkApiPathList);
    }

    @Override
    public DocumentApiDetailVo parseApiDetailOfDocument(ApiDetailVo apiDetailVo) {
        if (DataAssetEnums.PublicEnums.PRIVATE.equals(apiDetailVo.getDataAssetApi().getSecret())) {
            throw new OpenException(OpenApiMsgCodeEnum.w_data_api_is_private_not_view_document);
        }
        if (DataAssetPublishStatusEnum.UN_PUBLISH.getValue().equals(apiDetailVo.getDataAssetApi().getDataAssetPublishStatus().getValue())) {
            throw new OpenException(MsgCodeEnum.w_data_api_not_publish);
        }
        String domain = dwOpenCommonConfig.getDomainName();
        if("/".equals(domain.substring(domain.length()-1))){
            domain = domain.substring(0,domain.length() - 1);
        }
        //设置域名
        apiDetailVo.setDomainName(domain);
        //给api地址加上ipaas系统前缀
        apiDetailVo.getDataAssetApi().setDataAssetApiMethod(openApiAuthHandler.getPlatformDefaultApiPrefix() + apiDetailVo.getDataAssetApi().getDataAssetApiMethod());
        DataAssetApiPo dataAssetApiPo = apiDetailVo.getDataAssetApi();
        //定义响应参数示例map
        Map<String, String> responseParamsExample = new HashMap<>(16);
        //当API的类型不是服务编排时，将响应参数的responseParams字段数据转换成json、xml格式之后，生成一个map放到DocumentApiDetailVo的responseParamsExample字段中
        Integer apiTypeValue = dataAssetApiPo.getApiType().getValue();
        if (!DataAssetEnums.DataApiType.LITE_FLOW.getValue().equals(apiTypeValue)) {
            // 目前只有HTTP API类型的API才需要做响应参数映射
            responseParamsExample = DataAssetEnums.DataApiType.EXTERNAL_HTTP.getValue().equals(apiTypeValue) ?
                    generateResponseParamsExample(conversionApiRespParamList(apiDetailVo), Boolean.FALSE) :
                    generateResponseParamsExample(apiDetailVo.getResponseParams(), Boolean.FALSE);
            AbstractApiAttr apiAttr = dataAssetApiPo.getApiAttr();
            if (ObjectUtil.isNotEmpty(apiAttr)) {
                //将外部Api属性中的apiId隐藏
                apiAttr.setApiId(null);
            }
        }
        //当API的类型是服务编排时，将流程编排外部Api属性中的apiId加密，将响应参数的resultRespParamDTOS字段数据转换成json、xml格式之后，生成一个map放到DocumentApiDetailVo的responseParamsExample字段中
        if (DataAssetEnums.DataApiType.LITE_FLOW.getValue().equals(apiTypeValue)) {
            //响应参数塞值，因为流程编排的响应参数放在resultRespParamDTOS字段，非流程编排的响应参数放在responseParams，统一封装在responseParams，前端不再需要使用校验而分别到这两个字段取值解析
            apiDetailVo.setResponseParams(apiDetailVo.getResutRespParamDTOS());
            //响应示例塞值
            responseParamsExample = generateResponseParamsExample(apiDetailVo.getResutRespParamDTOS(), Boolean.TRUE);
            ApiFlowAttr apiFlowAttr = (ApiFlowAttr) dataAssetApiPo.getApiAttr();
            if (ObjectUtil.isNotEmpty(apiFlowAttr)) {
                //隐藏流程编排中的API算子的数据
                apiFlowAttr.setOperators(null);
                //将外部Api属性中的apiId隐藏
                apiFlowAttr.setApiId(null);
            }
        }
        //将请求参数示例转换成xml格式与json格式
        if (CollectionUtils.isNotEmpty(apiDetailVo.getParameters())) {
            String format = null;
            //过滤body类型的参数
            List<ApiConditionPo> requestParams = apiDetailVo.getParameters().stream().filter(p -> HttpParamKind.BODY.equals(p.getHttpParamKind())).collect(Collectors.toList());
            Map<String, String> requestExample = new HashMap<>();
            if (CollectionUtils.isEmpty(requestParams)) {
                requestExample = DocumentManagementUtils.getJsonAndXml(null, "requestExample", "requestExampleXml", null, Boolean.TRUE);
            } else {
                //如果不为空，类型为body的响应参数只有一个
                ApiConditionPo apiConditionPo = requestParams.get(0);
                //当responsePostData中的数据是json格式，并且isSchema是true时才对数据进行格式转换
                requestExample = DocumentManagementUtils.getJsonAndXml(apiConditionPo.getJsonSchema(), "requestExample", "requestExampleXml", apiConditionPo.getAssetDatatype(), Boolean.TRUE);
            }
            apiDetailVo.setParametersExample(requestExample);
        }
        DocumentApiDetailVo documentApiDetailVo = BeanUtil.copy(apiDetailVo, DocumentApiDetailVo.class);
        documentApiDetailVo.setResponseParamsExample(responseParamsExample);
        documentApiDetailVo.setErrorExample(getErrorExample());
        documentApiDetailVo.setDataAssetApi(BeanUtil.copy(dataAssetApiPo, DocumentDataAssetApiVo.class));
        //当API类型是数据表和自定义SQL类型时，去除请求参数中包含的公共参数
        if (DataAssetEnums.DataApiType.CUSTOM_SQL.getValue().equals(apiTypeValue) || DataAssetEnums.DataApiType.NORMAL_TABLE.getValue().equals(apiTypeValue)) {
            List<ApiConditionPo> apiConditionPos = apiDetailVo.getParameters();
            apiDetailVo.setParameters(apiConditionPos.stream().filter(a -> DataAssetEnums.FiledTypeAttrEnums.OPERATOR.equals(a.getTypeAttr())).collect(Collectors.toList()));
        }
        documentApiDetailVo.setParameters(BeanUtil.copyList(apiDetailVo.getParameters(), DocumentApiConditionVo.class));
        documentApiDetailVo.setResults(BeanUtil.copyList(apiDetailVo.getResults(), DocumentApiConditionVo.class));
        return documentApiDetailVo;
    }

    @Override
    public List<DataAssetApiDetailDTO> getPublishApiListByGroupId(Integer apiGroupId, Long lesseeId) {

        List<DataAssetApiPo> dataAssetApiPoList = dataAssetApiMapper.getPublishApiListByGroupId(apiGroupId,lesseeId);
        if(CollUtil.isEmpty(dataAssetApiPoList)){
            return null;
        }
        List<DataAssetApiDetailDTO> dataAssetApiDetailDTOList = BeanUtil.copyList(dataAssetApiPoList, DataAssetApiDetailDTO.class);
        for(DataAssetApiDetailDTO apiDetailDTO : dataAssetApiDetailDTOList){
            //body需要解析schema对应的json串
            //请求参数 dw_open_api_condition，type=1
            //数据表查询、自定义sql 响应参数 从dw_open_api_condition，type=2
            //http、webservice、服务编排响应参数从dw_open_api_response_param
            ApiConditionPo conditionPo = new ApiConditionPo();
            conditionPo.setDataAssetId(apiDetailDTO.getDataAssetApiId());
            conditionPo.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
            apiDetailDTO.setApiConditionPoList(BeanUtil.copyList(apiConditionMapper.select(conditionPo),ApiConditionDTO.class));
            if(isTableOrSql(apiDetailDTO)){

                apiDetailDTO.setApiRespParamList(Collections.singletonList(getApiRespParamDTO(apiDetailDTO)));

            }else if(isRestApi(apiDetailDTO)){
                //body参数将jsonschema解析成对象结构
                requestBodyParse(apiDetailDTO);
                //获取响应参数
                apiDetailDTO.setApiRespParamList(getApiRespParamDTOS(apiDetailDTO));
            }
        }
        return dataAssetApiDetailDTOList;
    }

    /**
     * api模式body参数需要转换
     * @param apiDetailDTO
     * @return
     */
    private List<ApiRespParamDTO> getApiRespParamDTOS(DataAssetApiDetailDTO apiDetailDTO) {
        List<ApiResponseParamPo> apiResponseData = apiResponseParamService.getApiResponseData(apiDetailDTO.getLesseeId(), apiDetailDTO.getDataAssetApiId());
        List<ApiRespParamDTO> respParamDTOList = new ArrayList<>();
        if(CollUtil.isNotEmpty(apiResponseData)) {
            for (ApiResponseParamPo condition : apiResponseData) {
                if (HttpParamKind.BODY == condition.getType()) {
                    ApiRespParamDTO apiRespParamDTO = JsonSchemaConvertUtil.convert(condition.getResponsePostData());
                    if (ObjectUtil.isNotEmpty(apiRespParamDTO)) {
                        respParamDTOList.add(apiRespParamDTO);
                    }
                } else {
                    respParamDTOList.add(BeanUtil.copy(condition, ApiRespParamDTO.class));
                }
            }
        }
        return respParamDTOList;
    }

    /**
     * 请求body参数解析
     * @param apiDetailDTO
     */
    private static void requestBodyParse(DataAssetApiDetailDTO apiDetailDTO) {
        if(CollUtil.isNotEmpty(apiDetailDTO.getApiConditionPoList())) {
            for (ApiConditionDTO condition : apiDetailDTO.getApiConditionPoList()) {
                if (HttpParamKind.BODY == condition.getHttpParamKind()) {
                    condition.setSchemaParseDataList(Collections.singletonList(JsonSchemaConvertUtil.convert(condition.getJsonSchema())));
                    break;
                }
            }
        }
    }

    /**
     * api模式
     * @param apiDetailDTO
     * @return
     */
    private static boolean isRestApi(DataAssetApiDetailDTO apiDetailDTO) {
        return DataApiType.EXTERNAL_HTTP == apiDetailDTO.getApiType()
                || DataApiType.LITE_FLOW == apiDetailDTO.getApiType()
                || DataApiType.WEB_SERVICE == apiDetailDTO.getApiType();
    }

    /**
     * 数据表或sql模式
     * @param apiDetailDTO
     * @return
     */
    private static boolean isTableOrSql(DataAssetApiDetailDTO apiDetailDTO) {
        return (DataApiType.NORMAL_TABLE == apiDetailDTO.getApiType()
                && DataAssetEnums.DataApiOperationType.QUERY == apiDetailDTO.getOperationType())
                || DataApiType.CUSTOM_SQL == apiDetailDTO.getApiType();
    }

    /**
     * 数据表及sql模式需要组装响应参数
     * @param apiDetailDTO
     * @return
     */
    private ApiRespParamDTO getApiRespParamDTO(DataAssetApiDetailDTO apiDetailDTO) {
        ApiConditionPo conditionPoResp = new ApiConditionPo();
        conditionPoResp.setDataAssetId(apiDetailDTO.getDataAssetApiId());
        conditionPoResp.setType(DataAssetEnums.FiledTypeEnums.RESULT);
        List<ApiConditionPo> apiConditionPoList = apiConditionMapper.select(conditionPoResp);
        List<ApiRespParamDTO> apiRespParamDTOList = new ArrayList<>();
        if(CollUtil.isNotEmpty(apiConditionPoList)) {
            for (ApiConditionPo po : apiConditionPoList) {
                if (!HttpExternalApiAttr.ALL_FIELD.equals(po.getAssetColumns())) {
                    ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
                    apiRespParamDTO.setId(po.getId());
                    apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
                    apiRespParamDTO.setType(HttpParamKind.BODY);
                    apiRespParamDTO.setAssetColumns(po.getAssetColumns());
                    apiRespParamDTO.setAssetDataType(po.getAssetDatatype());
                    apiRespParamDTO.setDescription(po.getDescriptions());
                    apiRespParamDTOList.add(apiRespParamDTO);
                }
            }
        }
        ApiRespParamDTO respParamDTO = new ApiRespParamDTO();
        respParamDTO.setId(DwOpenConstant.RESPONSE_PARENT_ID);
        respParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
        respParamDTO.setType(HttpParamKind.BODY);
        respParamDTO.setAssetColumns(HttpParamKind.BODY.name());
        respParamDTO.setAssetDataType(null);
        respParamDTO.setDescription(null);
        respParamDTO.setChildApiRespParams(apiRespParamDTOList);
        return respParamDTO;
    }

    /**
     * 根据API配置的参数映射规则，组装API响应参数列表
     *
     * @param apiDetailVo API详情对象
     * @return API响应参数列表
     */
    private List<ApiRespParamDTO> conversionApiRespParamList(ApiDetailVo apiDetailVo) {
        List<ApiRespParamDTO> responseParams = apiDetailVo.getResponseParams();
        // 判断是否配置映射
        Integer respMappingRuleId = apiDetailVo.getDataAssetApi().getRespMappingRule();
        boolean isDisableRespMapping = respMappingRuleId == null || DwOpenConstant.DEFAULT_RESPONSE_PARAM_MAPPING_RULE_ID.equals(respMappingRuleId);
        if (isDisableRespMapping) {
            return generateDisabledMappingRespParams(responseParams);
        }
        // 启用了配置映射，判断是否配置惟客云参数映射
        RespParamMappingRulePo respParamMappingRulePo = respParamMappingRuleMapper.selectByPrimaryKey(respMappingRuleId);
        if (respParamMappingRulePo == null) {
            throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_not_find_by_api);
        }
        boolean isWakeCloudRespMapping = DwOpenConstant.WAKE_CLOUD_RESPONSE_PARAM_MAPPING_RULE_ID.equals(respMappingRuleId);
        if (isWakeCloudRespMapping) {
            // 惟客云参数映射直接返回API响应表的响应参数
            return responseParams;
        }
        List<ApiRespParamDTO> bodyResponseParams = responseParams.stream().filter(p -> HttpParamKind.BODY.equals(p.getType())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(bodyResponseParams)) {
            return responseParams;
        }
        String dataKey = PublicReturnParametersEnum.DATA.getAttributeName();
        JSONObject mappingRuleJson = JSONObject.parseObject(respParamMappingRulePo.getRespParamMappingRule());
        // 如果data的value是空，就当不配置映射规则的响应参数
        Object data = mappingRuleJson.get(dataKey);
        if (data == null || (data instanceof String && StringUtils.isBlank(data.toString()))) {
            return generateDisabledMappingRespParams(responseParams);
        }
        boolean dataIsString = data instanceof String;
        // 先转换成可以通过jsonpath解析到参数的对象
        ApiRespParamDTO bodyResponseParam = JsonSchemaConvertUtil.convert(bodyResponseParams.get(0).getResponsePostData());
        JSONObject jsonSchemaObject = JSONObject.parseObject(JsonSchemaConvertUtil.toJsonSchemaString(bodyResponseParam));
        // 扁平化表达式，根据表达式去响应参数的schema找到对应的节点
        Map<String, Object> mappingRuleMap = new JsonFlattener(respParamMappingRulePo.getRespParamMappingRule()).withSeparator('.').flattenAsMap();
        Iterator<Map.Entry<String, Object>> iterator = mappingRuleMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue().toString();
            // 文档只处理data参数的映射表达式即可，并且value不能为空
            if (key.startsWith(dataKey) && StringUtils.isNotBlank(value)) {
                // 转换表达式，匹配出对应的schema时转换成响应参数对象覆盖value值
                String expression = value.replace(".", ".properties.").replace("$.", "$.root.");
                Object eval = JSONPath.eval(jsonSchemaObject, expression);
                if (eval == null) {
                    iterator.remove();
                } else {
                    ApiRespParamDTO apiRespParamDTO = JsonSchemaConvertUtil.convertSectionSchema(JSONObject.toJSONString(eval));
                    mappingRuleMap.put(key, apiRespParamDTO);
                }
            }
        }
        // 去扁平，转换回JSONObject对象。将对象中的响应参数结构放入最终的转换schema的响应对象中
        boolean hasPageParamMapping = mappingRuleJson.containsKey(PublicReturnParametersEnum.PAGE_NO.getAttributeName())
                || mappingRuleJson.containsKey(PublicReturnParametersEnum.PAGE_SIZE.getAttributeName())
                || mappingRuleJson.containsKey(PublicReturnParametersEnum.TOTAL_COUNT.getAttributeName());
        ApiRespParamDTO resultDtoRespParam = hasPageParamMapping ? JsonSchemaConvertUtil.convertPageResultDto() : JsonSchemaConvertUtil.convertResultDto();
        ApiRespParamDTO dataRespParam = null;
        for (ApiRespParamDTO childApiRespParam : resultDtoRespParam.getChildApiRespParams()) {
            if (!dataKey.equals(childApiRespParam.getAssetColumns())) {
                continue;
            }
            dataRespParam = childApiRespParam;
            break;
        }
        if (dataRespParam == null) {
            dataRespParam = buildDefaultDataRespParam();
        }
        JSONObject conversionRule = JSONObject.parseObject(JsonUnflattener.unflatten(JSONObject.toJSONString(mappingRuleMap)));
        JSONObject dataValue = conversionRule.getJSONObject(dataKey);
        if (dataValue != null) {
            if (dataIsString) {
                BeanUtils.copyProperties(JSONObject.parseObject(dataValue.toJSONString(), ApiRespParamDTO.class), dataRespParam);
                dataRespParam.setAssetColumns(dataKey);
            } else {
                List<ApiRespParamDTO> childApiRespParams = new ArrayList<>();
                for (String key : dataValue.keySet()) {
                    childApiRespParams.add(JSONObject.parseObject(dataValue.getJSONObject(key).toJSONString(), ApiRespParamDTO.class));
                }
                dataRespParam.setChildApiRespParams(childApiRespParams);
            }
        }
        for (ApiRespParamDTO apiRespParamDTO : responseParams) {
            if (!HttpParamKind.BODY.equals(apiRespParamDTO.getType())) {
                continue;
            }
            apiRespParamDTO.setResponsePostData(JsonSchemaConvertUtil.toJsonSchemaString(resultDtoRespParam));
        }
        return responseParams;
    }

    /**
     * 构建标准的ResultDTO中的data对象响应参数
     *
     * @return data对象响应参数
     */
    private ApiRespParamDTO buildDefaultDataRespParam() {
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setAssetColumns(PublicReturnParametersEnum.DATA.getAttributeName());
        apiRespParamDTO.setAssetDataType(DataTypeEnum.OBJECT.getType());
        apiRespParamDTO.setDescription(PublicReturnParametersEnum.DATA.getAttributeDesc());
        return apiRespParamDTO;
    }

    /**
     * 生成不配置映射规则的响应参数
     *
     * @param responseParams API响应参数列表
     * @return 组装后的响应参数
     */
    private List<ApiRespParamDTO> generateDisabledMappingRespParams(List<ApiRespParamDTO> responseParams) {
        for (ApiRespParamDTO apiRespParamDTO : responseParams) {
            if (!HttpParamKind.BODY.equals(apiRespParamDTO.getType())) {
                continue;
            }
            // 把原来响应的schema放入ResultDTO schema的data参数内
            ApiRespParamDTO resultDtoRespParam = JsonSchemaConvertUtil.convertResultDto();
            for (ApiRespParamDTO childApiRespParam : resultDtoRespParam.getChildApiRespParams()) {
                if (!PublicReturnParametersEnum.DATA.getAttributeName().equals(childApiRespParam.getAssetColumns())) {
                    continue;
                }
                childApiRespParam.setChildApiRespParams(JsonSchemaConvertUtil.convert(apiRespParamDTO.getResponsePostData()).getChildApiRespParams());
                break;
            }
            apiRespParamDTO.setResponsePostData(JsonSchemaConvertUtil.toJsonSchemaString(resultDtoRespParam));
            break;
        }
        return responseParams;
    }

    /**
     * 封装响应示例
     *
     * @param list 响应参数数组
     * @param isLiftFlow 是否流程编排
     */
    private Map<String, String> generateResponseParamsExample(List<ApiRespParamDTO> list, Boolean isLiftFlow) {
        if (CollectionUtils.isNotEmpty(list)) {
            //过滤出body类型的参数
            List<ApiRespParamDTO> responseParams = list.stream().filter(p -> HttpParamKind.BODY.equals(p.getType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(responseParams)) {
                //如果不为空，类型为body的响应参数只有一个
                ApiRespParamDTO apiRespParamDTO = responseParams.get(0);
                //当responsePostData中的数据是json格式，并且isSchema是true时才对数据进行格式转换
                //流程编排少了一层ResultDTO包装，需要特殊处理
                if (isLiftFlow) {
                    String result = null;
                    com.wakedata.common.core.dto.PageResultDTO<Object> resultDTO = new com.wakedata.common.core.dto.PageResultDTO<>();
                    resultDTO.setTotalCount(10L);
                    resultDTO.setPageSize(10);
                    resultDTO.setPageNo(1);
                    Map<String, String> map = new HashMap<>();
                    //响应示例需要返回公共响应参数
                    if (ObjectUtil.isEmpty(apiRespParamDTO.getAssetDataType())) {
                        //将data字符串转换成对象，塞进resultDTO的data字段中，这样处理在转换xml的时候才不会出现格式错误
                        Object parseObject = JSONObject.parseObject(apiRespParamDTO.getResponsePostData(), Object.class);
                        resultDTO.setData(parseObject);
                        result = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(JSONUtil.parseObj(resultDTO)));
                        map.put("responseExample", String.valueOf(resultDTO));
                        map.put("responseExampleXml", result);
                        return map;
                    } else if (DataAssetEnums.AssetDataTypeEnum.JSON.getValue().equalsIgnoreCase(apiRespParamDTO.getAssetDataType())) {
                        Object parseObject = JSONObject.parseObject(JsonUtils.JsonSchemaToJson(apiRespParamDTO.getResponsePostData()), Object.class);
                        resultDTO.setData(parseObject);
                        cn.hutool.json.JSONObject object = new cn.hutool.json.JSONObject(resultDTO);
                        result = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(object));
                        map.put("responseExample", String.valueOf(JSON.toJSON(resultDTO)));
                        map.put("responseExampleXml", result);
                        return map;
                    }
                    //如果传入的Body数据类型不为且不等于json，返回默认响应示例
                    return DocumentManagementUtils.getResultMap(Boolean.FALSE, "responseExample", "responseExampleXml", resultDTO);
                }else {
                    return DocumentManagementUtils.getJsonAndXml(apiRespParamDTO.getResponsePostData(), "responseExample", "responseExampleXml", apiRespParamDTO.getAssetDataType(), Boolean.FALSE);
                }
            }
        }
        return DocumentManagementUtils.getJsonAndXml(null, "responseExample", "responseExampleXml", null, Boolean.FALSE);
    }

    //获取标准异常示例
    private Map<String, String> getErrorExample() {
        com.wakedata.common.core.dto.ResultDTO<String> errorExample = com.wakedata.common.core.dto.ResultDTO.fail();
        errorExample.setData(StringUtils.SPACE);
        String errorExampleJson = String.valueOf(JSONUtil.parseObj(errorExample));
        String errorExampleXml = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(JSONUtil.parse(errorExampleJson)));
        Map<String, String> map = new HashMap<>();
        map.put("errorExampleJson", errorExampleJson);
        map.put("errorExampleXml", errorExampleXml);
        return map;
    }

    /**
     * 判断API Path路径是否超过了最大限制级数，如果超出限制抛出异常
     *
     * @param dataAssetApiMethod API Path
     */
    private void checkApiPathSlashSplitCount(String dataAssetApiMethod) {
        if (dataAssetApiMethod.split(StrUtil.SLASH).length > DwOpenConstant.MAX_API_PATH_SLASH_SPLIT_COUNT) {
            throw new OpenException(MsgCodeEnum.w_wrong_api_path_is_not_allowed_to_exceed_level_4);
        }
    }

    /**
     * 根据API的不同类型做不同的参数校验
     *
     * @param apiType      API类型
     * @param dataAssetApi DataAssetApiPo
     */
    private void checkParamForApiType(DataAssetEnums.DataApiType apiType, DataAssetApiPo dataAssetApi) {
        // 以后存在其他API类型的参数校验可以在这个方法拓展
        if (apiType == DataApiType.EXTERNAL_HTTP) {
            HttpExternalApiAttr apiAttr = (HttpExternalApiAttr) dataAssetApi.getApiAttr();
            String host = apiAttr.getHost();
            this.checkApiDomainName(host);
        }
    }

    /**
     * 检查Api外部域名
     *
     * @param apiDomainName Api外部域名
     */
    private void checkApiDomainName(String apiDomainName) {
        if (StringUtils.isBlank(apiDomainName)) {
            throw new OpenException(MsgCodeEnum.w_api_domain_name_is_not_empty);
        }
        URI uri = URLUtil.toURI(apiDomainName);
        if (Objects.isNull(uri.getHost()) || Objects.isNull(uri.getScheme())) {
            throw new OpenException(MsgCodeEnum.w_domain_name_error);
        }
        if (StringUtils.isNoneBlank(uri.getPath())) {
            throw new OpenException(MsgCodeEnum.w_domain_name_path_is_not_allowed_error);
        }
    }

    /**
     * 根据角色设置查询参数
     *
     * @param query 查询条件
     */
    private void setQueryParamByRole(ApiQuery query) {
        // 平台管理员(发布和未发布状态) 开发者(发布状态)
        query.setDataAssetPublishStatus(query.isPlatformAdmin() ? query.getDataAssetPublishStatus() : DataAssetPublishStatusEnum.PUBLISH.getValue());
        // 平台管理员(不涉及) 开发者(申请状态->审核状态)
        query.setApiApplyStatus(query.isPlatformAdmin() ? null : query.getApiApplyStatus());
        // 平台管理员(不涉及) 开发者查询审核表需要租户
        query.setLesseeId(query.isPlatformAdmin() ? null : query.getLesseeId());
        // 平台管理员（null-全查询） 开发（1-公开状态）
        query.setSecret(query.isPlatformAdmin()? query.getSecret() : DataAssetEnums.PublicEnums.PUBLIC.getValue());
        // 平台管理员(发布时间区间查询) 开发者(不涉及)
        query.setStartPublishTime(query.isPlatformAdmin() ? query.getStartPublishTime() : null);
        query.setEndPublishTime(query.isPlatformAdmin() ? query.getEndPublishTime() : null);
    }

    /**
     * 获取相应的接口分组信息
     *
     * @param dataList api列表信息
     * @return Map集合(key = id value = name)
     */
    private Map<Integer, String> getGroupList(List<DataAssetApiDTO> dataList) {

        List<Integer> apiGroupIdList = dataList.stream().map(DataAssetApiDTO::getApiGroupId).collect(Collectors.toList());
        List<ApiGroupPo> apiGroupPoList = apiGroupService.queryApiGroupListByIds(apiGroupIdList);
        if (CollectionUtils.isEmpty(apiGroupPoList)) {
            return new HashMap<>();
        }
        return apiGroupPoList.stream().collect(Collectors.toMap(ApiGroupPo::getId, ApiGroupPo::getGroupName, (apiGroupId, apiGroupName) -> apiGroupName));
    }

    /**
     * 构建WebService默认请求参数accessWsdl
     *
     * @param dataAssetApiId API ID
     * @param date           创建/更新时间
     * @return ApiConditionPo
     */
    private ApiConditionPo buildWsDefaultCondition(Integer dataAssetApiId, Date date) {
        ApiConditionDefaultColumnEnum accessWsdl = ApiConditionDefaultColumnEnum.ACCESS_WSDL;
        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setDataAssetId(dataAssetApiId);
        apiConditionPo.setAssetColumns(accessWsdl.getAssetColumns());
        apiConditionPo.setAssetDatatype(accessWsdl.getAssetDatatype());
        apiConditionPo.setAssetColumnsLength(accessWsdl.getAssetColumnsLength());
        apiConditionPo.setDescriptions(accessWsdl.getDescriptions());
        apiConditionPo.setType(DataAssetEnums.FiledTypeEnums.PARAMETERS);
        apiConditionPo.setTypeAttr(DataAssetEnums.FiledTypeAttrEnums.OPERATOR);
        apiConditionPo.setHttpParamKind(HttpParamKind.QUERY);
        apiConditionPo.setSample(Boolean.FALSE.toString());
        apiConditionPo.setRequired(accessWsdl.getRequired());
        apiConditionPo.setAutoPare(Boolean.TRUE);
        apiConditionPo.setCreateTime(date);
        apiConditionPo.setUpdateTime(date);
        return apiConditionPo;
    }

}