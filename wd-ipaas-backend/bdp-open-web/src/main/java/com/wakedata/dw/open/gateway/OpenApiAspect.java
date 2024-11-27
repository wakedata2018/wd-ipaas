package com.wakedata.dw.open.gateway;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.common.core.resultcode.CommonResultCode;
import com.wakedata.dw.open.accesstoken.*;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.*;
import com.wakedata.dw.open.mapper.log.AccessLogMapper;
import com.wakedata.dw.open.model.api.ApiRulePo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.model.warn.ApiWarnLogPo;
import com.wakedata.dw.open.model.warn.ApiWarnPo;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.service.accesstoken.dto.AccessTokenGenerateDTO;
import com.wakedata.dw.open.service.accesstoken.dto.AccessTokenRefreshDTO;
import com.wakedata.dw.open.service.api.BlackListService;
import com.wakedata.dw.open.service.api.WhiteListService;
import com.wakedata.dw.open.service.log.AccessLogService;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.service.utils.DocumentManagementUtils;
import com.wakedata.dw.open.utils.IpAddressUtil;
import com.wakedata.dw.warn.business.WarnHandleFactory;
import com.wakedata.dw.warn.entity.WarnMailDo;
import com.wakedata.dw.warn.enums.WarnTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author tanzhi
 * @title OpenApiAspect
 * @projectName bdp-open
 * @date 2019/8/16 9:54
 * @description
 */
@Aspect
@Component
@Slf4j
public class OpenApiAspect {

    private static final String LOCAL_HOST_ADDRESS = "0:0:0:0:0:0:0:1";
    private static final String LOOPBACK_ADDRESS = "127.0.0.1";

    @Resource
    private AccessLogMapper accessLogMapper;
    @Resource
    private DataAssetApiMapper dataAssetApiMapper;
    @Resource
    private AccessLogService accessLogService;
    @Resource
    private WhiteListService whiteListService;
    @Resource
    private ApiWarnMapper apiWarnMapper;
    @Resource
    private WarnHandleFactory warnHandleFactory;
    @Resource
    private ApiWarnLogMapper apiWarnLogMapper;
    @Resource
    private ApiRuleMapper apiRuleMapper;
    @Autowired
    private BlackListService blackListService;
    @Resource
    private OpenApiDataCache openApiDataCache;
    @Resource
    private OpenApiAuthHandler openApiAuthHandler;

    /**
     * 接口调用拦截，限流记录日志
     * @param pjp
     * @return
     * @param <T>
     */
    @Around("execution(public * com.wakedata.dw.open.controller.DataApiGatewayController.dataApiGateway(..))")
    public <T> T recordAccess(ProceedingJoinPoint pjp) {
        //参数初始化
        OpenApiParams apiParams = new OpenApiParams();
        apiParams.initParams(pjp.getArgs());
        AppAccessInfo app = null;
        DataAssetApiPo assetApi = null;
        try {
            //auth验证
            app = openApiAuthHandler.authenticate(apiParams);
            //api权限验证
            assetApi = openApiAuthHandler.checkApiAuthAndThrowExc(apiParams.getIsApiTest(),apiParams.getDataAssetApiMethod(), app);
            //限流 + 执行api
            return apiQpsLimit(apiParams,app,assetApi, openApiParams->(appAccess, dataAssetApi)->{
                //获取ip
                String ipAddress = IpAddressUtil.getIpAddress(openApiParams.getRequest());
                long date = System.currentTimeMillis();
                //添加访问日志
                AccessLogPo accessLog = createAccessLog(openApiParams, appAccess, dataAssetApi, ipAddress, date);
                ResultDTO retVal = null;
                try {
                    // 黑白名单校验
                    checkBlackWhiteList(ipAddress, appAccess.getDataAccessKey());
                    // 限流规则
                    doApiRule(dataAssetApi.getDataAssetApiId());
                    //api执行
                    retVal = executeApi(appAccess.getDataAccessKey(),pjp);
                    //添加执行结果
                    setAccessLogResultRow(accessLog, retVal.getData());
                    accessLog.setResultCode(retVal.getCode());
                    accessLog.setResultDescription(buildResDescription(retVal.getCode(),retVal.getMsg()));
                } catch (OpenException e) {
                    retVal = ResultDTO.fail(e.getCode(),e.getMessage());
                    accessLog.setResultCode(e.getCode());
                    accessLog.setResultDescription(buildResDescription(retVal.getCode(),retVal.getMsg()));
                    log.error("调用时open api时发生异常 errorCode={} errorMessage={},{}", e.getCode(), e.getMessage(), e.getStackTrace(), e);
                } catch (Throwable e) {
                    retVal = ResultDTO.fail(OpenApiMsgCodeEnum.s_error.getCode(),e.getMessage());
                    accessLog.setResultCode(OpenApiMsgCodeEnum.s_error.getCode());
                    accessLog.setResultDescription(buildResDescription(retVal.getCode(),retVal.getMsg()));
                    log.error("发生其他系统异常 errorCode={} errorMessage={}", OpenApiMsgCodeEnum.s_error.getCode(), e.getMessage(), e);
                }
                setAccessLogRespData(openApiParams, dataAssetApi, accessLog, retVal);
                //记录api的处理时间
                setAccessLogHandleTime(date, accessLog);
                //改为异步添加日志
                accessLogService.addApiAccessLog(accessLog);
                // 告警处理
                if (accessLog.getResultCode() != HttpServletResponse.SC_OK) {
                    doWarn(accessLog);
                }
                //响应数据
                return response(openApiParams, dataAssetApi, retVal);
            });
        }finally {
            //资源释放
            OpenApiAuthParamThreadLocal.removeAuthParams();
            openApiDataCache.removeAppAccessInfoByPrefix(apiParams.getApiPrefix());
            if(app != null) {
                openApiDataCache.removeApiCache(apiParams.getDataAssetApiMethod());
                if (assetApi != null) {
                    openApiDataCache.removeAppApiAssetColumns(assetApi.getDataAssetApiId(), app.getDataAccessAppId());
                }
            }
        }
    }

