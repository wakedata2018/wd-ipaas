package com.wakedata.dw.open.service;

import com.google.common.collect.Lists;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.ActiveStateEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.liteflow.DAGTaskEngine;
import com.wakedata.dw.open.liteflow.DataAssetApiLiteflowService;
import com.wakedata.dw.open.liteflow.utils.LiteFlowUtils;
import com.wakedata.dw.open.mapper.api.ApiFlowRelationMapper;
import com.wakedata.dw.open.mapper.api.attr.EventReceiveApiMapper;
import com.wakedata.dw.open.mapper.api.attr.EventSendApiMapper;
import com.wakedata.dw.open.mapper.api.attr.RedisLockConfigAttrMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.RedisLockConfigAttr;
import com.wakedata.dw.open.model.api.dto.ApiInfoDTO;
import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowRelation;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiComponent;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
import com.wakedata.dw.open.model.api.flow.operator.api.PublicKind;
import com.wakedata.dw.open.model.api.flow.operator.event.EventReceiveComponent;
import com.wakedata.dw.open.model.api.flow.operator.event.EventReceiveOperator;
import com.wakedata.dw.open.model.api.flow.operator.event.EventSendOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlComponent;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlOperator;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.impl.api.attr.RedisLockConfigAttrService;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;
import com.wakedata.dw.openapi.service.EventReceiveAttrService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * api流程编排实现
 * @author luomeng
 * @date 2022/12/14 10:47
 */
@Slf4j
public class DataAssetApiLiteflowServiceImpl implements DataAssetApiLiteflowService {

    @Resource
    private EventReceiveAttrService eventReceiveAttrService;

    @Resource
    private RedisLockConfigAttrService redisLockConfigAttrService;

    @Resource
    private RedisLockConfigAttrMapper redisLockConfigAttrMapper;

    @Resource
    private DataAssetApiService dataAssetApiService;

    @Resource
    private EventSendApiMapper eventSendApiMapper;

    @Resource
    private EventReceiveApiMapper eventReceiveApiMapper;

    @Resource
    private ApiFlowRelationMapper apiFlowRelationMapper;

