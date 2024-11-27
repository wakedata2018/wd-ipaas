package com.wakedata.dw.open.service.event;

import com.wakedata.dw.open.model.event.SubscribeRecordPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.vo.event.SubscribeRecordVo;

public interface SubscribeRecordService extends BaseDbService<SubscribeRecordPo> {

    /**
     * 添加
     *
     * @param subscribeAddressVo
     * @return
     */
    Integer add(SubscribeRecordVo subscribeAddressVo);

    Boolean updateById(SubscribeRecordVo subscribeAddressVo);

}