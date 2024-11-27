package com.wakedata.dw.open.service.event;

import com.wakedata.dw.open.model.event.TopicPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.vo.event.TopicVo;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;


public interface TopicService extends SelectByIdListMapper<TopicPo, Integer>, BaseDbService<TopicPo> {

    TopicVo detail(Integer id);

}