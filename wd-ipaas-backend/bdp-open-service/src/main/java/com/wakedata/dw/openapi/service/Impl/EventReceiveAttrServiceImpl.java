package com.wakedata.dw.openapi.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.mq.enums.RequestProtocolEnum;
import com.wakedata.common.mq.model.consumer.MakeSubscribeBody;
import com.wakedata.common.mq.model.consumer.Message;
import com.wakedata.common.mq.service.consumer.AbstractConsumerService;
import com.wakedata.dw.open.accesstoken.AccessToken;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.EventConstant;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.mapper.api.attr.EventReceiveApiMapper;
import com.wakedata.dw.open.mapper.event.EventReceiveRecordPoMapper;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.model.api.event.EventReceiveRecordPo;
import com.wakedata.dw.open.service.accesstoken.AccessTokenService;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.RestTemplateUtil;
import com.wakedata.dw.openapi.service.EventReceiveAttrService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 接收算子业务接口实现类
 *
 * @author wujunqiang
 * @since 2022/10/22 09:51
 */
@Slf4j
@Service
@DependsOn("globalApplicationContext")
public class EventReceiveAttrServiceImpl implements EventReceiveAttrService {

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Resource
    private AccessTokenService accessTokenService;

    @Resource
    private DataAssetApiMapper dataAssetApiMapper;

    @Resource
    private EventReceiveApiMapper eventReceiveApiMapper;

    @Resource
    private EventReceiveRecordPoMapper eventReceiveRecordPoMapper;

    /**
     * QUEUE_ID分隔符
     */
    private static final String QUEUE_ID_SEPARATOR = "_";

    /**
     * QUEUE_ID根据_拆分成数组后，dw_open_api_event_receive_attr.id所在数组的下标位置
     */
    private static final int QUEUE_ID_RECEIVE_ID_INDEX = 1;

    /**
     * QUEUE_ID根据_拆分成数组后，API ID所在数组的下标位置
     */
    private static final int QUEUE_ID_API_ID_INDEX = 2;

    /**
     * QUEUE_ID模版
     */
    private static final String QUEUE_ID_TEMPLATE = "QUEUE_%s_%s";

    /**
     * ApiId
     */
    private static final String EVENT_INVOKE_SOURCE_VALUE = "apiId为%s的事件接受算子";

    /**
     * 默认超时时间
     */
    private static final int REQUEST_TIME_OUT = 30000;

    /**
     * 服务初始化时，动态创建订阅者
     */
    @PostConstruct
    public void initCreateSubscriber() {
        List<EventReceiveApiAttr> eventReceiveApiAttrs = eventReceiveApiMapper.selectAll();
        if (CollectionUtil.isEmpty(eventReceiveApiAttrs)) {
            log.info("No event receive operator exists");
            return;
        }
        // 根据接收算子的API集合获取对应API的发布状态，如果API发布才创建订阅者
        List<Integer> apiIds = eventReceiveApiAttrs.stream().map(EventReceiveApiAttr::getApiId).collect(Collectors.toList());
        Example example = new Example(DataAssetApiPo.class);
        example.createCriteria().andIn("dataAssetApiId", apiIds);
        List<DataAssetApiPo> dataAssetApiPos = dataAssetApiMapper.selectByExample(example);
        Map<Integer, DataAssetPublishStatusEnum> apiPublishStatusMap = dataAssetApiPos.stream()
                .collect(Collectors.toMap(DataAssetApiPo::getDataAssetApiId, DataAssetApiPo::getDataAssetPublishStatus, (oldValue, newValue) -> newValue));
        // 初始化注册消息消费者
        for (EventReceiveApiAttr eventReceiveApiAttr : eventReceiveApiAttrs) {
            Integer apiId = eventReceiveApiAttr.getApiId();
            if (DataAssetPublishStatusEnum.PUBLISH == apiPublishStatusMap.get(apiId)) {
                try {
                    addSubscribe(eventReceiveApiAttr);
                } catch (Exception e) {
                    // 消息订阅者注册失败打印日志，不影响服务启动
                    log.warn("addSubscribe Fail, event receive id: {}, api id: {}", eventReceiveApiAttr.getId(), apiId);
                }
            }
        }
    }

    /**
     * 动态注册订阅者
     *
     * @param eventReceiveId 事件接收算子id
     */
    @Override
    public void onlineAddSubscribe(Integer eventReceiveId) {
        EventReceiveApiAttr eventReceiveApiAttr = eventReceiveApiMapper.selectByPrimaryKey(eventReceiveId);
        if (eventReceiveApiAttr != null) {
            addSubscribe(eventReceiveApiAttr);
        }
    }

