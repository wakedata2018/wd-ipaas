package com.wakedata.dw.open.service.impl.event;

import com.wakedata.dw.open.mapper.event.TopicMapper;
import com.wakedata.dw.open.model.event.TopicPo;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.event.TopicService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.vo.event.TopicVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author kuangjing
 */
@Slf4j
@Service
public class TopicServiceImpl extends BaseServiceImpl<TopicPo, TopicMapper> implements TopicService {

    @Autowired
    private BatchCurdService<TopicPo> batchCurdService;

    @Autowired
    @Override
    protected void init(CurdService<TopicPo> curdService, TopicMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public TopicVo detail(Integer id) {
        TopicPo topicPo = show(id);
        TopicVo topicVo = new TopicVo();
        BeanUtils.copyProperties(topicPo, topicVo);
        return topicVo;
    }

    @Override
    public List<TopicPo> selectByIdList(List<Integer> list) {
        return this.batchCurdService.selectByIdList(list, this.getMapper());
    }
}