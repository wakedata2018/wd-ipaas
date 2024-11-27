package com.wakedata.dw.open.mapper.event;

import com.wakedata.dw.open.model.event.TopicPo;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;


public interface TopicMapper extends Mapper<TopicPo>, SelectByIdListMapper<TopicPo, Integer> {
}