    /**
     * 设置api响应数据
     * @param openApiParams
     * @param dataAssetApi
     * @param accessLog
     * @param retVal
     */
    private static void setAccessLogRespData(OpenApiParams openApiParams, DataAssetApiPo dataAssetApi, AccessLogPo accessLog, ResultDTO retVal) {
        accessLog.setDataAssetMethod(dataAssetApi.getDataAssetApiMethod());
        accessLog.setDataAssetName(dataAssetApi.getApiName());
        accessLog.setDataAssetGroup(dataAssetApi.getApiGroupName());
        JSONObject reqParam = JSONUtil.parseObj(openApiParams);
        reqParam.set("reqParam",openApiParams.getRequest().getParameterMap());
        accessLog.setDataRequestParam(JSONUtil.toJsonStr(reqParam));
        accessLog.setDataResponseParam(JSONUtil.toJsonStr(retVal));
    }


    /**
     * accessToken接口调用拦截记录日志
     * @param pjp
     * @return
     */
    @Around("execution(public * com.wakedata.dw.open.controller.DataApiTokenController.*(..))")
    public ResultDTO<AccessTokenGenerateDTO> accessTokenRecord(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        HttpServletRequest request = (HttpServletRequest) args[0];
        //获取ip
        String ipAddress = IpAddressUtil.getIpAddress(request);
        long date = System.currentTimeMillis();
        //添加访问日志
        AppAccessInfo appAccess = openApiDataCache.getAppAccessInfo((String)args[1]);
        if(appAccess == null){
            log.error("token获取失败，param = {},requestUrl = {}",JSONUtil.toJsonStr(args),request.getRequestURI());
            throw new OpenException(OpenApiMsgCodeEnum.w_data_gateway_app_id_no_found);
        }
        AccessLogPo accessLog = getTokenAccessLog(ipAddress, date, appAccess);
        ResultDTO retVal = null;
        try {
            //api执行
            retVal = (ResultDTO) pjp.proceed(args);
            //添加执行结果
            setAccessLogResultRow(accessLog, retVal.getData());
            accessLog.setResultCode(retVal.getCode().equals(CommonResultCode.SUCCESS.getCode()) ?OpenApiMsgCodeEnum.s_success.getCode():retVal.getCode());
            accessLog.setResultDescription(buildResDescription(retVal.getCode(),retVal.getMsg()));
        } catch (OpenException e) {
            retVal = ResultDTO.fail(e.getCode(),e.getMessage());
            accessLog.setResultCode(e.getCode());
            accessLog.setResultDescription(buildResDescription(retVal.getCode(),retVal.getMsg()));
            log.error("调用时open api时发生异常 errorCode={} errorMessage={},{}", e.getCode(), e.getMessage(), e.getStackTrace(), e);
        } catch (Throwable e) {
            retVal = ResultDTO.fail(OpenApiMsgCodeEnum.s_error.getCode(),e.getMessage());
            accessLog.setResultCode(OpenApiMsgCodeEnum.s_error.getCode());
            accessLog.setResultDescription(buildResDescription(retVal.getCode(),retVal.getMsg()));
            log.error("发生其他系统异常 errorCode={} errorMessage={}", OpenApiMsgCodeEnum.s_error.getCode(), e.getMessage(), e);
        }
        setAccessTokenRespData(request, accessLog, retVal);
        //记录api的处理时间
        setAccessLogHandleTime(date, accessLog);
        //改为异步添加日志
        accessLogService.addApiAccessLog(accessLog);
        return retVal;
    }

