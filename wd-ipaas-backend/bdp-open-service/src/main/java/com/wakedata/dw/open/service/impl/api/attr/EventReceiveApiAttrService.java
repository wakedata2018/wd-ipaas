package com.wakedata.dw.open.service.impl.api.attr;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.attr.EventReceiveApiMapper;
import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.service.api.attr.ApiAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author caoshuang
 * @date 2021/10/21
 * @projectName dw-open
 * @description
 */
@Service("EventReceiveApiAttrService")
@Slf4j
public class EventReceiveApiAttrService implements ApiAttrService<EventReceiveApiAttr> {

    @Autowired
    private EventReceiveApiMapper eventReceiveApiMapper;

    @Override
    public EventReceiveApiAttr getApiAttr(Integer dataAssetApiId, DataAssetEnums.DataApiType apiKind) {
        EventReceiveApiAttr eventReceiveApiAttr = new EventReceiveApiAttr();
        eventReceiveApiAttr.setApiId(dataAssetApiId);
        eventReceiveApiAttr = eventReceiveApiMapper.selectOne(eventReceiveApiAttr);
        return eventReceiveApiAttr;
    }

    @Override
    public EventReceiveApiAttr saveOrUpdateApiAttr(Integer dataAssetApiId, EventReceiveApiAttr externalApi) {
        if (externalApi == null) {
            return externalApi;
        }

        Date now = new Date();
        if (externalApi.getId() == null) {
            externalApi.setApiId(dataAssetApiId);
            externalApi.setCreateTime(now);
            externalApi.setUpdateTime(now);
            eventReceiveApiMapper.insert(externalApi);
            return externalApi;
        }

        externalApi.setUpdateTime(now);
        eventReceiveApiMapper.updateByPrimaryKey(externalApi);
        return externalApi;
    }

    @Override
    public int deleteApiAttrByApiId(Integer dataAssetApiId) {
        if (ObjectUtil.isEmpty(dataAssetApiId)) {
            throw new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        EventReceiveApiAttr apiAttr = new EventReceiveApiAttr();
        apiAttr.setApiId(dataAssetApiId);
        return eventReceiveApiMapper.delete(apiAttr);
    }

    public EventReceiveApiAttr saveOrUpdateApiAttr(Integer dataAssetApiId, EventReceiveApiAttr externalApi, Long lesseeId, String operatorName) {
        externalApi.setOperatorName(operatorName);
        externalApi.setLesseeId(lesseeId);
        return saveOrUpdateApiAttr(dataAssetApiId, externalApi);
    }

}