package com.wakedata.dw.open.service.impl.event;

import com.wakedata.dw.open.mapper.event.SubscribeRecordMapper;
import com.wakedata.dw.open.model.event.SubscribeRecordPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.event.SubscribeRecordService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubscribeRecordServiceImpl extends BaseServiceImpl<SubscribeRecordPo, SubscribeRecordMapper> implements SubscribeRecordService {

    @Autowired
    @Override
    protected void init(CurdService<SubscribeRecordPo> curdService, SubscribeRecordMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public Integer add(SubscribeRecordVo subscribeRecordVo) {
        SubscribeRecordPo subscribeRecordPo = new SubscribeRecordPo();
        BeanUtils.copyProperties(subscribeRecordVo, subscribeRecordPo);
        Integer result = add(subscribeRecordPo);
        return result;
    }

    @Override
    public Boolean updateById(SubscribeRecordVo subscribeRecordVo) {
        SubscribeRecordPo subscribeRecordPo = new SubscribeRecordPo();
        BeanUtils.copyProperties(subscribeRecordVo, subscribeRecordPo);
        if (subscribeRecordPo != null && subscribeRecordPo.getId() != null) {
            return update(subscribeRecordPo) > 0 ? true : false;
        }
        return false;
    }

}