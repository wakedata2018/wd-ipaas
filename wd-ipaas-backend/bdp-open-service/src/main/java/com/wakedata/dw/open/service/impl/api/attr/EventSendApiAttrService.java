package com.wakedata.dw.open.service.impl.api.attr;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.attr.EventSendApiMapper;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.service.api.attr.ApiAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author caoshuang
 * @date 2021/10/21
 * @projectName dw-open
 * @description
 */
@Service("EventSendApiAttrService")
@Slf4j
public class EventSendApiAttrService implements ApiAttrService<EventSendApiAttr> {

    @Autowired
    private EventSendApiMapper eventSendApiMapper;

    @Override
    public EventSendApiAttr getApiAttr(Integer dataAssetApiId, DataAssetEnums.DataApiType apiKind) {
        EventSendApiAttr eventSendApiAttr = new EventSendApiAttr();
        eventSendApiAttr.setApiId(dataAssetApiId);
        eventSendApiAttr = eventSendApiMapper.selectOne(eventSendApiAttr);
        return eventSendApiAttr;
    }

    @Override
    public EventSendApiAttr saveOrUpdateApiAttr(Integer dataAssetApiId, EventSendApiAttr externalApi) {
        if (externalApi == null) {
            return externalApi;
        }

        Date now = new Date();
        if (externalApi.getId() == null) {
            externalApi.setApiId(dataAssetApiId);
            externalApi.setCreateTime(now);
            externalApi.setUpdateTime(now);
            eventSendApiMapper.insert(externalApi);
            return externalApi;
        }
        EventSendApiAttr eventSendApiAttr = eventSendApiMapper.selectByPrimaryKey(externalApi.getId());
        if (ObjectUtil.isEmpty(eventSendApiAttr)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_event_send_operator_not_find_error);
        }
        externalApi.setUpdateTime(now);
        eventSendApiMapper.updateByPrimaryKey(externalApi);
        return externalApi;
    }

    public EventSendApiAttr saveOrUpdateApiAttr(Integer dataAssetApiId, EventSendApiAttr externalApi, Long lesseeId, String operatorName) {
        externalApi.setConfigName("private_" + lesseeId + "_" + dataAssetApiId + "_" + operatorName);
        EventSendApiAttr eventSendApiAttr = new EventSendApiAttr();
        eventSendApiAttr.setApiId(dataAssetApiId);
        eventSendApiAttr.setConfigName(externalApi.getConfigName());
        List<EventSendApiAttr> eventSendApiAttrs = eventSendApiMapper.select(eventSendApiAttr);
        //同一个流程编排下，config_name不能重复
        if (CollectionUtil.isNotEmpty(eventSendApiAttrs) && ObjectUtil.isEmpty(externalApi.getId())) {
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_event_send_operator_config_name_error);
        }
        externalApi.setLesseeId(lesseeId);
        return saveOrUpdateApiAttr(dataAssetApiId, externalApi);
    }

    @Override
    public int deleteApiAttrByApiId(Integer dataAssetApiId) {
        if(ObjectUtil.isEmpty(dataAssetApiId)) {
            throw  new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        EventSendApiAttr apiAttr = new EventSendApiAttr();
        apiAttr.setApiId(dataAssetApiId);
        eventSendApiMapper.delete(apiAttr);
        return 1;
    }

}