    /**
     * 动态注销订阅者
     *
     * @param id dw_open_api_event_receive_attr.id
     */
    @Override
    public void deleteSubscribe(Integer id) {
        EventReceiveApiAttr eventReceiveApiAttr = eventReceiveApiMapper.selectByPrimaryKey(id);
        if (eventReceiveApiAttr != null) {
            String queueId = buildQueueId(eventReceiveApiAttr.getId(), eventReceiveApiAttr.getApiId());
            String consumerKey = AbstractConsumerService.generateConsumerKey(queueId, eventReceiveApiAttr.getClusterAddress(), eventReceiveApiAttr.getTopic(), eventReceiveApiAttr.getTag());
            RequestProtocolEnum protocol = RequestProtocolEnum.covert(eventReceiveApiAttr.getMqType());
            protocol.consumer().deleteSubscribe(consumerKey);
        }
    }

    /**
     * 添加订阅者
     *
     * @param eventReceiveApiAttr EventReceiveApiAttr
     */
    private void addSubscribe(EventReceiveApiAttr eventReceiveApiAttr) {
        RequestProtocolEnum protocol = RequestProtocolEnum.covert(eventReceiveApiAttr.getMqType());
        MakeSubscribeBody subscribeBody = MakeSubscribeBody.builder()
                .queueId(buildQueueId(eventReceiveApiAttr.getId(), eventReceiveApiAttr.getApiId()))
                .protocol(protocol)
                .bootstrapServers(eventReceiveApiAttr.getClusterAddress())
                .topic(eventReceiveApiAttr.getTopic())
                .tag(eventReceiveApiAttr.getTag())
                .build();
        String accessKey = eventReceiveApiAttr.getAccessKey();
        String secretKey = eventReceiveApiAttr.getSecretKey();
        if (RequestProtocolEnum.ROCKET_MQ == protocol) {
            subscribeBody.setRocketMqAccessKey(accessKey);
            subscribeBody.setRocketMqSecretKey(secretKey);
        }
        if (RequestProtocolEnum.KAFKA == protocol) {
            subscribeBody.setRequestTimeout(REQUEST_TIME_OUT);
            if (StringUtils.isNotBlank(accessKey) || StringUtils.isNotBlank(secretKey)) {
                subscribeBody.setSecurityProtocol(EventConstant.KAFKA_SECURITY_PROTOCOL);
                subscribeBody.setSaslMechanism(EventConstant.KAFKA_SASL_MECHANISM);
                subscribeBody.setSaslJaasConfig(String.format(EventConstant.KAFKA_SASL_JAAS_CONFIG, accessKey, secretKey));
            }
        }
        protocol.consumer().addSubscribe(subscribeBody, this.createConsumer());
    }

    /**
     * 构建queueId
     *
     * @param id    dw_open_api_event_receive_attr表id
     * @param apiId dw_open_api表id
     * @return queueId
     */
    private String buildQueueId(Integer id, Integer apiId) {
        return String.format(QUEUE_ID_TEMPLATE, id, apiId);
    }