    @Override
    public List<Integer> getApiLiteflowRelationApiAndHandle(ApiFlowAttr apiFlowAttr) {
        List<Integer> apiIds = Lists.newArrayList();

        DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);
        for (Map.Entry<String, DAGTaskEngine.OperatorContext> mapEntry : operatorContainer.getFullOperators().entrySet()) {
            DAGTaskEngine.OperatorContext operatorContext = mapEntry.getValue();
            Operator operator = operatorContext.getOperator();
            if (operator instanceof ApiOperator) {
                ApiOperator apiOperator = (ApiOperator) operator;
                ApiComponent component = apiOperator.getComponent();
                if (component.getPublicKind() != PublicKind.share) {
                    continue;
                }
                DataAssetApiPo internal = component.getDataAssetApi();
                apiIds.add(internal.getDataAssetApiId());
            }
            // 如果算子为事件接收算子，获取事件接收算子配置注册为消息消费者
            if (operator instanceof EventReceiveOperator) {
                EventReceiveComponent component = ((EventReceiveOperator) operator).getComponent();
                EventReceiveApiAttr apiAttr = (EventReceiveApiAttr) component.getDataAssetApi().getApiAttr();
                Integer receiveApiId = apiAttr.getId();
                eventReceiveAttrService.deleteSubscribe(receiveApiId);
                eventReceiveAttrService.onlineAddSubscribe(receiveApiId);
            }
        }
        return apiIds;

    }

    @Override
    public List<ApiConditionPo> getApiSinkOperatorsResultParams(ApiFlowAttr apiFlowAttr,Integer dataAssetApiId) {
        List<ApiConditionPo> resultParams = new ArrayList<>();
        DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);
        List<Operator> sinkOperators = operatorContainer.filterSinkOperator();
        ApiConditionPo apiParam = null;
        for (Operator sinkOperator : sinkOperators) {
            apiParam = ApiConditionPo.build(
                    dataAssetApiId,
                    sinkOperator.getName(),
                    sinkOperator.getDesc(),
                    null,
                    null,
                    DataAssetEnums.FiledTypeEnums.RESULT,
                    null,
                    false,
                    false
            );
            resultParams.add(apiParam);
        }
        return resultParams;
    }

    @Override
    public List<ApiInfoDTO> getApiOperatorInfo(ApiFlowAttr apiFlowAttr) {
        List<ApiInfoDTO> apiInfoDTOList = new ArrayList<>();
        List<ApiOperator> sharedApiOperators = getApiOperators(apiFlowAttr);
        for (ApiOperator sharedApiOperator : sharedApiOperators) {
            ApiComponent apiComponent = sharedApiOperator.getComponent();
            Optional.ofNullable(apiComponent).ifPresent(component -> {
                String name = component.getName();
                DataAssetApiPo dataAssetApi = component.getDataAssetApi();
                Optional.ofNullable(dataAssetApi).ifPresent(api -> apiInfoDTOList.add(new ApiInfoDTO(api.getDataAssetApiId(), name)));
            });
        }
        return apiInfoDTOList;
    }

    @Override
    public ApiFlowAttr fillApiOperatorInfo(ApiFlowAttr apiFlowAttr) {
        // 更新工作空间API信息、用于解决服务编排与外部API信息不一致问题
        List<ApiOperator> sharedApiOperators = getApiOperators(apiFlowAttr);

        for (ApiOperator sharedApiOperator : sharedApiOperators) {
            ApiComponent component = sharedApiOperator.getComponent();
            ApiDetailVo apiDetailVo = dataAssetApiService.detailVo(component.getDataAssetApi().getDataAssetApiId(), component.getParameters());
            if (apiDetailVo == null) {
                log.warn("api operator[{}] should not null!", component.getDataAssetApi().getDataAssetApiId());
                continue;
            }
            component.setDataAssetApi(apiDetailVo.getDataAssetApi());
            component.setParameters(apiDetailVo.getParameters());
            component.setResults(apiDetailVo.getResults());
        }
        return apiFlowAttr;
    }

    @Override
    public void addOperatorRelationApi(ApiFlowAttr apiFlowAttr, ApiFlowRelation apiFlowRelation) {
        for (Map.Entry<String, Operator> mapEntry : apiFlowAttr.getOperators().entrySet()) {
            Operator operator = mapEntry.getValue();
            if (!(operator instanceof ApiOperator)) {
                continue;
            }

            ApiOperator apiOperator = (ApiOperator) operator;

            ApiComponent component = apiOperator.getComponent();
            if (component.getPublicKind() == PublicKind.self) {
                continue;
            }

            apiFlowRelation.setApiId(component.getDataAssetApi().getDataAssetApiId());
            apiFlowRelation.setId(null);
            apiFlowRelationMapper.insert(apiFlowRelation);
        }
    }

    @Override
    public JSONObject generateApiLiteflowChain(ApiFlowAttr apiFlowAttr) {
        DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);
        return LiteFlowUtils.parseJsonLiteFlow(
                apiFlowAttr.getApiId(), operatorContainer.getFullOperators());
    }

    /**
     * 根据传入的ApiFlowAttr获取ApiOperator集合
     *
     * @param apiFlowAttr ApiFlowAttr
     * @return ApiOperator集合
     */
    private List<ApiOperator> getApiOperators(ApiFlowAttr apiFlowAttr) {
        return apiFlowAttr.getOperators().values()
                .stream()
                .filter(operator -> operator instanceof ApiOperator)
                .map(operator -> ((ApiOperator) operator))
                .filter(apiOperator -> apiOperator.getComponent().getPublicKind() == PublicKind.share)
                .collect(Collectors.toList());
    }

    @Override
    public void updateInternalApiOperatorIfNecessary(SubscribeRecordVo subscribeRecordVo, ApiFlowAttr apiAttr, String inCharge) {
        List<String> eventSendConfigList = new ArrayList<>();
        List<Integer> eventReceiveIdList = new ArrayList<>();
        List<Integer> redisLockConfigIdList = new ArrayList<>();
        Integer apiId = apiAttr.getApiId();
        for (Map.Entry<String, Operator> mapEntry : apiAttr.getOperators().entrySet()) {
            Operator operator = mapEntry.getValue();
            if (operator instanceof EventSendOperator || operator instanceof EventReceiveOperator) {
                ApiComponent apiComponent = operator instanceof EventSendOperator ? ((EventSendOperator) operator).getComponent() : ((EventReceiveOperator) operator).getComponent();
                DataAssetApiPo dataAssetApi = apiComponent.getDataAssetApi();
                // 如果API已经发布则跳过
                if (DataAssetPublishStatusEnum.PUBLISH.equals(dataAssetApi.getDataAssetPublishStatus())) {
                    continue;
                }
                if(operator instanceof EventSendOperator){
                    dataAssetApi.setApiType(DataAssetEnums.DataApiType.EVENT_SEND);
                }else{
                    dataAssetApi.setApiType(DataAssetEnums.DataApiType.EVENT_RECEIVE);
                }
                dataAssetApi.setInCharge(inCharge);
                ApiDetailVo internal = getApiDetailVo(apiComponent);
                internal.setSubscribeRecord(subscribeRecordVo);
                //透传apiId，将服务编排api的Id传到发送算子配置信息表中
                internal.getDataAssetApi().setDataAssetApiId(apiId);
                //事件算子步骤名不能为空（包括发送、接收）
                if (StringUtils.isBlank(operator.getName())) {
                    throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_event_operator_name_error);
                }
                internal.setOperatorName(operator.getName());
                dataAssetApiService.updateApi(internal);
                apiComponent.setDataAssetApi(internal.getDataAssetApi());
                apiComponent.setResults(internal.getResults());
                apiComponent.setParameters(internal.getParameters());
                if (operator instanceof EventSendOperator) {
                    eventSendConfigList.add(((EventSendApiAttr) dataAssetApi.getApiAttr()).getConfigName());
                }
                if (operator instanceof EventReceiveOperator) {
                    eventReceiveIdList.add(internal.getDataAssetApi().getApiAttr().getId());
                }
            } else if (operator instanceof SqlOperator) {
                SqlComponent component = (SqlComponent) operator.getComponent();
                RedisLockConfigAttr redisLockConfigAttr = component.getApiAttr();
                if (redisLockConfigAttr == null) {
                    continue;
                }
                if (redisLockConfigAttr.getApiId() == null) {
                    redisLockConfigAttr.setApiId(apiId);
                }
                if (ActiveStateEnum.ACTIVE.getValue().equals(redisLockConfigAttr.getEnableRedisLock())) {
                    dataAssetApiService.checkRedisConfig(redisLockConfigAttr);
                }
                redisLockConfigAttrService.saveOrUpdateApiAttr(redisLockConfigAttr.getApiId(), redisLockConfigAttr);
                redisLockConfigIdList.add(redisLockConfigAttr.getId());
            } else if (operator instanceof TryCatchOperator && StringUtils.isNotBlank(((TryCatchOperator) operator).getParentOperatorId())) {
                throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_try_catch_operator_nesting_is_not_allowed_error);
            }
        }
        deleteEventReceiveApiAttr(apiId, eventReceiveIdList);
        deleteEventSendApiAttr(apiId, eventSendConfigList);
        deleteRedisLockConfigAttr(apiId, redisLockConfigIdList);
    }

    /**
     * 删除更新编排后移除的Redis锁配置
     *
     * @param apiId                 API ID
     * @param redisLockConfigIdList 配置ID集合
     */
    private void deleteRedisLockConfigAttr(Integer apiId, List<Integer> redisLockConfigIdList) {
        if (apiId == null) {
            return;
        }
        Example example = new Example(RedisLockConfigAttr.class);
        example.createCriteria().andEqualTo("apiId", apiId);
        if (CollectionUtil.isEmpty(redisLockConfigIdList)) {
            redisLockConfigAttrMapper.deleteByExample(example);
            return;
        }
        List<RedisLockConfigAttr> redisLockConfigs = redisLockConfigAttrMapper.selectByExample(example);
        for (RedisLockConfigAttr redisLockConfig : redisLockConfigs) {
            Integer id = redisLockConfig.getId();
            if (!redisLockConfigIdList.contains(id)) {
                redisLockConfigAttrMapper.deleteByPrimaryKey(id);
            }
        }
    }

    private void deleteEventReceiveApiAttr(Integer apiId, List<Integer> eventReceiveIdList) {
        if (ObjectUtil.isNotEmpty(apiId) && eventReceiveIdList.size() == 0) {
            Example example = new Example(EventReceiveApiAttr.class);
            example.createCriteria().andEqualTo("apiId", apiId);
            List<EventReceiveApiAttr> eventReceiveApiAttrs = eventReceiveApiMapper.selectByExample(example);
            for (EventReceiveApiAttr eventReceiveApiAttr : eventReceiveApiAttrs) {
                eventSendApiMapper.deleteByPrimaryKey(eventReceiveApiAttr.getId());
                eventReceiveAttrService.deleteSubscribe(eventReceiveApiAttr.getId());
            }
        }
    }

    private void deleteEventSendApiAttr(Integer apiId, List<String> eventSendConfigList) {
        if (ObjectUtil.isNotEmpty(apiId)) {
            Example example = new Example(EventSendApiAttr.class);
            example.createCriteria().andEqualTo("apiId", apiId);
            List<EventSendApiAttr> eventSendApiAttrs = eventSendApiMapper.selectByExample(example);
            for (EventSendApiAttr eventSendApiAttr : eventSendApiAttrs) {
                if (!eventSendConfigList.contains(eventSendApiAttr.getConfigName())) {
                    eventSendApiMapper.deleteByPrimaryKey(eventSendApiAttr.getId());
                }
            }
        }
    }

    private ApiDetailVo getApiDetailVo(ApiComponent apiComponent) {
        ApiDetailVo apiDetailVo = new ApiDetailVo();
        apiDetailVo.setDataAssetApi(apiComponent.getDataAssetApi());
        apiDetailVo.setParameters(apiComponent.getParameters());
        apiDetailVo.setResults(apiComponent.getResults());
        if (DataAssetEnums.DataApiType.EVENT_SEND.equals(apiComponent.getDataAssetApi().getApiType())) {
            apiDetailVo.setOperatorName(apiComponent.getName());
        }
        return apiDetailVo;
    }
}
