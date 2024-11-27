package com.wakedata.dw.open.mapper.event;

import com.wakedata.dw.open.model.event.SubscribeRecordPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;


public interface SubscribeRecordMapper extends Mapper<SubscribeRecordPo>, SelectByIdListMapper<SubscribeRecordPo, Integer> {

    /**
     * 获取除当前api外使用topic的数量
     * @param topicId
     * @param apiId
     * @return
     */
    Integer getUseTopicCountExceptApiId(@Param("topicId") Integer topicId, @Param("apiId") Integer apiId);

    /**
     * 根据apiId更新订阅记录id
     * @param apiId
     * @param subscribeRecordFromEventCenter
     * @return
     */
    Integer updateByApiId(@Param("apiId") Integer apiId, @Param("subscribeRecordFromEventCenter") String subscribeRecordFromEventCenter);
}