    /**
     * 创建业务消费者
     */
    private Consumer<Message> createConsumer() {
        return message -> {
            String messageContent = message.getValue();
            EventReceiveRecordPo eventReceiveRecordPo = new EventReceiveRecordPo();
            eventReceiveRecordPo.setMessage(messageContent);
            try {
                // 根据groupId找到对应的API ID
                String groupId = message.getGroupId();
                String[] groupIdSplit = groupId.split(QUEUE_ID_SEPARATOR);
                Integer apiId = Integer.valueOf(groupIdSplit[QUEUE_ID_API_ID_INDEX]);
                DataAssetApiPo dataAssetApiPo = dataAssetApiMapper.selectByPrimaryKey(apiId);
                if (dataAssetApiPo != null) {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    AccessToken accessToken = accessTokenService.generate(timestamp);
                    String responseBody;
                    JSONObject params = JSON.parseObject(messageContent);
                    String operatorName = findEventReceiveOperatorName(Integer.valueOf(groupIdSplit[QUEUE_ID_RECEIVE_ID_INDEX]));
                    if (DataAssetEnums.ReqMethod.GET == dataAssetApiPo.getReqMethod()) {
                        // GET请求需要额外封装一个参数包含在eventReceiveParams参数里面
                        JSONObject operatorParam = new JSONObject();
                        operatorParam.put(operatorName, JSON.parseObject(messageContent));
                        params.put(DwOpenConstant.EVENT_RECEIVE_QUERY_PARAM_NAME, URLEncoder.encode(JSONObject.toJSONString(operatorParam), "UTF-8"));
                        params.put(DwOpenConstant.API_INVOKE_SOURCE_PARAM_NAME, buildInvokeSourceValue(dataAssetApiPo.getDataAssetApiId()));
                        String requestUrl = restTemplateUtil.buildRequestUrl(dataAssetApiPo.getDataAssetApiMethod(), accessToken.getAccessToken(), timestamp, params);
                        buildEventReceiveRecordPo(eventReceiveRecordPo, apiId, requestUrl, dataAssetApiPo.getLesseeId());
                        eventReceiveRecordPoMapper.insert(eventReceiveRecordPo);
                        responseBody = restTemplateUtil.get(requestUrl, new HashMap<>(16), RequestParamUtils.getParams(params));
                    } else {
                        // 请求参数中放入接收算子步骤英文名的参数，方便接收算子运行时取值放入上下文中
                        params.put(operatorName, messageContent);
                        JSONObject sourceParam = new JSONObject();
                        sourceParam.put(DwOpenConstant.API_INVOKE_SOURCE_PARAM_NAME, buildInvokeSourceValue(dataAssetApiPo.getDataAssetApiId()));
                        String requestUrl = restTemplateUtil.buildRequestUrl(dataAssetApiPo.getDataAssetApiMethod(), accessToken.getAccessToken(), timestamp, sourceParam);
                        buildEventReceiveRecordPo(eventReceiveRecordPo, apiId, requestUrl, dataAssetApiPo.getLesseeId());
                        eventReceiveRecordPoMapper.insert(eventReceiveRecordPo);
                        responseBody = restTemplateUtil.postByJson(requestUrl, new HashMap<>(16), JSONObject.toJSONString(params));
                    }
                    eventReceiveRecordPo.setResponseBody(responseBody);
                    eventReceiveRecordPo.setExecuteStatus(ExecuteStatusEnum.SUCCESS.getCode());
                    eventReceiveRecordPoMapper.updateByPrimaryKey(eventReceiveRecordPo);
                } else {
                    log.warn("notfound api, api id: {}", apiId);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                eventReceiveRecordPo.setExceptionMessage(e.toString());
                eventReceiveRecordPo.setExecuteStatus(ExecuteStatusEnum.EXCEPTION.getCode());
                if (eventReceiveRecordPo.getId() == null) {
                    eventReceiveRecordPoMapper.insert(eventReceiveRecordPo);
                } else {
                    eventReceiveRecordPoMapper.updateByPrimaryKey(eventReceiveRecordPo);
                }
            }
        };
    }

    /**
     * 构建EventReceiveRecordPo参数
     *
     * @param eventReceiveRecordPo EventReceiveRecordPo
     * @param dataAssetApiId       API ID
     * @param requestUrl           请求服务编排URL
     * @param lesseeId             租户ID
     */
    private void buildEventReceiveRecordPo(EventReceiveRecordPo eventReceiveRecordPo, Integer dataAssetApiId,
                                           String requestUrl, Long lesseeId) {
        eventReceiveRecordPo.setDataAssetApiId(dataAssetApiId);
        eventReceiveRecordPo.setRequestUrl(requestUrl);
        eventReceiveRecordPo.setLesseeId(lesseeId);
        Date now = new Date();
        eventReceiveRecordPo.setCreateTime(now);
        eventReceiveRecordPo.setUpdateTime(now);
    }

    /**
     * 根据id查询接收算子配置算子步骤英文名
     *
     * @param id dw_open_api_event_receive_attr表id
     * @return 算子步骤英文名
     */
    private String findEventReceiveOperatorName(Integer id) {
        EventReceiveApiAttr eventReceiveApiAttr = eventReceiveApiMapper.selectByPrimaryKey(id);
        if (eventReceiveApiAttr == null) {
            throw new OpenException(String.format(MsgCodeEnum.w_event_receive_is_null.getDesc(), id));
        }
        return eventReceiveApiAttr.getOperatorName();
    }

    /**
     * 调用来源(事件算子所在的API的ID)
     */
    private String buildInvokeSourceValue(Integer dataAssetApiId){
        String value = String.format(EVENT_INVOKE_SOURCE_VALUE,dataAssetApiId);
        return ApiInvokeSource.API_EVENT_RECEIVE.getValue().concat(":").concat(value);
    }

}
