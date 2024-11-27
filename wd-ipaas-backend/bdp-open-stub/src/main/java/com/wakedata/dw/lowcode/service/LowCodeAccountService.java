package com.wakedata.dw.lowcode.service;

import com.wakedata.dw.lowcode.model.LowCodeAccountPo;
import com.wakedata.dw.open.service.BaseDbService;
import java.util.List;
import java.util.Set;

/**
 * @author wanghu@wakedata.com
 * @title 低代码应用绑定的账号
 * @date 2021/11/25
 * @since v1.0.0
 */
public interface LowCodeAccountService extends BaseDbService<LowCodeAccountPo> {

    /**
     * 根据appId查询关联的wake账号信息
     *
     * @param appIds 应用id
     * @return 账号列表
     */
    List<LowCodeAccountPo> listByAppIds(Set<Integer> appIds);

}
