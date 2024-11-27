package com.wakedata.dw.open.openapi;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.constant.ThirdAuthConstant;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.RespParamMappingRuleMapper;
import com.wakedata.dw.open.model.api.RespParamMappingRulePo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.attr.ExternalApiInvokeService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.strategy.impl.HttpExternalApiInvokeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * http类型api执行处理
 *
 * @author luomeng
 * @date 2022/9/30 17:28
 */
@Service
@Scope(value = SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class OpenApiGatewayHttpService extends OpenApiGatewayAbstractService {

    @Resource
    private ExternalApiInvokeService externalApiInvokeService;

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Resource
    private RespParamMappingRuleMapper respParamMappingRuleMapper;

    @Override
    public DataAssetEnums.DataApiType apiType() {
        return DataAssetEnums.DataApiType.EXTERNAL_HTTP;
    }

    @Override
    public void before(JSONObject params) {

    }

    @Override
    public <T> ResultDTO<T> after(T result, ResultDTO<T> resultDTO) {
        return checkAndResult(result, resultDTO);
    }

    private <T> ResultDTO<T> checkAndResult(T result, ResultDTO<T> resultDTO) {
        if (result instanceof JSONObject) {
            Object body = ((JSONObject) result).get(HttpParamKind.BODY.name());
            if (body instanceof String) {
                try {
                    JSONObject resultBody = JSONObject.parseObject((String) body);
                    // 根据两种类型的映射规则，进行不同的处理（null和0是不进行映射的类型，其它数字按照映射规则处理）
                    if (ObjectUtil.defaultIfNull(this.dataAssetApiPo.getRespMappingRule(), CommonConstant.ZERO) == CommonConstant.ZERO) {
                        // api没有关联映射规则
                        resultDTO.setData((T) resultBody);
                    } else {
                        // 其它的都是存在关联关系的
                        // 获取参数映射规则
                        RespParamMappingRulePo respParamMappingRulePo = respParamMappingRuleMapper.selectByPrimaryKey(dataAssetApiPo.getRespMappingRule());
                        if (ObjectUtil.isEmpty(respParamMappingRulePo)) {
                            throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_not_find_by_api);
                        }
                        JSONObject respParamMappingRule = JSONObject.parseObject(respParamMappingRulePo.getRespParamMappingRule());
                        resultDTO = buildResultDto(respParamMappingRule, resultBody);
                    }
                }catch (OpenException o) {
                    throw new OpenException(o.getCode(), o.getMessage());
                } catch (Exception e) {
                    log.error("api解析返回值异常,result = {},e = {}", body, e);
                }
            }
        }
        return resultDTO;
    }

    /**
     * 通过表达式判断是ResultDTO/PageResultDTO
     */
    private <T> ResultDTO<T> buildResultDto(JSONObject respParamMappingRule, JSONObject resultBody){
        // 针对data被多个参数映射
        T data;
        if (respParamMappingRule.get(PublicReturnParametersEnum.DATA.getAttributeName()) instanceof JSONObject) {
            JSONObject respParamMappingRuleData = respParamMappingRule.getJSONObject(PublicReturnParametersEnum.DATA.getAttributeName());
            JSONObject resultBodyDataJsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : respParamMappingRuleData.entrySet()) {
                resultBodyDataJsonObject.put(entry.getKey(), JSONPath.eval(resultBody, (String) entry.getValue()));
            }
            data = (T) resultBodyDataJsonObject;
        }else {
            data = (T) getValue(resultBody,  respParamMappingRule.getString(PublicReturnParametersEnum.DATA.getAttributeName()));
        }
        // 针对msg被多个参数映射
        String msg;
        if (respParamMappingRule.get(PublicReturnParametersEnum.MSG.getAttributeName()) instanceof JSONObject) {
            JSONObject respParamMappingRuleMsg = respParamMappingRule.getJSONObject(PublicReturnParametersEnum.MSG.getAttributeName());
            JSONObject msgJsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : respParamMappingRuleMsg.entrySet()) {
                msgJsonObject.put(entry.getKey(), JSONPath.eval(resultBody, (String) entry.getValue()));
            }
            msg = msgJsonObject.toJSONString();
        }else {
            msg = (String) getValue(resultBody, respParamMappingRule.getString(PublicReturnParametersEnum.MSG.getAttributeName()));
        }
        // 如果三个分页参数pageNo、pageSize、totalCount都没有映射，规则中会将这三个key都删掉，所以只需要判断其中一个key是否存在就可以
        Boolean isPageResultDto = respParamMappingRule.containsKey(PublicReturnParametersEnum.PAGE_NO.getAttributeName());
        return judgeAndReturnResultDto(respParamMappingRule, resultBody, data, msg, isPageResultDto);
    }


    private <T> ResultDTO<T> judgeAndReturnResultDto(JSONObject respParamMappingRule, JSONObject resultBody, T data, String msg, Boolean isPageResultDto) {
        Boolean success = (Boolean) getValue(resultBody, respParamMappingRule.getString(PublicReturnParametersEnum.SUCCESS.getAttributeName()));
        if (isPageResultDto) {
            Integer pageNo = (Integer) convertPageParamType(getValue(resultBody, respParamMappingRule.getString(PublicReturnParametersEnum.PAGE_NO.getAttributeName())), DataTypeEnum.INTEGER);
            Integer pageSize = (Integer) convertPageParamType(getValue(resultBody, respParamMappingRule.getString(PublicReturnParametersEnum.PAGE_SIZE.getAttributeName())), DataTypeEnum.INTEGER);
            Long totalCount = (Long) convertPageParamType(getValue(resultBody, respParamMappingRule.getString(PublicReturnParametersEnum.TOTAL_COUNT.getAttributeName())), DataTypeEnum.LONG);
            // api特殊处理，映射规则已经映射分页参数，但是api实际不是查询类型接口，所以判断映射之后三个分页参数都为空的情况下，直接走生成ResultDTO逻辑
            if (ObjectUtil.isEmpty(pageNo) && ObjectUtil.isEmpty(pageSize) && ObjectUtil.isEmpty(totalCount)) {
                return judgeAndReturnResultDto(respParamMappingRule, resultBody, data, msg, Boolean.FALSE);
            }
            PageResultDTO<T> pageResultDTO = new PageResultDTO<>();
            pageResultDTO.setCode(OpenApiMsgCodeEnum.s_success.getCode());
            pageResultDTO.setData(data);
            pageResultDTO.setMsg(msg);
            pageResultDTO.setSuccess(success);
            pageResultDTO.setPageNo(pageNo);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotalCount(totalCount);
            return pageResultDTO;
        }else {
            ResultDTO<T> resultDTO = new ResultDTO<>(OpenApiMsgCodeEnum.s_success.getCode(), data, msg);
            resultDTO.setSuccess(success);
            return resultDTO;
        }
    }

    private Object getValue(JSONObject resultBody, String expression) {
        Object value;
        if (StringUtils.isBlank(expression)) {
            value = null;
        }else {
            value = JSONPath.eval(resultBody, expression);
        }
        return value;
    }

    /**
     * 将分页参数数据转换成目标类型（Integer、Long、int、long类型互转）
     *
     * @param value 需要转换的数据
     * @param dataTypeEnum 目标类型
     */
    private Object convertPageParamType(Object value, DataTypeEnum dataTypeEnum) {
        if (value instanceof Integer) {
            if (DataTypeEnum.INTEGER.equals(dataTypeEnum)) {
                return value;
            }
            if (DataTypeEnum.LONG.equals(dataTypeEnum)) {
                return ((Integer)value).longValue();
            }
        }
        if (value instanceof Long) {
            if (DataTypeEnum.INTEGER.equals(dataTypeEnum)) {
                return ((Long)value).intValue();
            }
            if (DataTypeEnum.LONG.equals(dataTypeEnum)) {
                return value;
            }
        }
        return value;
    }

    /**
     * 构建返回Result的code和msg
     *
     * @param resultDTO 返回包装对象
     * @param res       API返回包装对象
     * @param <T>       返回包装对象
     */
    @SuppressWarnings("unchecked")
    private <T> void buildResCodeAndMsg(ResultDTO<T> resultDTO, JSONObject res) {

        Boolean success = res.getBoolean(ThirdAuthConstant.WdAuthParamEnum.RESULT_SUCCESS.getCode());
        Integer resCode = res.getInteger(ThirdAuthConstant.WdAuthParamEnum.RESULT_CODE.getCode());
        String resMsg = res.getString(ThirdAuthConstant.WdAuthParamEnum.RESULT_MSG.getCode());
        Object data = res.get(ThirdAuthConstant.WdAuthParamEnum.RESULT_DATA.getCode());

        resultDTO.setData((T) data);
        if (resultDTO instanceof PageResultDTO) {
            Long totalCount = res.getLong(ThirdAuthConstant.WdAuthParamEnum.RESULT_TOTAL_COUNT.getCode());
            ((PageResultDTO<T>) resultDTO).setTotalCount((ObjectUtil.isNotNull(totalCount) ? totalCount : null));
            //如果目标API不返回分页参数，则使用入参（resultDTO中的分页参数来源于入参）
            ((PageResultDTO<T>) resultDTO).setPageNo(ObjectUtil.isNotEmpty(res.getInteger(ApiConditionDefaultColumnEnum.PAGE_NO.getAssetColumns()))
                    ? res.getInteger(ApiConditionDefaultColumnEnum.PAGE_NO.getAssetColumns()) : ((PageResultDTO<T>) resultDTO).getPageNo());
            ((PageResultDTO<T>) resultDTO).setPageSize(ObjectUtil.isNotEmpty(res.getInteger(ApiConditionDefaultColumnEnum.PAGE_SIZE.getAssetColumns()))
                    ? res.getInteger(ApiConditionDefaultColumnEnum.PAGE_SIZE.getAssetColumns()) : ((PageResultDTO<T>) resultDTO).getPageSize());
        }
        if (!success) {
            resultDTO.setSuccess(Boolean.FALSE);
            resultDTO.setCode(OpenApiMsgCodeEnum.s_api_failed_to_execute.getCode());
            resultDTO.setMsg(String.format(OpenApiMsgCodeEnum.s_api_failed_to_execute.getDesc(), resCode, resMsg));
        }

    }

    private boolean checkIsWdResponse(JSONObject res) {
        Set<String> keys = res.keySet();
        return keys.containsAll(Arrays.asList(ThirdAuthConstant.WdAuthParamEnum.RESULT_CODE.getCode()
                , ThirdAuthConstant.WdAuthParamEnum.RESULT_MSG.getCode()
                , ThirdAuthConstant.WdAuthParamEnum.RESULT_SUCCESS.getCode()));
    }

    @Override
    public <T> T process(JSONObject params) {
        return (T) new HttpExternalApiInvokeStrategy(
                externalApiInvokeService,
                dataAssetApiPo.getApiType(),
                dataAssetApiPo,
                appAccessInfo.getDataAccessAppId(),
                params,
                accessRuleFields
        ).invoke();
    }

    @Override
    public <T> T responseBodyHandle(T result) {
        return result;
    }

    @Override
    public List<ApiRespParamDTO> responseHeadHandle(Integer apiId) {
        return openApiDataCache.getApiResponseParams(apiId);
    }
}