    private static void setAccessTokenRespData(HttpServletRequest request, AccessLogPo accessLog, ResultDTO retVal) {
        String assetName = "";
        if(request.getRequestURI().contains("get.access.token")){
            assetName = "获取AccessToken";
        }else if(request.getRequestURI().contains("refresh.token")){
            assetName = "刷新AccessToken";
        }else if(request.getRequestURI().contains("check.token")){
            assetName = "校验AccessToken";
        }
        accessLog.setDataAssetMethod(request.getRequestURI());
        accessLog.setDataAssetName(assetName);
        accessLog.setDataAssetGroup("获取AccessToken");
        accessLog.setDataRequestParam(JSONUtil.toJsonStr(request.getParameterMap()));
        accessLog.setDataResponseParam(JSONUtil.toJsonStr(retVal));
    }

    private static AccessLogPo getTokenAccessLog(String ipAddress, long date, AppAccessInfo appAccess) {
        AccessLogPo accessLog = new AccessLogPo();
        accessLog.setLesseeId(appAccess.getLesseeId());
        accessLog.setAppId(appAccess.getDataAccessAppId());
        accessLog.setDataAccessName(appAccess.getDataAccessAppName());
        accessLog.setDataAssetApiId(0);
        accessLog.setIp(ipAddress);
        accessLog.setCreateTime(new Date(date));
        accessLog.setApiInvokeSource(ApiInvokeSource.API_EXTERNAL.getValue());
        accessLog.setApiInvokeSourceType(ApiInvokeSource.API_EXTERNAL.getCode());
        return accessLog;
    }

    /**
     * 执行api
     * @param appKey
     * @param pjp
     * @return
     * @throws Throwable
     */
    private ResultDTO executeApi(String appKey,ProceedingJoinPoint pjp) throws Throwable {
        //设置appKey
        Object[] args = pjp.getArgs();
        args[3] = appKey;
        //执行api
        return (ResultDTO) pjp.proceed(args);
    }

    /**
     * 数据响应
     * @param openApiParams
     * @param dataAssetApi
     * @param retVal
     * @return
     * @param <T>
     */
    private <T> T response(OpenApiParams openApiParams, DataAssetApiPo dataAssetApi, ResultDTO retVal) {
        //webservice返回格式
        if (DataAssetEnums.DataApiType.WEB_SERVICE.equals(dataAssetApi.getApiType())) {
            return webserviceReturn(openApiParams.getRequest().getHeader(RequestParamUtils.CONTENT_TYPE), openApiParams.getResponse(), retVal);
        }
        //当API返回格式是xml
        if (RequestParamUtils.CONTENT_TYPE_XML.equals(openApiParams.getRequest().getHeader(RequestParamUtils.CONTENT_TYPE))) {
            xmlReturn(openApiParams.getResponse(), retVal);
            return null;
        }
        return (T) retVal;
    }


    /**
     * qps限制
     * @param openApiParams
     * @param accessInfo
     * @param dataAssetApi
     * @param function
     * @return
     * @param <T>
     */
    private <T> T apiQpsLimit(OpenApiParams openApiParams, AppAccessInfo accessInfo, DataAssetApiPo dataAssetApi
            , Function<OpenApiParams,BiFunction<AppAccessInfo,DataAssetApiPo,T>> function){
        //限流
        try (Entry entry = SphU.entry(String.valueOf(dataAssetApi.getDataAssetApiId()))) {
            return function.apply(openApiParams).apply(accessInfo,dataAssetApi);
        } catch (BlockException ex) {
            log.error("open api error:{}，{}",ex.getMessage(), ex);
            return qpsLimitFailed();
        }
    }

