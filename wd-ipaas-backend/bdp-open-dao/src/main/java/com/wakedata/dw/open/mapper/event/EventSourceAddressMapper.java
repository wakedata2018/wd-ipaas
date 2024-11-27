package com.wakedata.dw.open.mapper.event;

import com.wakedata.dw.open.model.event.EventSourceAddressPo;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;


public interface EventSourceAddressMapper extends Mapper<EventSourceAddressPo>, SelectByIdListMapper<EventSourceAddressPo, Integer> {
}
