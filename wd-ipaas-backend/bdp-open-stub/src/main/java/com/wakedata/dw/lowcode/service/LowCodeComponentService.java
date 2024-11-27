package com.wakedata.dw.lowcode.service;

import com.wakedata.dw.lowcode.model.LowCodeComponentPo;
import com.wakedata.dw.open.service.BaseDbService;
import java.util.List;

/**
 * @author wanghu@wakedata.com
 * @title 低代码组件
 * @date 2021/11/24
 * @since v1.0.0
 */
public interface LowCodeComponentService extends BaseDbService<LowCodeComponentPo>, LowCodeBatchService {

    /**
     * 是否存在组件名称
     *
     * @param name  标识符（名称）
     * @param id    主键
     * @param appId 应用id
     * @return 存在返回true
     */
    Boolean existsComponentName(String name, Integer id, Integer appId);

    /**
     * 组件列表
     *
     * @param appId 应用id
     * @return 组件
     */
    List<LowCodeComponentPo> findByAppId(Integer appId);

}