    /**
     * 获取错误详情
     *
     * @param code 错误编号
     * @param msg 错误详情
     * @return 错误详情
     */
    private String buildResDescription(Integer code,String msg) {

        if (ObjectUtil.equal(code,OpenApiMsgCodeEnum.s_success.getCode())){
            return OpenApiMsgCodeEnum.s_success.getDesc();
        }

        if (StringUtils.isNotEmpty(msg) && ObjectUtil.equal(code,OpenApiMsgCodeEnum.s_api_failed_to_execute.getCode())){
            msg = StringUtils.substringBetween(msg, DwOpenConstant.MIDDLE_BRACKET_LEFT, DwOpenConstant.MIDDLE_BRACKET_RIGHT);
            return StringUtils.substringAfterLast(msg,DwOpenConstant.COLON);
        }
        return msg;
    }

    /**
     * API返回格式是XML的处理方式
     * @param response
     * @param retVal
     * @return
     */
    private <T> T xmlReturn(HttpServletResponse response, ResultDTO retVal) {
        JSONConfig jsonConfig = new JSONConfig().setIgnoreNullValue(Boolean.FALSE);
        cn.hutool.json.JSONObject object = new cn.hutool.json.JSONObject(retVal,jsonConfig);
        String xmlResult = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(object));
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader(RequestParamUtils.CONTENT_TYPE, RequestParamUtils.CONTENT_TYPE_XML);
            response.getWriter().write(xmlResult);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * webservice返回
     * @param contentType
     * @param response
     * @param retVal
     * @param <T>
     * @return
     */
    private <T> T webserviceReturn(String contentType,HttpServletResponse response, ResultDTO retVal) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String data = (String)retVal.getData();
            //返回Json
            if(ContentType.JSON.getValue().equals(contentType)){
                Map<String,Object> res = StrUtil.isBlank(data) ? null : XmlUtil.xmlToMap(data);
                retVal.setData(res);
                response.setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
                response.getWriter().write( JSONUtil.toJsonStr(retVal));
            } else {
                response.setHeader(Header.CONTENT_TYPE.getValue(), ContentType.XML.getValue());
                if (StrUtil.isBlank(data)) {
                    response.getWriter().write(DocumentManagementUtils.standardXmlResult(retVal.getMsg(), retVal.getCode(), retVal.isSuccess(), ""));
                    return null;
                }
                Document document = DocumentHelper.parseText(data);
                Element rootElement = document.getRootElement();
                response.getWriter().write(DocumentManagementUtils.standardXmlResult(retVal.getMsg(), retVal.getCode(), retVal.isSuccess(), rootElement.asXML()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    private <T> void setAccessLogResultRow(AccessLogPo accessLog, T data) {
        if(data == null){
            accessLog.setResultRow(0);
        }else if (data instanceof List) {
            List dataList = (List) data;
            accessLog.setResultRow(dataList.size());
        }
    }

    /**
     * 设置接口请求耗时
     * @param date
     * @param accessLog
     */
    private void setAccessLogHandleTime(long date, AccessLogPo accessLog) {
        long dateEnd = System.currentTimeMillis();
        long elapsed = dateEnd - date;
        accessLog.setElapsed((int) elapsed);
        accessLog.setUpdateTime(new Date(dateEnd));
    }


    /**
     * 添加访问日志
     * @param openApiParams
     * @param appAccess
     * @param dataAssetApi
     * @param ipAddress
     * @param date
     * @return
     */
    private AccessLogPo createAccessLog(OpenApiParams openApiParams, AppAccessInfo appAccess, DataAssetApiPo dataAssetApi, String ipAddress, long date) {
        AccessLogPo accessLog = new AccessLogPo();
        accessLog.setVersion(openApiParams.getVersion());
        accessLog.setLesseeId(appAccess.getLesseeId());
        accessLog.setAppId(appAccess.getDataAccessAppId());
        accessLog.setDataAccessName(appAccess.getDataAccessAppName());
        accessLog.setSeqNo(openApiParams.getSeqNo());
        accessLog.setDataAssetApiId(dataAssetApi.getDataAssetApiId());
        accessLog.setIp(ipAddress);
        accessLog.setCreateTime(new Date(date));
        setAccessLogInvokeTypeAndSource(accessLog,openApiParams);
        return accessLog;
    }

    /**
     * qps超限制出错
     * @param <T>
     * @return
     */
    private <T> T qpsLimitFailed() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(OpenApiMsgCodeEnum.w_qps_limit_failed.getCode());
        resultDTO.setMsg(OpenApiMsgCodeEnum.w_qps_limit_failed.getDesc());
        return (T) resultDTO;
    }

    /**
     * 检查黑白名单规则.
     *
     * @param ipAddress ip地址.
     * @param appKey    appKey.
     */
    private void checkBlackWhiteList(String ipAddress, String appKey) {
        log.info("请求来的IP:{}", ipAddress);
        if (!LOOPBACK_ADDRESS.equals(ipAddress) && !LOCAL_HOST_ADDRESS.equals(ipAddress)) {
            // 黑名单校验
            Set<String> blackList = blackListService.checkBlockList(appKey);
            if (CollectionUtils.isNotEmpty(blackList) && blackList.contains(ipAddress)) {
                log.info("ip在黑名单中：{}", ipAddress);
                throw new OpenException(OpenApiMsgCodeEnum.w_operation_not_permitted);
            }

            //IP限制
            Set<String> list = whiteListService.accessWhiteList(appKey);
            if (CollectionUtils.isNotEmpty(list) && !list.contains(ipAddress)) {
                log.info("ip不在白名单:{}", ipAddress);
                throw new OpenException(OpenApiMsgCodeEnum.w_operation_not_permitted);
            }
        }
    }

    /**
     * 执行告警逻辑
     *
     * @param accessLogPo
     */
    private void doWarn(AccessLogPo accessLogPo) {
        List<ApiWarnPo> apiWarnInfos = apiWarnMapper.findByApiId(accessLogPo.getDataAssetApiId());
        if (CollectionUtils.isEmpty(apiWarnInfos)) {
            return;
        }
        ApiWarnPo po = apiWarnInfos.get(0);
        if (!po.getStatus()) {
            return;
        }
        String apiName = dataAssetApiMapper.selectByPrimaryKey(accessLogPo.getDataAssetApiId()).getApiName();
        ApiWarnLogPo apiWarnLogPo = new ApiWarnLogPo();
        if (po.getRuleSecond() != null) {
            BigDecimal actual = BigDecimal.valueOf(accessLogPo.getElapsed() / 1000);
            BigDecimal threshold = BigDecimal.valueOf(po.getRuleSecond());
            if (actual.compareTo(threshold) == 1) {
                if (apiWarnLogMapper.countWarnLogToday(accessLogPo.getDataAssetApiId(), WarnTypesEnum.overtime.getValue()) > 0) {
                    log.info("已发送告警信息,忽略此次告警");
                    return;
                }
                StringBuffer content = new StringBuffer();
                content.append("api调用超时,调用时间:").append(actual).append("s,告警阈值设置为:").append(threshold.toString()).append("s");
                apiWarnLogPo.setContent(content.toString());
                apiWarnLogPo.setType(WarnTypesEnum.overtime.getValue());
                sendWarnInfo(apiWarnLogPo, accessLogPo, po, apiName);
            }
        }

        if (po.getRuleErrorTimes() != null) {
            Integer errorTimes = accessLogMapper.queryErrorTimes(accessLogPo.getDataAssetApiId()) + 1;
            if (po.getRuleErrorTimes() <= errorTimes) {
                if (apiWarnLogMapper.countWarnLogToday(accessLogPo.getDataAssetApiId(), WarnTypesEnum.overrun.getValue()) > 0) {
                    log.info("已发送告警信息,忽略此次告警");
                    return;
                }
                StringBuffer content = new StringBuffer();
                content.append("api调用错误超出阈值,今日调用错误次数:").append(errorTimes).append("阈值设置:").append(po.getRuleErrorTimes());
                apiWarnLogPo.setContent(content.toString());
                apiWarnLogPo.setType(WarnTypesEnum.overrun.getValue());
                sendWarnInfo(apiWarnLogPo, accessLogPo, po, apiName);
            }
        }

    }

    /**
     * 校验api规则.
     *
     * @param apiId apiId.
     */
    private void doApiRule(Integer apiId) {
        ApiRulePo apiRule = new ApiRulePo();
        apiRule.setDataAssetApiId(apiId);

        List<ApiRulePo> rulePos = apiRuleMapper.select(apiRule);
        if (CollectionUtils.isNotEmpty(rulePos)) {
            ApiRulePo apiRulePo = rulePos.get(0);
            if (apiRulePo.getDayLimit() != null) {
                if (accessLogMapper.countDayLog(apiId) >= apiRulePo.getDayLimit()) {
                    log.info("该api超过了天调用次数限制");
                    throw new OpenException(OpenApiMsgCodeEnum.w_day_limit_failed);
                }
            }
            if (apiRulePo.getMonthLimit() != null) {
                if (accessLogMapper.countMonthLog(apiId) >= apiRulePo.getMonthLimit()) {
                    log.info("该api超过了月调用次数限制");
                    throw new OpenException(OpenApiMsgCodeEnum.w_month_limit_failed);
                }
            }
            if (apiRulePo.getTotalLimit() != null) {
                if (accessLogMapper.countTotalLog(apiId) >= apiRulePo.getTotalLimit()) {
                    log.info("该api超过了总调用次数限制");
                    throw new OpenException(OpenApiMsgCodeEnum.w_total_limit_failed);
                }
            }
        }
    }

    private void sendWarnInfo(ApiWarnLogPo apiWarnLogPo, AccessLogPo accessLogPo, ApiWarnPo po, String apiName) {
        apiWarnLogPo.setApiId(accessLogPo.getDataAssetApiId());
        apiWarnLogPo.setWarnTypeEnum(WarnTypeEnum.email);
        apiWarnLogPo.setCreateTime(new Date());
        apiWarnLogPo.setWarnUser(po.getEmail());
        WarnMailDo warnMailDo = new WarnMailDo();
        warnMailDo.setMailUser(po.getEmail());
        warnMailDo.setMailSubject("数据服务api(" + apiName + ")调用异常");
        warnMailDo.setMailContent(apiWarnLogPo.getContent());
        warnHandleFactory.handler(WarnTypeEnum.email).doWarn(warnMailDo);
        apiWarnLogMapper.insert(apiWarnLogPo);
    }

    /**
     * 获取来源字段并set进日志对象
     * 示例：应用名+调用类型(+id)
     *
     * @param accessLog 日志对象
     * @param openApiParams query参数
     */
    private void setAccessLogInvokeTypeAndSource(AccessLogPo accessLog, OpenApiParams openApiParams){

        // 判断是否通过RestTemplateUtil调用(通过query参数：_ipaas_invoke_source_= 来源+(定时任务/接受事件的)id)
        Map<String, String[]> parameterMap = openApiParams.getRequest().getParameterMap();
        String[] values = parameterMap.get(DwOpenConstant.API_INVOKE_SOURCE_PARAM_NAME);
        if (parameterMap.containsKey(DwOpenConstant.API_INVOKE_SOURCE_PARAM_NAME) && Objects.nonNull(values) && values.length == 1){
            accessLog.setApiInvokeSourceType(getApiInvokeSourceType(values[0]));
            accessLog.setApiInvokeSource(values[0]);
            return;
        }
        if(ObjectUtil.isNotNull(openApiParams.getIsApiTest()) && openApiParams.getIsApiTest()){
            accessLog.setApiInvokeSourceType(getApiInvokeSourceType(ApiInvokeSource.API_TEST.getValue()));
            accessLog.setApiInvokeSource(ApiInvokeSource.API_TEST.getValue());
            return;
        }
        // 判断是否为测试应用
        if (openApiAuthHandler.isApiTest(openApiParams)) {
            accessLog.setApiInvokeSourceType(getApiInvokeSourceType(ApiInvokeSource.API_TEST.getValue()));
            accessLog.setApiInvokeSource(ApiInvokeSource.API_TEST.getValue());
        } else {
            accessLog.setApiInvokeSourceType(getApiInvokeSourceType(ApiInvokeSource.API_EXTERNAL.getValue()));
            accessLog.setApiInvokeSource(ApiInvokeSource.API_EXTERNAL.getValue());
        }

    }

    /**
     * 构建来源apiInvokeSourceType字段
     */
    private Integer getApiInvokeSourceType(String info){

        // 固定格式：调用类型:XXX
        String value = StringUtils.substringBefore(info, ":");
        if (StringUtils.isEmpty(value)){
            return null;
        }

        ApiInvokeSource enumByValue = ApiInvokeSource.getEnumByValue(value);
        return Objects.nonNull(enumByValue) ? enumByValue.getCode() : null;
    }
}
