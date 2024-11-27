package com.wakedata.dw.lowcode.service;

import com.wakedata.dw.lowcode.model.LowCodeConfigPo;
import com.wakedata.dw.open.service.BaseDbService;

/**
 * 配置信息 - service
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
public interface LowCodeConfigService extends BaseDbService<LowCodeConfigPo> {

    /**
     * 保存配置
     *
     * @param lowCodeConfigPo 配置
     * @return 主键
     */
    Integer save(LowCodeConfigPo lowCodeConfigPo);


    /**
     * 根据应用id和类型查找配置
     *
     * @param appId 应用id
     * @param type  类型
     * @return 配置
     */
    LowCodeConfigPo findByAppIdType(Integer appId, String type);

}
